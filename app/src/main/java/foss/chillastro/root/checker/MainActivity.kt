@file:OptIn(ExperimentalMaterial3Api::class)
package foss.chillastro.root.checker

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForwardIos
import androidx.compose.material.icons.automirrored.rounded.LibraryBooks
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.rounded.Animation
import androidx.compose.material.icons.rounded.Brush
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.Computer
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.LockOpen
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.ReportProblem
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Tag
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import androidx.core.net.toUri
import foss.chillastro.root.checker.ui.theme.FOSSRootCheckerTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.system.exitProcess

// Getting Hardware Props (for Decoration I mean for Informing!)
object HardwareProbe {
    fun getProp(prop: String): String = try {
        Runtime.getRuntime().exec(arrayOf("getprop", prop)).inputStream.bufferedReader().readLine() ?: ""
    } catch (_: Exception) { "" }
    fun getBootloader(): String = if (getProp("ro.boot.flash.locked") == "0") "Unlocked" else "Locked"
    fun getVerity(): String = getProp("ro.boot.veritymode").ifEmpty { "disabled" }

    fun getTotalRAM(context: Context): Long {
        val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        actManager.getMemoryInfo(memInfo)
        return memInfo.totalMem
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val config = resources.configuration
            requestedOrientation = if (config.smallestScreenWidthDp < 600) {
                android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT // Force Portrait for Phones for BETTER USER EXPERIENCE ( Because they are Casinos for DoomScrolling )
            } else {
                android.content.pm.ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED // Allow Foldables & Tablets to have Landscape as they aren't a 2.15 Ahhh Remote 💀💀💀💀 ( Okay but Foldables are 4.30 Remotes! 🤣 )
            }
            val context = LocalContext.current
            val prefs = remember { context.getSharedPreferences("settings", MODE_PRIVATE) }

            // 0: System, 1: Light, 2: Dark
            var themeMode by remember { mutableIntStateOf(prefs.getInt("theme_mode", 0)) }
            var useDynamic by remember { mutableStateOf(prefs.getBoolean("use_dynamic", true)) }

            val totalRam = remember { HardwareProbe.getTotalRAM(context) }
            val isLowRam = totalRam < 4L * 1000 * 1000 * 1000 // 4000MB of DEDOTATAED RAM!

            var reducedAnimations by remember {
                mutableStateOf(if (isLowRam) true else prefs.getBoolean("reduced_animations", false))
            }

            val isSystemDark = isSystemInDarkTheme()
            val darkTheme = when(themeMode) {
                1 -> false
                2 -> true
                else -> isSystemDark
            }

            FOSSRootCheckerTheme(darkTheme = darkTheme, dynamicColor = useDynamic) {
                CARootChecker(
                    themeMode = themeMode,
                    onThemeChange = {
                        themeMode = it
                        prefs.edit { putInt("theme_mode", it) }
                    },
                    dyn = useDynamic,
                    onDyn = {
                        useDynamic = it
                        prefs.edit { putBoolean("use_dynamic", it) }
                    },
                    reducedAnimations = reducedAnimations,
                    onReducedAnimationsChange = {
                        reducedAnimations = it
                        prefs.edit { putBoolean("reduced_animations", it) }
                    },
                    isLowRam = isLowRam
                )
            }
        }
    }
}
@Composable
fun CARootChecker( // CA stands for Chill-Astro who neither is an Astronaut nor uses Astro.
    themeMode: Int,
    onThemeChange: (Int) -> Unit,
    dyn: Boolean,
    onDyn: (Boolean) -> Unit,
    reducedAnimations: Boolean,
    onReducedAnimationsChange: (Boolean) -> Unit,
    isLowRam: Boolean
) {
    var dest by rememberSaveable { mutableStateOf(AppDestinations.HOME) }
    var showHistorySheet by remember { mutableStateOf(false) }
    var showInfoSheet by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var logs by remember { mutableStateOf(getLogs(context)) }
    val refreshLogs = { logs = getLogs(context) }
    BackHandler(dest != AppDestinations.HOME) { dest = AppDestinations.HOME }
    NavigationSuiteScaffold(
        layoutType = NavigationSuiteType.NavigationBar,
        navigationSuiteItems = {
            AppDestinations.entries.forEach { item ->
                item(
                    icon = {
                        val iconPainter = when (val icon = item.icon) {
                            is ImageVector -> rememberVectorPainter(icon)
                            is Int -> painterResource(id = icon)
                            else -> error("Invalid icon type")
                        }
                        Icon(
                            painter = iconPainter,
                            contentDescription = item.label
                        )
                    },
                    label = { Text(text = item.label) },
                    selected = dest == item,
                    onClick = { dest = item }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(buildAnnotatedString {
                            append("ROOT CHECKER ")
                        }, fontWeight = FontWeight.ExtraBold, fontFamily = FontFamily.Monospace)
                    },
                    navigationIcon = { // Info button
                        IconButton(onClick = { showInfoSheet = true }) {
                            Icon(Icons.Rounded.Info, contentDescription = "Info") // Useful / Useless Facts About your Hardware
                        }
                    },
                    actions = { // History button
                        IconButton(onClick = { showHistorySheet = true }) {
                            Icon(Icons.Rounded.History, contentDescription = "History") // Not the Boring History from School
                        }
                    }
                )
            }
        ) { padding ->
            AnimatedContent(
                targetState = dest,
                modifier = Modifier.padding(padding).fillMaxSize(),
                transitionSpec = {
                    if (reducedAnimations) {
                        (fadeIn(animationSpec = tween(300)) + scaleIn(initialScale = 0.95f)) togetherWith
                                (fadeOut(animationSpec = tween(300)) + scaleOut(targetScale = 0.95f))
                    } else {
                        val spec = spring<IntOffset>(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow)
                        if (targetState.ordinal > initialState.ordinal) {
                            (slideInHorizontally(spec) { it / 2 } + fadeIn() + scaleIn(initialScale = 0.92f)) togetherWith
                                    (slideOutHorizontally(spec) { -it / 2 } + fadeOut() + scaleOut(targetScale = 0.92f))
                        } else {
                            (slideInHorizontally(spec) { -it / 2 } + fadeIn() + scaleIn(initialScale = 0.92f)) togetherWith
                                    (slideOutHorizontally(spec) { it / 2 } + fadeOut() + scaleOut(targetScale = 0.92f))
                        }
                    }
                }, label = "PageTransition"
            ) { target ->
                when (target) {
                    AppDestinations.HOME -> RootChecker(reducedAnimations, onCheckComplete = refreshLogs)
                    AppDestinations.BUSYBOX -> Busybox()
                    AppDestinations.GUIDE -> RootGuide()
                    AppDestinations.SETTINGS -> Settings(
                        themeMode, onThemeChange, dyn, onDyn,
                        reducedAnimations, onReducedAnimationsChange, isLowRam
                    )
                }
            }
        }

        if (showHistorySheet) {
            ModalBottomSheet(
                onDismissRequest = { showHistorySheet = false },
                shape = MaterialTheme.shapes.extraLarge
            ) {
                HistoryContent(logs = logs, onClear = { clearLogs(context); refreshLogs() })
            }
        }
        if (showInfoSheet) {
            ModalBottomSheet(
                onDismissRequest = { showInfoSheet = false },
                shape = MaterialTheme.shapes.extraLarge,
                containerColor = MaterialTheme.colorScheme.background
            ) {
                SystemInfo()
            }
        }
    }
}
@Composable
fun SystemInfo() {
    val scrollState = rememberScrollState()
    val lspData = listOf(
        "/data/adb/lspd", "/system/framework/lsposed.jar",
        "/system/etc/init/hw/init.lsposed.rc", "/data/adb/lspd.db"
    ).map { it to safeExists(it) }
    val rootData = listOf(
        "/system/bin/su", "/system/xbin/su", "/sbin/su", "/data/local/xbin/su",
        "/sbin/su_magisk_arm64", "/system/xbin/magisk", "/data/adb/magisk"
    ).map { it to safeExists(it) }
    val busyBoxData = listOf(
        "/system/bin/busybox", "/system/xbin/busybox", "/vendor/bin/busybox",
        "/sbin/busybox", "/data/local/bin/busybox"
    ).map { it to safeExists(it) }
    val zygiskFound = try {
        java.io.File("/proc/self/mounts").readText().contains("zygisk", ignoreCase = true)
    } catch(_: Exception) { false }
    val seLinux = try {
        val p = getProp("selinux.get", "")
        p.ifBlank {
            if (java.io.File("/sys/fs/selinux/enforce")
                    .exists()
            ) "Enforcing" else "Enforcing (Protected)"
        }
    } catch(_: Exception) { "Enforcing" }
    val debuggable = getProp("ro.debuggable", "0")
    val secure = getProp("ro.secure", "1")
    val isSafe = lspData.none { it.second } &&
            rootData.none { it.second } &&
            busyBoxData.none { it.second } &&
            !zygiskFound &&
            debuggable != "1" &&
            secure != "0"
    Column(
        modifier = Modifier.fillMaxWidth().verticalScroll(scrollState).padding(bottom = 40.dp)
    ) {
        ElevatedCard(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Row(modifier = Modifier.padding(24.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Info, null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = if(isSafe) "Device is Normal" else "Device is Modified",
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
        CategoryGroup("LSPosed Framework", lspData)
        CategoryGroup("Root Binaries", rootData)
        CategoryGroup("BusyBox Traces", busyBoxData)
        SectionLabel("System Security")
        StatRow(label = "SELinux Policy", value = seLinux, ok = !seLinux.contains("Disabled", true))
        StatRow(label = "ro.debuggable", value = debuggable, ok = debuggable != "1")
        StatRow(label = "ro.secure", value = secure, ok = secure != "0")
        PathRow(path = "Zygisk Traces (Mounts)", found = zygiskFound)
    }
}
fun getProp(key: String, default: String): String {
    return try {
        val c = Class.forName("android.os.SystemProperties")
        val get = c.getMethod("get", String::class.java)
        val result = get.invoke(c, key) as String
        if (result.isNullOrBlank()) {
            val process = Runtime.getRuntime().exec("getprop $key")
            val output = process.inputStream.bufferedReader().use { it.readLine() }
            if (output.isNullOrBlank()) "ACCESS DENIED" else output
        } else result
    } catch (_: Exception) {
        "ACCESS DENIED"
    }
}
fun safeExists(path: String): Boolean = try {
    val file = java.io.File(path)
    file.exists() && file.canRead()
} catch (_: Exception) {
    false
}
@Composable
fun RowLayout(ok: Boolean, content: @Composable ColumnScope.() -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
    ) {
        Row(modifier = Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (ok) Icons.Rounded.CheckCircle else Icons.Rounded.Cancel,
                contentDescription = null,
                tint = if (ok) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) { content() }
        }
    }
}
@Composable
fun PathRow(path: String, found: Boolean) {
    RowLayout(ok = !found) {
        Text(
            text = path,
            style = MaterialTheme.typography.bodySmall,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.fillMaxWidth(),
            softWrap = true
        )
    }
}
@Composable
fun StatRow(label: String, value: String, ok: Boolean) {
    RowLayout(ok = ok) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(label, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.weight(1f))
            Text(value, style = MaterialTheme.typography.bodySmall, fontFamily = FontFamily.Monospace, color = if(ok) Color.Unspecified else MaterialTheme.colorScheme.error)
        }
    }
}
@Composable
fun SectionLabel(text: String) {
    Text(text, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 24.dp, vertical = 14.dp), color = MaterialTheme.colorScheme.primary)
}
@Composable
fun CategoryGroup(title: String, data: List<Pair<String, Boolean>>) {
    SectionLabel(title)
    data.forEach { (p, e) -> PathRow(path = p, found = e) }
}
// User INTERFACE (I hope it works like butter on Dumpster Fire Devices!)
@Composable
fun RootChecker(reducedAnimations: Boolean, onCheckComplete: () -> Unit) {
    var checkState by rememberSaveable { mutableIntStateOf(0) }
    var isRooted by rememberSaveable { mutableStateOf(false) }
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val bootloader = remember { HardwareProbe.getBootloader() }
    val verity = remember { HardwareProbe.getVerity() }

    val circleScale by animateFloatAsState(
        targetValue = if (checkState == 1 || checkState == 3) 1.15f else 1f,
        animationSpec = if (reducedAnimations) tween(300) else spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow),
        label = "pulse"
    )
    val isAnyProcessing = checkState == 1 || checkState == 3
    val cornerSpec = if (reducedAnimations) {
        tween<androidx.compose.ui.unit.Dp>(300)
    } else {
        spring(Spring.DampingRatioNoBouncy, Spring.StiffnessMediumLow)
    }
    val innerCornerRadius by animateDpAsState(
        targetValue = if (isAnyProcessing) 32.dp else 4.dp,
        animationSpec = cornerSpec,
        label = "innerCorner"
    )
    Column(
        Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ) {
            Row(
                Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Rounded.Info, null, Modifier.size(20.dp), MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(12.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) { // Info Pill (Good Hardware Props)
                    Text("${Build.MANUFACTURER} ${Build.MODEL} | Android ${Build.VERSION.RELEASE}", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    Text("Bootloader: $bootloader | dm-verity: $verity", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
                }
            }
        }
        Spacer(Modifier.weight(1f))
        Box(contentAlignment = Alignment.Center) {
            if (checkState == 1 || checkState == 3) {
                CircularProgressIndicator(Modifier.size(290.dp), strokeWidth = 6.dp)
            }
            Surface(
                modifier = Modifier.size(220.dp).graphicsLayer(scaleX = circleScale, scaleY = circleScale),
                shape = CircleShape,
                color = when(checkState) {
                    2, 4 -> if (isRooted) Color(0xFF4CAF50) else Color(0xFFB00020)
                    else -> MaterialTheme.colorScheme.primaryContainer
                },
                tonalElevation = 8.dp
            ) {
                Crossfade(
                    targetState = checkState,
                    label = "icon_fade",
                    animationSpec = if (reducedAnimations) tween(300) else spring(stiffness = Spring.StiffnessLow)
                ) { s ->
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        if (s == 2 || s == 4) {
                            Icon(if (isRooted) Icons.Rounded.Check else Icons.Rounded.Close, null, Modifier.size(90.dp), Color.White)
                        } else {
                            Icon(painterResource(id = R.drawable.root_hash), null, Modifier.size(90.dp), MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                    }
                }
            }
        }
        Text(
            text = when(checkState) { // Transparency is Trust
                1 -> "Searching for Paths..."
                2 -> if (isRooted) "Root Traces Found" else "No Root Traces Found"
                3 -> "Interrogating SU Binary..."
                4 -> if (isRooted) "Root Access Verified" else "Root Access not Available"
                else -> "Ready to verify?" // No
            },
            modifier = Modifier.padding(top = 24.dp).animateContentSize(
                animationSpec = if (reducedAnimations) tween(300) else spring()
            ),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.weight(1.2f))
        Row(
            modifier = Modifier.widthIn(max = 700.dp).height(64.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SpringButton(
                onClick = {
                    checkState = 1
                    isRooted = false
                    scope.launch(Dispatchers.IO) {
                        delay(1000)
                        val paths = arrayOf(
                            // A Rabbit Hole of a HELL LOT OF PATHS! (Feels like I am a Security Official tbh.)
                            // Magisk & Stealth Modules
                            "/data/adb/magisk",
                            "/data/adb/magisk.db",
                            "/data/adb/magisk.img",
                            "/data/adb/modules",
                            "/data/adb/magisk/su",
                            "/sbin/.magisk/mirror",
                            "/dev/com.topjohnwu.magisk.daemon",
                            "/cache/magisk.log",
                            "/data/resource-cache/magisk.apk",
                            "/data/adb/post-fs-data.d",
                            "/data/adb/service.d",
                            "/data/adb/env",
                            "/data/adb/shamiko",
                            "/data/adb/tricky_store",
                            "/data/adb/zygisk_next",
                            "/data/adb/riru",
                            // KernelSU & APatch
                            "/data/adb/ksu",
                            "/data/adb/ksu/bin/su",
                            "/proc/kernelsu",
                            "/dev/ksu",
                            "/data/adb/apatch",
                            "/data/adb/apatch/bin/su",
                            "/data/adb/ap/bin/su",
                            "/data/adb/ap/patch",
                            "/dev/apatch",
                            "/sys/kernel/debug/tracing/su",
                            // Others
                            "/system/bin/su",
                            "/system/xbin/su",
                            "/sbin/su",
                            "/system/sd/xbin/su",
                            "/system/bin/failsafe/su",
                            "/data/local/xbin/su",
                            "/data/local/bin/su",
                            "/data/local/su",
                            "/su/bin/su",
                            "/system/sbin/su",
                            "/system/usr/we-need-root/su-backup",
                            "/system/xbin/mu",
                            "/system/bin/.ext/.su",
                            "/system/app/Superuser.apk",
                            "/system/app/SuperSU",
                            "/system/etc/init.d/99SuperSUDaemon",
                            "/dev/com.koushikdutta.superuser.daemon",
                            "/data/data/com.noshufou.android.su",
                            "/system/xbin/busybox",
                            "/system/bin/busybox",
                            "/sbin/busybox",
                            "/vendor/bin/busybox",
                            "/data/local/busybox",
                            "/data/local/xbin/busybox"
                        )
                        val found = paths.any { try { Runtime.getRuntime().exec(arrayOf("ls", it)).waitFor() == 0 } catch (_: Exception) { false } }
                        withContext(Dispatchers.Main) {
                            isRooted = found
                            checkState = 2
                            if (found) {
                                if (bootloader == "Locked") Toast.makeText(ctx, "Root Traces Found. Nice Spoofing! :)", Toast.LENGTH_LONG).show() // What do you call a fake noodle? AN IMPASTA!
                                else Toast.makeText(ctx, "Root Traces Found", Toast.LENGTH_SHORT).show()
                            } else Toast.makeText(ctx, "Root Traces not Found", Toast.LENGTH_SHORT).show()
                            saveLog(ctx, found, "SCAN")
                            onCheckComplete()
                        }
                    }
                },
                modifier = Modifier.weight(1f).height(64.dp),
                shape = RoundedCornerShape(topStart = 32.dp, bottomStart = 32.dp, topEnd = innerCornerRadius, bottomEnd = innerCornerRadius),
                enabled = !isAnyProcessing,
                reducedAnimations = reducedAnimations
            ) {
                Text("Search Root", textAlign = TextAlign.Center)
            }
            Spacer(Modifier.width(4.dp))
            SpringButton(
                onClick = {
                    checkState = 3
                    isRooted = false
                    scope.launch(Dispatchers.IO) {
                        delay(1200)
                        val suWorks = isSUWorking()
                        withContext(Dispatchers.Main) {
                            isRooted = suWorks
                            checkState = 4
                            if (suWorks) {
                                if (bootloader == "Locked") Toast.makeText(ctx, "Root Access Verified. Nice Spoofing! :)", Toast.LENGTH_LONG).show() // Congratulations! Ms. Mobile is Rooted! (WAIT WHAT?)
                                else Toast.makeText(ctx, "Root Access Verified", Toast.LENGTH_SHORT).show() // Congratulations! Ms. Mobile is Rooted!
                            } else Toast.makeText(ctx, "Root Access not Available", Toast.LENGTH_SHORT).show() // Try Better next Time.
                            saveLog(ctx, suWorks, "SU")
                            onCheckComplete()
                        }
                    }
                },
                modifier = Modifier.weight(1f).height(64.dp),
                shape = RoundedCornerShape(topEnd = 32.dp, bottomEnd = 32.dp, topStart = innerCornerRadius, bottomStart = innerCornerRadius),
                enabled = !isAnyProcessing,
                reducedAnimations = reducedAnimations
            ) {
                Text("Verify Root", textAlign = TextAlign.Center)
            }
        }
    }
}
@Composable
fun SpringButton( // Split Button with Split Brain. Wait Callosotomy!
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: androidx.compose.ui.graphics.Shape = ButtonDefaults.shape,
    reducedAnimations: Boolean = false,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.90f else 1f,
        animationSpec = if (reducedAnimations) tween(100) else spring(0.6f, Spring.StiffnessLow),
        label = "button_scale"
    )
    Button(
        onClick = onClick,
        modifier = modifier.graphicsLayer(scaleX = scale, scaleY = scale),
        enabled = enabled,
        shape = shape,
        interactionSource = interactionSource
    ) {
        content()
    }
}

@Composable
fun WarningCard(bodyText: String) { // For newbies
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
        shape = MaterialTheme.shapes.large
    ) {
        Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.Warning, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(32.dp))
            Spacer(Modifier.width(16.dp))
            Column {
                Text("WARNING!", fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onErrorContainer)
                Text(bodyText, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.9f))
            }
        }
    }
}

