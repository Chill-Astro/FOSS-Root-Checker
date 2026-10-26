package foss.chillastro.root.checker

import android.Manifest
import android.app.Application
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.SystemUpdate
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import java.io.File
import androidx.core.content.edit
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import java.security.MessageDigest
import kotlin.time.Duration.Companion.milliseconds

// Require the release APK to match the SHA-256 published in the update metadata.
private const val VERIFY_UPDATE_CHECKSUM = true

internal data class UpdateRelease(val tag: String, val url: String, val sha256: String)

internal fun newerRelease(remote: String, current: String): Boolean {
    fun parts(value: String): List<Long> {
        require(Regex("v?\\d+(\\.\\d+)*").matches(value)) { "The Server Returned an Invalid Version Number!" }
        return value.removePrefix("v").split('.').map { it.toLong() }
    }
    val r = parts(remote)
    val c = parts(current)
    for (i in 0 until maxOf(r.size, c.size)) {
        val comparison = r.getOrElse(i) { 0L }.compareTo(c.getOrElse(i) { 0L })
        if (comparison != 0) return comparison > 0
    }
    return false
}

class OfficialUpdater(application: Application) : AndroidViewModel(application) {
    private val app = application
    private val currentVersion: String = run {
        val info = if (Build.VERSION.SDK_INT >= 33) {
            app.packageManager.getPackageInfo(app.packageName, PackageManager.PackageInfoFlags.of(0))
        } else {
            @Suppress("DEPRECATION")
            app.packageManager.getPackageInfo(app.packageName, 0)
        }
        requireNotNull(info.versionName) { "Installed App Version is Unavailable." }
    }
    private val prefs = app.getSharedPreferences("settings", Context.MODE_PRIVATE)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val manager = app.getSystemService(DownloadManager::class.java)
    var automatic by mutableStateOf(prefs.getBoolean("check_for_updates", true))
        private set
    var busy by mutableStateOf(false)
        private set
    var downloadProgress by mutableStateOf<Float?>(null)
        private set
    var status by mutableStateOf<String?>(null)
        private set
    internal var release by mutableStateOf<UpdateRelease?>(null)
        private set
    var ready by mutableStateOf(false)
        private set
    var messageTitle by mutableStateOf("Update Failed")
        private set
    private var messageText by mutableStateOf<String?>(null)
    var message: String?
        get() = messageText
        set(value) {
            messageTitle = "Update Failed"
            messageText = value
        }
    var showReady by mutableStateOf(true)
    private val verifiedApk = File(app.cacheDir, "updates/official-update.apk")
    private var downloadId = -1L
    private var job: Job? = null
    private var started = false

    fun onLaunch() {
        if (started) return
        started = true
        if (downloadId != -1L) resumeDownload() else if (automatic) check(false)
    }

    init {
        // Resume a consented download after process recreation; verify again before installation.
        downloadId = prefs.getLong("update_download_id", -1L)
        if (downloadId != -1L) {
            val tag = prefs.getString("update_tag", null)
            val url = prefs.getString("update_url", null)
            val hash = prefs.getString("update_hash", null)
            if (tag != null && url != null && hash != null &&
                runCatching { newerRelease(tag, currentVersion) }.getOrDefault(false)) {
                release = UpdateRelease(tag, url, hash)
            } else clearDownload()
        }
    }

    fun updateAutomaticChecks(enabled: Boolean) {
        automatic = enabled
        prefs.edit { putBoolean("check_for_updates", enabled) }
    }

    fun check(manual: Boolean) {
        if (ready) { showReady = true; return }
        if (busy || downloadId != -1L) return
        busy = true
        job = scope.launch {
            try {
                val next = withContext(Dispatchers.IO) {
                    // Local testing URL: http://localhost:8000/assets/data/repo-data.json
                    val connection = URL("https://chill-astro.github.io/assets/data/repo-data.json").openConnection() as HttpsURLConnection
                    connection.connectTimeout = 15000
                    connection.readTimeout = 15000
                    val json = try {
                        require(connection.responseCode == 200) { "Update Server Unavailable." }
                        connection.inputStream.bufferedReader().use { reader ->
                            val text = StringBuilder()
                            val buffer = CharArray(4096)
                            while (true) {
                                val count = reader.read(buffer)
                                if (count < 0) break
                                text.appendRange(buffer, 0, count)
                                require(text.length <= 256 * 1024) { "Invalid Update Response." }
                            }
                            JSONObject(text.toString()).getJSONObject("rootChecker")
                        }
                    } finally { connection.disconnect() }
                    val tag = json.getString("tag")
                    if (!newerRelease(tag, currentVersion)) {
                        if (manual) {
                            withContext(Dispatchers.Main) {
                                message = if (newerRelease(currentVersion, tag)) {
                                    "This is a DEV. BUILD!\n\nApp Version = $currentVersion\nLatest Version = $tag"
                                } else "Root Checker is UP TO DATE!"
                                messageTitle = "Update Check"
                            }
                        }
                        null
                    } else {
                        val url = json.getJSONObject("downloads").getString("apk_official")
                        val uri = url.toUri()
                        require(uri.scheme == "https" && uri.host == "github.com" &&
                            uri.path == "/Chill-Astro/FOSS-Root-Checker/releases/download/$tag/app-official-release.apk") { "Invalid APK Link." }
                        val hash = json.optJSONObject("sha256")?.optString("apk_official").orEmpty()
                        require(!VERIFY_UPDATE_CHECKSUM || Regex("[a-fA-F0-9]{64}").matches(hash)) { "This Release has no valid SHA-256 Checksum!" }
                        UpdateRelease(tag, url, hash)
                    }
                }
                release = next
            } catch (e: CancellationException) { throw e
            } catch (e: Exception) {
                if (manual) message = when (e) {
                    is java.net.UnknownHostException, is java.net.ConnectException, is java.net.SocketTimeoutException -> "No Internet Connection!"
                    else -> e.message ?: "Could Not Check for Updates!"
                }
            } finally { busy = false }
        }
    }

    fun dismissRelease() { if (!busy && !ready) release = null }

    fun download() {
        val next = release ?: return
        if (busy || downloadId != -1L) return
        try {
            val request = DownloadManager.Request(next.url.toUri())
                .setTitle("Root Checker ${next.tag}")
                .setMimeType("application/vnd.android.package-archive")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                    "Root-Checker-${next.tag}-${System.currentTimeMillis()}.apk")
            downloadId = manager.enqueue(request)
            prefs.edit(commit = true) {
                putLong("update_download_id", downloadId)
                putString("update_tag", next.tag)
                putString("update_url", next.url)
                putString("update_hash", next.sha256)
            }
            resumeDownload()
        } catch (_: Exception) { message = "Couldn't start download. Check Storage & Permissions." }
    }

    private fun resumeDownload() {
        val next = release ?: return
        busy = true
        status = "Connecting to the download server..."
        job = scope.launch {
            try {
                while (true) {
                    val progress = withContext(Dispatchers.IO) {
                        manager.query(DownloadManager.Query().setFilterById(downloadId)).use { cursor ->
                            check(cursor.moveToFirst()) { "The Download was Removed. Please Download Again!" }
                            Triple(cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS)),
                                cursor.getLong(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)),
                                cursor.getLong(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)))
                        }
                    }
                    check(progress.first != DownloadManager.STATUS_FAILED) { "Download failed. Check your connection and storage, then try again." }
                    if (progress.first == DownloadManager.STATUS_SUCCESSFUL) break
                    downloadProgress = if (progress.third > 0) (progress.second.toFloat() / progress.third).coerceIn(0f, 1f) else null
                    val downloadedMB = progress.second / 1048576.0
                    status = if (progress.third > 0) {
                        "Downloading APK...\n" + String.format("%.0f%%  •  %.1f MB / %.1f MB", downloadProgress!! * 100, downloadedMB, progress.third / 1048576.0)
                    } else if (progress.second > 0) {
                        "Downloading APK...\n" + String.format("%.1f MB downloaded", downloadedMB)
                    } else "Preparing download..."
                    delay(750.milliseconds)
                }
                downloadProgress = null
                status = if (VERIFY_UPDATE_CHECKSUM) "Verifying SHA-256…" else "Preparing APK…"
                withContext(Dispatchers.IO) {
                    val digest = MessageDigest.getInstance("SHA-256")
                    verifiedApk.parentFile?.mkdirs()
                    android.os.ParcelFileDescriptor.AutoCloseInputStream(manager.openDownloadedFile(downloadId)).use { input ->
                        verifiedApk.outputStream().use { output ->
                            val buffer = ByteArray(64 * 1024)
                            while (true) {
                                ensureActive()
                                val count = input.read(buffer)
                                if (count < 0) break
                                digest.update(buffer, 0, count)
                                output.write(buffer, 0, count)
                            }
                        }
                    }
                    val actual = digest.digest().joinToString("") { "%02x".format(it.toInt() and 255) }
                    check(!VERIFY_UPDATE_CHECKSUM || actual.equals(next.sha256, ignoreCase = true)) { "Checksum Verification Failed. The APK was Deleted." }
                }
                ready = true
                showReady = true
            } catch (e: CancellationException) { throw e
            } catch (e: Exception) {
                manager.remove(downloadId)
                clearDownload()
                message = e.message ?: "Couldn't Prepare the APK. Please Download Again!"
            } finally { busy = false; status = null; downloadProgress = null }
        }
    }

    fun cancelDownload() {
        job?.cancel()
        if (downloadId != -1L) manager.remove(downloadId)
        clearDownload()
        release = null
        busy = false
        status = null
        downloadProgress = null
    }

    private fun clearDownload() {
        downloadId = -1L
        ready = false
        verifiedApk.delete()
        prefs.edit { remove("update_download_id"); remove("update_tag"); remove("update_url"); remove("update_hash") }
    }

    fun install(context: Context) {
        if (!ready) return
        try {
            check(verifiedApk.isFile) { "Verified APK is Unavailable." }
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.updates", verifiedApk)
            context.startActivity(Intent(Intent.ACTION_VIEW).setDataAndType(uri, "application/vnd.android.package-archive")
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION))
        } catch (_: Exception) { message = "Couldn't open the installer. You can retry or open the APK in Downloads!" }
    }

    override fun onCleared() { scope.cancel() }
}