@Composable
fun Busybox() { // Let's hope She ain't Busy!
    var checkState by rememberSaveable { mutableIntStateOf(0) }
    var foundPath by rememberSaveable { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val ctx = LocalContext.current
    Column(Modifier.fillMaxSize().padding(24.dp)) {
        Spacer(Modifier.height(16.dp))
        Box(Modifier.fillMaxWidth().weight(1f).clip(MaterialTheme.shapes.medium).background(MaterialTheme.colorScheme.surfaceContainerHigh).padding(16.dp).verticalScroll(rememberScrollState())) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TerminalLine("NOTE : Most Modern Root Solutions hide their BusyBox Installation to Avoid Detection!") // Bhari good!
                TerminalLine("Install 'BusyBox for NDK Module' if needed...") // For turning Android into LINUX ig
                HorizontalDivider(Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant)
                TerminalLine("Ready to Verify?") // No
                if (checkState == 2) {
                    TerminalLine("Searching for BusyBox Paths...  ♪(´▽｀)") // La la la la la! La la la la la!
                    if (foundPath.isNotEmpty()) {
                        TerminalLine("BusyBox Path Verified!", MaterialTheme.colorScheme.primary)
                        TerminalLine(foundPath, MaterialTheme.colorScheme.primary)
                        Toast.makeText(ctx, "BusyBox found via Path", Toast.LENGTH_SHORT).show() // Ay did u forgot to hide?
                    }
                    else {
                        TerminalLine("Busybox not Found in Path!", MaterialTheme.colorScheme.error)
                        Toast.makeText(ctx, "BusyBox not Found. Is it Installed?", Toast.LENGTH_SHORT).show()
                        TerminalLine("Fine, but su Never Lies! ^_~") // SU
                        TerminalLine("Launching Shell....")
                        TerminalLine("usr@android $ su")
                        val suPath = findBusyBoxPathBySU()
                        if (suPath.isNotEmpty()) {
                            TerminalLine("root@android $ which busybox")
                            TerminalLine("BusyBox Path Verified!", MaterialTheme.colorScheme.primary)
                            Toast.makeText(ctx, "BusyBox found via Path as Root Nice Spoofing! :)", Toast.LENGTH_SHORT).show() // Ayooo something is wrong with Ms. Mobile!
                            TerminalLine(suPath, MaterialTheme.colorScheme.primary)
                            TerminalLine("root@android $ exit")
                        } else {
                            TerminalLine("Busybox not Found in Path as Root!", MaterialTheme.colorScheme.error)
                            Toast.makeText(ctx, "BusyBox not Installed.", Toast.LENGTH_SHORT).show()
                        }
                        TerminalLine("usr@android $ _") // I always come back
                    }
                }
            }
        }
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = {
                    scope.launch(Dispatchers.IO) {
                        checkState = 1; delay(800)
                        val p = findBusyBoxPath()
                        withContext(Dispatchers.Main) { foundPath = p; checkState = 2 }
                    }
                },
                modifier = Modifier
                    .widthIn(max = 500.dp)
                    .fillMaxWidth()
                    .height(70.dp)
                    .padding(top = 16.dp),
                enabled = checkState != 1,
                shape = MaterialTheme.shapes.large
            ) {
                Text(if (checkState == 1) "Searching..." else "Verify BusyBox Installation")
            }
        }
    }
}
@Composable
fun RootGuide() {
    var menuPath by rememberSaveable { mutableStateOf("MAIN") }
    val slot = remember { HardwareProbe.getProp("ro.boot.slot_suffix").replace("_", "").ifEmpty { "" } }
    val kernelVersion = remember { HardwareProbe.getProp("ro.kernel.version") }
    val isAB = slot.isNotEmpty()
    BackHandler(menuPath != "MAIN") { menuPath = "MAIN" }
    AnimatedContent(
        targetState = menuPath,
        modifier = Modifier.fillMaxSize(),
        transitionSpec = {
            (fadeIn(animationSpec = tween(400, easing = FastOutSlowInEasing)) + scaleIn(initialScale = 0.95f)) togetherWith
                    (fadeOut(animationSpec = tween(400, easing = FastOutSlowInEasing)) + scaleOut(targetScale = 0.95f))
        }, label = "SubMenuTransition"
    ) { targetPath ->
        Column(Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            when (targetPath) {
                // CHILL-ASTRO PRESENTS : A GOATED ROOTING GUIDE 🐐🐐🐐🐐🐐🐐🐐🐐🐐
                "MAIN" -> {
                    WarningCard("Never trust 'One-Click Root' Apps and Please BE CAREFUL while following this guide. I am not responsible for any damages to your device.")
                    NavCard("1. Rooting: An Introduction", Icons.AutoMirrored.Rounded.LibraryBooks) { menuPath = "INTRO" }
                    NavCard("2. Unlocking Bootloader", Icons.Rounded.LockOpen) { menuPath = "UNLOCK" }
                    NavCard("3. Rooting Methods", Icons.Rounded.Tag) { menuPath = "METHODS" }
                    NavCard("4. Root Hiding", Icons.Rounded.VisibilityOff) { menuPath = "HIDING" }
                }
                "INTRO" -> {
                    GuideHeader("Rooting : An Introduction", onBack = { menuPath = "MAIN" })
                    WarningCard("Please BE CAREFUL what apps you are giving Root Permissions to. I am not responsible for Data or Money Theft by Malware on your Device.")
                    InfoBlock("Introduction : What is Rooting?", "\nRooting an Android device means gaining full administrative (superuser) control, similar to an administrator on a computer, by unlocking deep system access restricted by manufacturers.")
                    InfoBlock("Pros of Rooting :", "\n✓ Bloatware Removal\n✓ System-wide Adblocking\n✓ Advanced Theming and Modification ( using LSPosed Framework )\n✓ Full Data Backups\n✓ Unlimited Google Photos Backups\n✓ Unlocking Higher FPS in Games\n✓ Sound Enhancement\n✓ Running FULL BLOWN Linux on Android using chroot\n✓ Battery Longevity (ACC)\n\nAnd Many Others.......")
                    InfoBlock("Cons of Rooting :", "\n✗ Usually Voids Warranty\n✗ Increased Security Risks\n✗ Loss of Hardware Encoding\n✗ No Official Updates (OTA)\n✗ Data loss\n✗ Risk of Bricking Device\n\nNow with that out of the way, let me inform you about some ADDITIONAL STUFF that you WILL FACE during your Modding Journey.")
                    InfoBlock("What is Bootloader?", "\nBootloader is the first piece of software that runs every time you turn on your Android device. It acts as a security guard and a guide, directing the hardware on how to start up and which operating system to \"hand off\" control to. This is locked by default to ensure stability and prevent malware from infecting the device.")
                    InfoBlock("What is Bricking?\n", "Bricking refers to a device becoming completely non-functional, usually due to a corrupted software update or a failed firmware modification.")
                    InfoBlock("Types of Bricking and How to fix them :\n","1. Soft Brick : A soft brick is a \"recoverable\" state. The device might be stuck in a boot loop (constantly restarting at the logo) or booting straight into recovery mode.\n\n- The Cause : Usually a minor software error, incompatible app, or a bad module.\n- The Fix: Can often be fixed by a factory reset, clearing the cache, or reflashing the original firmware using a computer.\n\n2. Hard Brick : A hard brick is much more serious. The device shows no signs of life. No lights, no vibration, and the screen remains black.\n\n- The Cause: This happens when the bootloader (the \"first-stage\" software we discussed earlier) or the kernel is corrupted or deleted.\n- The Fix: This often requires specialized hardware tools to bypass the main software, or in many cases, a physical replacement of the motherboard. Tools suck as SP Flash Tool and MTKClient can do this Work. However FASTBOOT is not Accessible during this time.")
                    InfoBlock("What is Device Mapper Verity (dm-verity)?\n", "Device Mapper Verity is a transparent integrity checking feature of the Linux kernel. Its sole job is to ensure that the data on critical partitions (like /system, /vendor, or /product) has not been modified even by a single bit. This is why it is Sometimes Disabled while Modding.")
                    InfoBlock("How does dm-verity work?\n", "The system creates a \"Hash Tree\" (Merkle Tree).\n\n• It hashes every 4KB block on the partition.\n• It then hashes those hashes.\n• It keeps doing this until only one hash remains at the very top.\n\nThis final single hash is called the Root Hash. This hash is digitally signed by the manufacturer and stored in a read-only area (the VBMeta partition).\n\nWhen the Android wants to read a file:\n\n• The kernel reads the 4KB block from the disk.\n• It calculates the hash of that block.\n• It compares it against the \"parent\" hash in the tree, all the way up to the Root Hash.\n• If the math doesn't match perfectly, it knows the block was tampered with. This is when you get the \"dm-verity corruption\" and \"System is Destroyed\" Warnings.")
                    InfoBlock("Suggestion from My Experience :", "\nAs from my little experience from Rooting, use Magisk if you are not sure. It works on almost every device and it can be flashed with PC and Custom Recovery ( like TWRP or OrangeFox ) and does the job very well. Unless your device is old, DO NOT USE EXPLOITS! I had soft-bricked my own device like this so BE CAREFUL!")
                }
                "UNLOCK" -> {
                    GuideHeader("Unlocking Bootloader", onBack = { menuPath = "MAIN" })
                    WarningCard("This process will wipe all user data. Ensure you have a backup before proceeding. Also Xiaomi, Oppo and Realme have Additional Steps. Vivo, iQOO and certain Manufacturers don't support Bootloader Unlocking.")
                    ExpandableMethod("Fastboot Method (Recommended)", Icons.Rounded.Computer) {
                        Text("Step 1 : Reboot Phone to Bootloader :")
                        CodeBox("$ adb reboot bootloader")
                        Text("Step 2 : Unlock Bootloader using Fastboot :")
                        Text(" • For most devices :")
                        CodeBox("$ fastboot flashing unlock")
                        Text(" • For some older devices :")
                        CodeBox("$ fastboot oem unlock")
                        Text("Pros :\n✓ Unlocking doesn't brick device immediately.\n✓ Safe and Easy to Use.\n\nCons :\n✗ Not available on all devices.\n✗ Xiaomi Devices need permission from Xiaomi Community and then Mi Unlock Tool is used.\n✗ Oppo and Realme Devices use 'Deep Testing' or 'In-Depth Test' for Fastboot Permissions.")
                    }
                    ExpandableMethod("MTKClient (For MTK Devices)", Icons.Rounded.Memory) {
                        WarningCard("Please BE CAREFUL as it doesn't work on very new device and can cause 'System is Destroyed' and 'dm-verity corruption' Ensure that your device has no Replay Protected Memory Block (RPMB) before proceeding.")
                        Text("Hardware-level bypass for locked MediaTek chipsets.\n\nFirst install USBdk if using Windows (Recommended).\n\nNOTE: For Each Step, Run the Command, Press both Volume Buttons and Connect Phone to PC.\n")
                        Text("Step 1 : Dump vbmeta : ")
                        CodeBox("$ python mtk.py r vbmeta_a,vbmeta_b vbmeta_a.img,vbmeta_b.img")
                        CodeBox("$ python mtk.py r vbmeta vbmeta.img # For Old Devices")
                        Text("Step 2 : Unlock Bootloader : ")
                        CodeBox("$ python mtk.py da seccfg unlock")
                        Text("Step 3 : Disable dm-verity (Easy Way) :  ")
                        CodeBox("$ python mtk.py da vbmeta 3")
                        Text("Step 4 : Erase Userdata : ")
                        CodeBox("$ python mtk.py e metadata,userdata")
                        Text("Step 5 : Reboot Device : ")
                        CodeBox("$ python mtk.py reset")
                        Text("Pros :\n✓ Easy to Recover with Backups.\n✓ Can fix Hard-Bricks.\n✓ Fast and Easy to Use.\n\nCons :\n✗ Does not Support QualComm and UniSOC Devices.\n✗ High Chances of Bricking.\n✗ Doesn't work on very new devices.\n✗ Fastboot may not be usable as on Realme Devices.\n")
                        LinkCard("mtkclient by @bkerler", "https://github.com/bkerler/mtkclient")
                    }
                }
                "METHODS" -> {
                    GuideHeader("Rooting Methods", onBack = { menuPath = "MAIN" })
                    WarningCard(
                        "1. Use Official Sources Only\n" +
                                "2. Don't use 'One-Click Root' Apps\n" +
                                "3. UNLOCK Bootloader first\n" +
                                "4. FASTBOOT devices ONLY ( Excludes Samsung & Odin )")
                    ExpandableMethodLocal("Magisk (Recommended)", R.drawable.ic_magisk) {
                        Text("First obtain your stock boot.img or init_boot.img and patch it using Magisk App and then Flash it.\n")
                        FlashLogic(isAB, slot, true)
                        Text("Pros :\n✓ Truly Systemless\n✓ Widest Module Support\n✓ Works on pretty much anything.\n✓ Best possible documentation and compatibility.\n\nCons :\n✗ Easily Detectable as it leaves Traces.\n")
                        LinkCard("Magisk by @topjohnwu", "https://github.com/topjohnwu/Magisk")
                    }
                    ExpandableMethodLocal("KernelSU", R.drawable.ic_ksu) {
                        if(kernelVersion < "5.10") WarningCard("This Device doesn't Support KernelSU OFFICIALLY. You have to compile your Device's Kernel and integrate KernelSU into it YOURSELF!")
                        Text("First obtain your stock boot.img or init_boot.img and patch it using KernelSU App and then Flash it.\n")
                        FlashLogic(isAB, slot, true)
                        Text("Pros :\n✓ Fully Systemless.\n✓ Very hard to detect by Banking Apps.\n✓ Leaves no Traces.\n\nCons :\n✗ Only Supports devices with Generic Kernel Image.\n")
                        LinkCard("KernelSU by @tiann", "https://github.com/tiann/KernelSU")
                        TerminalLine("Alternative Forks :", MaterialTheme.colorScheme.primary)
                        LinkCard("KernelSU Next by @KernelSU-Next", "https://github.com/KernelSU-Next/KernelSU-Next")
                        LinkCard("SkiSU Ultra by @SkiSU-Ultra", "https://github.com/SkiSU-Ultra/SkiSU-Ultra")
                    }
                    ExpandableMethodLocal("APatch", R.drawable.ic_apatch) {
                        if(kernelVersion < "5.10") WarningCard("This device may or may not Support APatch! Please ensure that your Kernel has 'kallsysms'! DO YOUR OWN RESEARCH!")
                        Text("First obtain your stock boot.img and patch it using Apatch App and then Flash it.\n")
                        FlashLogic(isAB, slot, false)
                        Text("Pros :\n✓ Fully Systemless.\n✓ Very hard to detect by Banking Apps.\n✓ Leaves no Traces.\n✓ Doesn't need a GKI Device.\n\nCons :\n✗ Doesn't work on every device.\n")
                        LinkCard("APatch by @bmax121", "https://github.com/bmax121/APatch")
                    }
                }
                "HIDING" -> {
                    GuideHeader("Rooting Hiding : A Step-by-Step Guide", onBack = { menuPath = "MAIN" })
                    WarningCard("NOTE : This allows you to Bypass Root Checks used by Banking apps for YOUR FINANCIAL SAFETY! Please be cautious while hiding Root.")
                    InfoBlock("Introduction : What is Rooting Hiding?", "\nNow that your Device is Unlocked and Rooted, it's time to Hide this Unlocked Status! Basically, certain Apps like Banking Apps and Game with Anti-Cheat check the presence of Zygisk, Magisk, the 'su' Binary and many more ( for user safety ). But with the power of Systemless Rooting and 'Modules', the device can give a Software-Level Lie to ALL APPS!")
                    InfoBlock("Enabling Zygisk : ","\nIf using Magisk, enable 'Zygisk' in 'Settings'.\n\nFlash the following modules if using KernelSU or APatch or even Magisk ( with built-in Zygisk TURNED OFF ).")
                    LinkCard("ReZygisk by @PerformanC","https://github.com/PerformanC/ReZygisk")
                    LinkCard("Zygisk Next by @Dr-TSNG", "https://github.com/Dr-TSNG/ZygiskNext")
                    InfoBlock("Root Hiding Modules :", "\n1. Tricky Store ( Closed Source but Recommended ) :\n\nThis Module spoofs Hardware Backed Attestation by Software / Hardware Trusted Execution Environment (TEE) by injecting a Valid 'KeyBox.xml'.\n\nThis combined with Tricky Addon and a WebUI Interface can make this Process EASY!\n\nFirst obtain the .ZIP Files from these two links and Flash them. After Reboot Tap the 'Action' Button under Tricky Store and in WebUI, Select All Apps and Tap 'Set Valid Keybox'.")
                    LinkCard("Tricky Store by @5ec1cff", "https://github.com/5ec1cff/TrickyStore")
                    LinkCard("Tricky Addon by @KOWX712", "https://github.com/KOWX712/Tricky-Addon-Update-Target-List")
                    Text("\n2. Shamiko (Closed Source) :\n\nUsed to hide Root Status and ALL TRACES OF ZYGISK AND ROOT PATHS and it Fakes the UNLOCKED Status of Bootloader!\n\nGet the Module from the Latest Release and Flash it.")
                    LinkCard("Shamiko by @LSPosed","https://github.com/LSPosed/LSPosed.github.io/releases/")
                    Text("\n3. Play Integrity Fix (For Custom ROM Users) :\n\nThis Assigns a Valid Fingerprint of a Locked Device Systemlessly.\n\nFlash any ONE of these Modules and Tap the 'Action' Button after Reboot.")
                    LinkCard("Play Integrity Fork by @osm0sis","https://github.com/osm0sis/PlayIntegrityFork")
                    LinkCard("Play Integrity Fix by @KOWX712","https://github.com/KOWX712/PlayIntegrityFix")
                    InfoBlock("Open Source Alternatives :", "\nHaha what an IRONY 💀 !!! A FOSS App is Recommending Closed Source Modules! Peak Logic! Okay, here's some alternatives!")
                    LinkCard("TEESimulator by @JingMatrix","https://github.com/JingMatrix/TEESimulator")
                    LinkCard("NoHello by @MhmRdd", "https://github.com/MhmRdd/NoHello")
                    LinkCard("Tricky Store OSS by @beakthoven", "https://github.com/beakthoven/TrickyStoreOSS")
                    LinkCard("Zygisk Assistant by @snake-4", "https://github.com/snake-4/Zygisk-Assistant")
                }
            }
        }
    }
}
@Composable
fun FlashLogic(isAB: Boolean, slot: String, hasInit: Boolean) { // Just the Command boxes you see.
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (isAB) {
            if (slot == "a") {
                Text("Flash to Active Slot : ")
                CodeBox("$ fastboot flash boot_a patched.img")
                if (hasInit) CodeBox("$ fastboot flash init_boot_a patched.img")
                Text("Flash to Inactive Slot if Needed : ")
                CodeBox("$ fastboot flash boot_b patched.img")
                if(hasInit) CodeBox("$ fastboot flash init_boot_b patched.img")
                Text("For Older Devices : ")
                CodeBox("$ fastboot flash boot patched.img")
            }
            if (slot == "b") {
                Text("Flash to Active Slot : ")
                CodeBox("$ fastboot flash boot_b patched.img")
                if (hasInit) CodeBox("$ fastboot flash init_boot_b patched.img")
                Text("Flash to Inactive Slot if Needed : ")
                CodeBox("$ fastboot flash boot_a patched.img")
                if(hasInit) CodeBox("$ fastboot flash init_boot_a patched.img")
                Text("For Older Devices : ")
                CodeBox("$ fastboot flash boot patched.img")
            }
        } else {
            Text("For Older Devices : ")
            CodeBox("$ fastboot flash boot patched.img")
            Text("Commands not for this Device : ")
            CodeBox("$ fastboot flash boot_a patched.img")
            if(hasInit) CodeBox("$ fastboot flash init_boot_a patched.img")
            CodeBox("$ fastboot flash boot_b patched.img")
            if(hasInit) CodeBox("$ fastboot flash inti_boot_b patched.img")
        }
    }
}
@Composable
fun NavCard(title: String, icon: ImageVector, onClick: () -> Unit) {
    Card(Modifier.fillMaxWidth().clickable { onClick() }, shape = MaterialTheme.shapes.medium) {
        Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(16.dp))
            Text(title, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Icon(Icons.AutoMirrored.Rounded.ArrowForwardIos, null, Modifier.size(14.dp))
        }
    }
}
@Composable
fun ExpandableMethod(title: String, icon: ImageVector, content: @Composable ColumnScope.() -> Unit) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    Card(Modifier.fillMaxWidth().clickable { expanded = !expanded }.animateContentSize(), shape = MaterialTheme.shapes.medium) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(12.dp))
                Text(title, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Icon(if (expanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore, null)
            }
            AnimatedVisibility(expanded) {
                Column(Modifier.padding(top = 12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) { content() }
            }
        }
    }
}
@Composable
fun ExpandableMethodLocal(title: String, resId: Int, content: @Composable ColumnScope.() -> Unit) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    Card(Modifier.fillMaxWidth().clickable { expanded = !expanded }.animateContentSize(), shape = MaterialTheme.shapes.medium) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(painterResource(id = resId), null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                Spacer(Modifier.width(12.dp))
                Text(title, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Icon(if (expanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore, null)
            }
            AnimatedVisibility(expanded) {
                Column(Modifier.padding(top = 12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) { content() }
            }
        }
    }
}
@Composable
fun GuideHeader(title: String, onBack: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { onBack() }.padding(bottom = 8.dp)) {
        Icon(Icons.AutoMirrored.Rounded.ArrowBack, null, tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(8.dp))
        Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
    }
}
@Composable
fun InfoBlock(t: String, d: String) {
    Column {
        Text(t, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
        Text(d, style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(8.dp))
    }
}
@Composable
fun CodeBox(cmd: String) {
    Surface(color = Color.Black, shape = MaterialTheme.shapes.extraSmall, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(cmd, color = Color.White, modifier = Modifier.padding(8.dp), fontFamily = FontFamily.Monospace, fontSize = 11.sp)
    }
}
@Composable
fun TerminalLine(text: String, color: Color = MaterialTheme.colorScheme.onSurfaceVariant) {
    Text(text, color = color, fontFamily = FontFamily.Monospace, fontSize = 13.sp)
}
@Composable
fun LinkCard(t: String, url: String) {
    val ctx = LocalContext.current
    OutlinedCard(onClick = { ctx.startActivity(Intent(Intent.ACTION_VIEW, url.toUri())) }, modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(t, Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Icon(Icons.AutoMirrored.Rounded.OpenInNew, null, Modifier.size(14.dp))
        }
    }
}
@Composable
fun Settings(
    themeMode: Int,
    onThemeChange: (Int) -> Unit,
    dyn: Boolean,
    onDyn: (Boolean) -> Unit,
    reducedAnimations: Boolean,
    onReducedAnimationsChange: (Boolean) -> Unit,
    isLowRam: Boolean
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val noRipple = remember { MutableInteractionSource() }
    val isOfflineVersion = remember { ctx.packageName.endsWith(".offline") }

    // States for Easter Eggs & Dialogs
    var showLicense by rememberSaveable { mutableStateOf(false) }
    var showPoem by rememberSaveable { mutableStateOf(false) }
    var bTaps by rememberSaveable { mutableIntStateOf(0) }
    var vTaps by rememberSaveable { mutableIntStateOf(0) }
    var logoTaps by rememberSaveable { mutableIntStateOf(0) }

    var isChecking by rememberSaveable { mutableStateOf(false) }
    var expanded by rememberSaveable { mutableStateOf(false) }

    val appVersion = remember {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ctx.packageManager.getPackageInfo(ctx.packageName, PackageManager.PackageInfoFlags.of(0)).versionName
            } else {
                @Suppress("DEPRECATION")
                ctx.packageManager.getPackageInfo(ctx.packageName, 0).versionName
            }
        } catch (_: Exception) { "36.23.2.0" }
    }

    // Version Comparison Logic
    fun isNewer(current: String, remote: String): Boolean {
        val currParts = current.replace("v", "").split(".").mapNotNull { it.toIntOrNull() }
        val remoteParts = remote.replace("v", "").split(".").mapNotNull { it.toIntOrNull() }
        val maxLength = maxOf(currParts.size, remoteParts.size)
        for (i in 0 until maxLength) {
            val currPart = currParts.getOrElse(i) { 0 }
            val remotePart = remoteParts.getOrElse(i) { 0 }
            if (remotePart > currPart) return true
            if (remotePart < currPart) return false
        }
        return false
    }
    fun performUpdateCheck() { // Update Check Logic
        scope.launch(Dispatchers.IO) {
            isChecking = true
            try {
                val url = "https://gist.githubusercontent.com/Chill-Astro/b8d2cb9ba2ea314babf65de1bed88662/raw/FRC-SU_V.txt"
                val remoteVersion = URL(url).readText().trim()
                withContext(Dispatchers.Main) {
                    val current = appVersion ?: "36.23.2.0"
                    if (isNewer(current, remoteVersion)) {
                        Toast.makeText(ctx, "$remoteVersion OUT NOW! 🎉", Toast.LENGTH_LONG).show()
                    } else if (isNewer(remoteVersion, current)) {
                        Toast.makeText(ctx, "You are using a DEV. BUILD! ⚠️", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(ctx, "Your Version is UP TO DATE! 🎉", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (_: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(ctx, "Please Verify Internet Connection! ❌", Toast.LENGTH_SHORT).show()
                }
            } finally {
                isChecking = false
            }
        }
    }
    if (showPoem) { // God comes with Dedication and so does FREEDOM. So tap 108 times ig for an Easter egg!
        BasicAlertDialog(onDismissRequest = { showPoem = false }) {
            Surface(
                shape = MaterialTheme.shapes.extraLarge,
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                modifier = Modifier.widthIn(max = 400.dp).padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(Color(0xFFFF9933), Color(0xFFFFFFFF), Color(0xFF128807))
                                ),
                                shape = MaterialTheme.shapes.large
                            )
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "\"यूनान-ओ-मिस्र-ओ-रूमा, सब मिट गए जहाँ से\nअब तक मगर है बाक़ी, नाम-ओ-निशाँ हमारा\nकुछ बात है कि हस्ती मिटती नहीं हमारी\nसदियों रहा है दुश्मन दौर-ए-ज़माँ हमारा\"",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                lineHeight = 30.sp,
                                color = Color.Black
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { showPoem = false; logoTaps = 0 },
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text("Close")
                    }
                }
            }
        }
    }
    if (showLicense) {
        BasicAlertDialog(onDismissRequest = { showLicense = false }) {
            Surface(shape = MaterialTheme.shapes.extraLarge, color = MaterialTheme.colorScheme.surface, tonalElevation = 6.dp, modifier = Modifier.widthIn(max = 500.dp).padding(10.dp)) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(text = "MIT License", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(32.dp))
                    Box(modifier = Modifier.heightIn(max = 300.dp).verticalScroll(rememberScrollState())) {
                        Text(text = "MIT License\n\nCopyright (c) 2025 Dev. Chill-Astro\n\nPermission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the \"Software\"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:\n\nThe above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.\n\nTHE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Spacer(Modifier.height(24.dp))
                    TextButton(modifier = Modifier.align(Alignment.End), onClick = { showLicense = false }) { Text("Close") }
                }
            }
        }
    }
    Column(Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {

        Icon(
            painter = painterResource(id = R.drawable.root_logo),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(160.dp).clickable(indication = null, interactionSource = noRipple) {
                logoTaps++
                when (logoTaps) { // The following lines are by Honourable Netaji Subhas Chandra Bose, the Man who sacrificed everything for his Motherland!
                    5 -> Toast.makeText(ctx, "It is our duty to pay for our liberty...", Toast.LENGTH_SHORT).show()
                    10 -> Toast.makeText(ctx, "...with our own blood.", Toast.LENGTH_SHORT).show()
                    15 -> Toast.makeText(ctx, "Give me blood, and I will give you freedom!", Toast.LENGTH_LONG).show()
                    20 -> Toast.makeText(ctx, "Delhi Chalo!", Toast.LENGTH_SHORT).show()
                    25 -> Toast.makeText(ctx, "Success always stands on the pillars of failure.", Toast.LENGTH_SHORT).show()
                    30 -> Toast.makeText(ctx, "No real change in history has ever been achieved...", Toast.LENGTH_LONG).show()
                    35 -> Toast.makeText(ctx, "...by discussions alone.", Toast.LENGTH_LONG).show()
                    40 -> Toast.makeText(ctx, "One individual may die for an idea,", Toast.LENGTH_LONG).show()
                    45 -> Toast.makeText(ctx, "but that idea will, after his death,", Toast.LENGTH_LONG).show()
                    50 -> Toast.makeText(ctx, "incarnate itself in a thousand lives.", Toast.LENGTH_LONG).show()
                    55 -> Toast.makeText(ctx, "Freedom is not given, it is taken.", Toast.LENGTH_SHORT).show()
                    80 -> Toast.makeText(ctx, "So you have a lot of dedication!", Toast.LENGTH_SHORT).show()
                    103 -> Toast.makeText(ctx, "5!", Toast.LENGTH_SHORT).show()
                    104 -> Toast.makeText(ctx, "4!", Toast.LENGTH_SHORT).show()
                    105 -> Toast.makeText(ctx, "3!", Toast.LENGTH_SHORT).show()
                    106 -> Toast.makeText(ctx, "2!", Toast.LENGTH_SHORT).show()
                    107 -> Toast.makeText(ctx, "1!", Toast.LENGTH_SHORT).show()
                    108 -> {
                        Toast.makeText(ctx, "वन्दे मातरम्!", Toast.LENGTH_SHORT).show() // From Anand Math by Bankim Chandra Chatterjee
                        showPoem = true // Tarana-e-Milli by Muhammad Iqbal. Patriotism!
                        logoTaps = 0
                    }
                }
            }
        )
        Text(
            text = buildAnnotatedString { append("Developer: "); withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) { append("Chill-Astro Software") } },
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(top = 16.dp).clickable(indication = null, interactionSource = noRipple) {
                if (++bTaps == 5) {
                    Toast.makeText(ctx, "Chill-Astro Software - TRANSPARENT BY DESIGN", Toast.LENGTH_SHORT).show() // Ay why not check out my other FOSS Projects!
                    bTaps = 0
                }
            }
        )

        Text(
            text = buildAnnotatedString { append("Version: "); withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) { append(appVersion) } },
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable(indication = null, interactionSource = noRipple) {
                vTaps++
                if (vTaps == 5) Toast.makeText(ctx, "Hi there! You Found me. :)", Toast.LENGTH_SHORT).show()
                if (vTaps == 10) Toast.makeText(ctx, "I hope you like the App! ^_^", Toast.LENGTH_LONG).show()
                if (vTaps == 15) Toast.makeText(ctx, "Ok now you are just poking me....", Toast.LENGTH_LONG).show()
                if (vTaps == 25) Toast.makeText(ctx, "Ok its not funny. Now its hurting my screen!", Toast.LENGTH_LONG).show()
                if (vTaps == 50) Toast.makeText(ctx, "Does Tapping give you anything?", Toast.LENGTH_LONG).show()
                if (vTaps == 75) Toast.makeText(ctx, "Are you a Human Autoclicker?", Toast.LENGTH_LONG).show()
                if (vTaps == 100) Toast.makeText(ctx, "Or maybe you ARE an Autoclicker?", Toast.LENGTH_LONG).show()
                if (vTaps >= 150) {
                    Toast.makeText(ctx, "Touch Some Grass! 🌿", Toast.LENGTH_LONG).show()
                    android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({ exitProcess(0) }, 1500)
                }
            }
        )
        Row(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), horizontalArrangement = Arrangement.Center) {
            Button(
                onClick = { ctx.startActivity(Intent(Intent.ACTION_VIEW, "https://github.com/Chill-Astro/FOSS-Root-Checker".toUri())) },
                modifier = Modifier.height(48.dp),
                shape = RoundedCornerShape(topStart = 28.dp, bottomStart = 28.dp, topEnd = 4.dp, bottomEnd = 4.dp)
            ) {
                Icon(Icons.Rounded.Code, null, Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Repository", fontSize = 13.sp)
            }
            Spacer(Modifier.width(4.dp))
            Button(
                onClick = { showLicense = true },
                modifier = Modifier.height(48.dp),
                shape = RoundedCornerShape(topEnd = 28.dp, bottomEnd = 28.dp, topStart = 4.dp, bottomStart = 4.dp)
            ) {
                Icon(Icons.Rounded.Info, null, Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("License", fontSize = 13.sp)
            }
        }
        HorizontalDivider(Modifier.padding(vertical = 24.dp)) // No Divide and Rule Please!
        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Card(
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
            ) {
                ListItem(
                    headlineContent = { Text("Theme") },
                    leadingContent = { Icon(Icons.Rounded.Palette, null) },
                    trailingContent = {
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded },
                            modifier = Modifier.width(130.dp).padding(end = 4.dp)
                        ) {
                            TextField(
                                value = when (themeMode) { 1 -> "Light"; 2 -> "Dark"; else -> "System" },
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                colors = ExposedDropdownMenuDefaults.textFieldColors(unfocusedContainerColor = Color.Transparent, focusedContainerColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent, focusedIndicatorColor = Color.Transparent),
                                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryEditable, true),
                                textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp)
                            )
                            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                DropdownMenuItem(text = { Text("System") }, onClick = { onThemeChange(0); expanded = false })
                                DropdownMenuItem(text = { Text("Light") }, onClick = { onThemeChange(1); expanded = false })
                                DropdownMenuItem(text = { Text("Dark") }, onClick = { onThemeChange(2); expanded = false })
                            }
                        }
                    },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                )
            }
            if (Build.VERSION.SDK_INT >= 31) { // System Colors (Android 12+)
                Card(shape = MaterialTheme.shapes.medium, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow), modifier = Modifier.fillMaxWidth()) {
                    ListItem(
                        headlineContent = { Text("Use System Colours") },
                        leadingContent = { Icon(Icons.Rounded.Brush, null) },
                        trailingContent = { Switch(checked = dyn, onCheckedChange = onDyn) },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                    )
                }
            }
            Card(shape = MaterialTheme.shapes.medium, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow), modifier = Modifier.fillMaxWidth()) {
                ListItem(
                    headlineContent = { Text("Reduced Animations") },
                    supportingContent = { if (isLowRam) Text("Forced on Low RAM Devices (<4GB)", color = MaterialTheme.colorScheme.primary) },
                    leadingContent = { Icon(Icons.Rounded.Animation, null) },
                    trailingContent = { Switch(checked = reducedAnimations, onCheckedChange = onReducedAnimationsChange, enabled = !isLowRam) },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                )
            }
            if (!isOfflineVersion) {
                // Show the "Check for Updates" button only for the Official version
                Spacer(Modifier.height(16.dp))
                FilledTonalButton(
                    onClick = { if (!isChecking) performUpdateCheck() },
                    modifier = Modifier.align(Alignment.CenterHorizontally).width(220.dp),
                    shape = CircleShape
                ) {
                    if (isChecking) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Rounded.Refresh, null, Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Check for Updates")
                    }
                }
            }
        }
        HorizontalDivider(Modifier.padding(vertical = 24.dp).fillMaxWidth(0.3f), thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)
        Text(text = "Made with 💖 by Chill-Astro", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
@Composable
fun HistoryContent(logs: List<String>, onClear: () -> Unit) { // This History doesn't include boring movements that didn't affect much.
    Column(Modifier.fillMaxWidth().padding(24.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("History", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
            if (logs.isNotEmpty()) IconButton(onClick = onClear) { Icon(Icons.Rounded.DeleteForever, null, tint = MaterialTheme.colorScheme.error) }
        }
        Spacer(Modifier.height(16.dp))
        if (logs.isEmpty()) Box(Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) { Text("Aww no History? Let's make some!", modifier = Modifier.alpha(0.4f)) }
        else LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(logs) { log ->
                val p = log.split("|")
                if (p.size >= 5) {
                    val isOk = p[0] == "OK"
                    val type = p[4]
                    val displayText = if (type == "SCAN") {
                        if (isOk) "Root Traces Found" else "No Root Traces Found"
                    } else {
                        if (isOk) "Root Access Verified" else "Root Access not Available"
                    }
                    Card(Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Surface(Modifier.size(40.dp), shape = CircleShape, color = if (isOk) Color(0xFF4CAF50) else Color(0xFFB00020)) {
                                Icon(if (isOk) Icons.Rounded.Check else Icons.Rounded.Close, null, Modifier.padding(8.dp), Color.White)
                            }
                            Spacer(Modifier.width(16.dp))
                            Column {
                                Text(displayText, fontWeight = FontWeight.Bold)
                                Text("${p[1]} • Android ${p[3]}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }
    }
}
enum class AppDestinations(val label: String, val icon: Any) {
    HOME("Root", Icons.Rounded.Tag),
    BUSYBOX("BusyBox", R.drawable.ic_box),
    GUIDE("Guide", Icons.AutoMirrored.Rounded.MenuBook),
    SETTINGS("Settings", Icons.Rounded.Settings)
}
fun isSUWorking(): Boolean { // No
    return try {
        val process = Runtime.getRuntime().exec(arrayOf("su", "-c", "id"))
        val output = process.inputStream.bufferedReader().use { it.readLine() }
        process.waitFor()
        output?.contains("uid=0") == true
    } catch (_: Exception) { false }
}
fun findBusyBoxPath(): String {
    val paths = arrayOf("/system/xbin/busybox", "/system/bin/busybox", "/data/adb/magisk/busybox", "/data/adb/ksu/bin/busybox", "/data/adb/ap/bin/busybox")
    return paths.firstOrNull { java.io.File(it).exists() } ?: ""
}
fun findBusyBoxPathBySU(): String {
    return try {
        val process = Runtime.getRuntime().exec(arrayOf("su", "-c", "which busybox"))
        val output = process.inputStream.bufferedReader().use { it.readLine() }
        process.waitFor()
        output?.trim() ?: ""
    } catch (_: Exception) { "" }
}
fun saveLog(c: Context, r: Boolean, type: String) {
    val p = c.getSharedPreferences("su_logs", Context.MODE_PRIVATE)
    // OK/NO | Date | Model | Android | Type
    val entry = "${if (r) "OK" else "NO"}|${SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(Date())}|${Build.MODEL}|${Build.VERSION.RELEASE}|$type"
    val set = p.getStringSet("logs", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
    set.add("${System.currentTimeMillis()}_$entry")
    p.edit { putStringSet("logs", set) }
}
fun getLogs(c: Context): List<String> = c.getSharedPreferences("su_logs", Context.MODE_PRIVATE).getStringSet("logs", emptySet())?.toList()?.sortedByDescending { it.substringBefore("_") }?.map { it.substringAfter("_") } ?: emptyList()
fun clearLogs(c: Context) = c.getSharedPreferences("su_logs", Context.MODE_PRIVATE).edit { remove("logs") }