@Composable
private fun updater(): OfficialUpdater {
    val activity = checkNotNull(LocalActivity.current as? ComponentActivity) {
        "The updater requires a ComponentActivity host."
    }
    return remember(activity) { ViewModelProvider(activity)[OfficialUpdater::class.java] }
}

@Composable
private fun UpdateAction(
    text: String,
    icon: ImageVector,
    primary: Boolean = true,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val content: @Composable androidx.compose.foundation.layout.RowScope.() -> Unit = {
        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text(text)
    }
    if (primary) Button(onClick = onClick, shapes = expressiveButtonShapes(), enabled = enabled, modifier = Modifier.fillMaxWidth(), content = content)
    else OutlinedButton(onClick = onClick, shapes = expressiveButtonShapes(), enabled = enabled, modifier = Modifier.fillMaxWidth(), content = content)
}

@Composable
private fun UpdateDialog(
    onDismissRequest: () -> Unit,
    icon: @Composable () -> Unit,
    title: @Composable () -> Unit,
    text: @Composable () -> Unit,
    confirmButton: @Composable () -> Unit,
    dismissButton: (@Composable () -> Unit)? = null
) {
    Dialog(onDismissRequest = onDismissRequest, properties = androidx.compose.ui.window.DialogProperties(dismissOnClickOutside = false)) {
        Surface(
            modifier = Modifier.widthIn(max = 400.dp).fillMaxWidth(),
            shape = MaterialTheme.shapes.extraLargeIncreased,
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()).padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    modifier = Modifier.size(64.dp),
                    shape = foss.chillastro.root.checker.ui.theme.AppShapes.root,
                    color = if (foss.chillastro.root.checker.ui.theme.LocalMonochrome.current) Color.White else MaterialTheme.colorScheme.primaryContainer,
                    contentColor = if (foss.chillastro.root.checker.ui.theme.LocalMonochrome.current) Color.Black else MaterialTheme.colorScheme.onPrimaryContainer
                ) {
                    androidx.compose.foundation.layout.Box(contentAlignment = Alignment.Center) { icon() }
                }
                Spacer(Modifier.height(16.dp))
                ProvideTextStyle(MaterialTheme.typography.headlineSmall.copy(textAlign = TextAlign.Center)) { title() }
                Spacer(Modifier.height(16.dp))
                CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
                    ProvideTextStyle(MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center)) { text() }
                }
                Spacer(Modifier.height(24.dp))
                confirmButton()
                if (dismissButton != null) {
                    Spacer(Modifier.height(8.dp))
                    dismissButton()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun UpdateHost() {
    val model = updater()
    val context = LocalContext.current
    var allowInstallPrompt by remember { mutableStateOf(false) }
    val storagePermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) model.download() else model.message = "Storage permission is needed to save the APK in Downloads. No download was started."
    }
    val installPermission = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (Build.VERSION.SDK_INT < 26 || context.packageManager.canRequestPackageInstalls()) model.install(context)
        else model.message = "Installation permission was not granted. Your APK is still in Downloads."
    }
    LaunchedEffect(Unit) { model.onLaunch() }
    model.message?.let { text ->
        UpdateDialog(
            onDismissRequest = { model.message = null },
            icon = { Icon(if (text.contains("UP TO DATE")) Icons.Rounded.CheckCircle else Icons.Rounded.Info, null) },
            title = { Text(model.messageTitle) },
            text = { Text(text) },
            confirmButton = { UpdateAction("Close", Icons.Rounded.Close) { model.message = null } }
        )
    }
    if (model.status != null) {
        UpdateDialog(
            onDismissRequest = {},
            icon = { Icon(Icons.Rounded.Download, null) },
            title = { Text("Downloading Update") },
            text = {
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(model.status.orEmpty(), modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(12.dp))
                    val progress = model.downloadProgress
                    if (progress != null) LinearWavyProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth())
                    else LinearWavyProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = { UpdateAction("Cancel", Icons.Rounded.Close, onClick = model::cancelDownload) }
        )
    } else if (model.ready && model.showReady) {
        UpdateDialog(
            onDismissRequest = { model.showReady = false },
            icon = { Icon(Icons.Rounded.CheckCircle, null) },
            title = { Text("Update Downloaded!") },
            text = { Text("Root Checker is Ready to Update!\n\nThe APK is saved in Downloads. ✅\n\n" +
                (if (VERIFY_UPDATE_CHECKSUM) " SHA-256 Checksum Verified! ✅" else "") +
                "\n\nHit Install to Continue!") },
            confirmButton = {
                UpdateAction("Install", Icons.Rounded.SystemUpdate) {
                    if (Build.VERSION.SDK_INT >= 26 && !context.packageManager.canRequestPackageInstalls()) allowInstallPrompt = true
                    else model.install(context)
                }
            },
            dismissButton = { UpdateAction("Close", Icons.Rounded.Close, primary = false) { model.showReady = false } }
        )
    } else if (!model.ready) model.release?.let { next ->
        UpdateDialog(
            onDismissRequest = model::dismissRelease,
            icon = { Icon(Icons.Rounded.SystemUpdate, null) },
            title = { Text("Update Available!") },
            text = { Text("${next.tag} is OUT NOW!\n\nThe APK will be saved in Downloads!") },
            confirmButton = {
                UpdateAction("Update", Icons.Rounded.Download, enabled = !model.busy) {
                    if (Build.VERSION.SDK_INT <= 28 && context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                        storagePermission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    else model.download()
                }
            },
            dismissButton = { UpdateAction("Close", Icons.Rounded.Close, primary = false, onClick = model::dismissRelease) }
        )
    }
    if (allowInstallPrompt) {
        UpdateDialog(
            onDismissRequest = { allowInstallPrompt = false },
            icon = { Icon(Icons.Rounded.Settings, null) },
            title = { Text("Allow App Installation") },
            text = { Text("Enable “Allow from this Source” for Root Checker in Android settings.") },
            confirmButton = {
                UpdateAction("Open Settings", Icons.Rounded.Settings) {
                    allowInstallPrompt = false
                    try {
                        if (Build.VERSION.SDK_INT >= 26) {
                            installPermission.launch(Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, "package:${context.packageName}".toUri()))
                        } else model.install(context)
                    } catch (_: Exception) {
                        model.message = "Couldn't Open Installation Settings. Open Android Settings and Allow installation from Root Checker."
                    }
                }
            },
            dismissButton = { UpdateAction("Cancel", Icons.Rounded.Close, primary = false) { allowInstallPrompt = false } }
        )
    }
}

@Composable
fun UpdateSettings(monochrome: Boolean) {
    val model = updater()
    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        modifier = Modifier.fillMaxWidth()
    ) {
        ListItem(
            content = { Text("Check for Updates") },
            leadingContent = { Icon(Icons.Rounded.Refresh, null) },
            trailingContent = { ExpressiveSwitch(checked = model.automatic, onCheckedChange = model::updateAutomaticChecks) },
            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
        )
    }
    Spacer(Modifier.height(16.dp))
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        FilledTonalButton(
            onClick = { model.check(true) },
            enabled = !model.busy,
            modifier = Modifier.width(220.dp).height(56.dp),
            shapes = expressiveButtonShapes(CircleShape),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (monochrome) Color.White else MaterialTheme.colorScheme.secondaryContainer,
                contentColor = if (monochrome) Color.Black else MaterialTheme.colorScheme.onSecondaryContainer,
                disabledContainerColor = if (monochrome) Color.White.copy(alpha = 0.5f) else MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                disabledContentColor = if (monochrome) Color.Black.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.5f)
            )
        ) {
            if (model.busy) {
                AppLoadingIndicator(
                    modifier = Modifier.size(24.dp),
                    color = if (monochrome) Color.Black else MaterialTheme.colorScheme.primary
                )
            } else {
                Icon(Icons.Rounded.Refresh, null, modifier = Modifier.size(20.dp),
                    tint = if (monochrome) Color.Black else MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(8.dp))
                Text("Check for Updates")
            }
        }
    }
}
