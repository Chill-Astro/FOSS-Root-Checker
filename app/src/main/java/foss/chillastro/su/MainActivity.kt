@file:OptIn(ExperimentalMaterial3Api::class)

package foss.chillastro.su

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
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
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
import foss.chillastro.su.ui.theme.FOSSRootCheckerTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isSystemDark = isSystemInDarkTheme()
            var manualDarkOverride by rememberSaveable { mutableStateOf<Boolean?>(null) }
            var useDynamic by rememberSaveable { mutableStateOf(true) }
            val currentTheme = manualDarkOverride ?: isSystemDark

            FOSSRootCheckerTheme(darkTheme = currentTheme, dynamicColor = useDynamic) {
                FOSSRootApp(
                    dark = currentTheme,
                    onDark = { manualDarkOverride = it },
                    dyn = useDynamic,
                    onDyn = { useDynamic = it }
                )
            }
        }
    }
}

@Composable
fun FOSSRootApp(dark: Boolean, onDark: (Boolean) -> Unit, dyn: Boolean, onDyn: (Boolean) -> Unit) {
    var dest by rememberSaveable { mutableStateOf(AppDestinations.HOME) }
    var showHistorySheet by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var logs by remember { mutableStateOf(getLogs(context)) }
    val refreshLogs = { logs = getLogs(context) }

    BackHandler(dest != AppDestinations.HOME) { dest = AppDestinations.HOME }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach { item ->
                item(
                    icon = { Icon(item.icon, null) },
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
                            withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) { append("[ FOSS ]") }
                        }, fontWeight = FontWeight.ExtraBold, fontFamily = FontFamily.Monospace)
                    },
                    actions = {
                        IconButton(onClick = { showHistorySheet = true }) {
                            Icon(Icons.Rounded.History, contentDescription = "History")
                        }
                    }
                )
            }
        ) { padding ->
            AnimatedContent(
                targetState = dest,
                modifier = Modifier.padding(padding),
                transitionSpec = {
                    val spec = spring<IntOffset>(stiffness = Spring.StiffnessLow)
                    if (targetState.ordinal > initialState.ordinal) {
                        slideInHorizontally(spec) { it } + fadeIn() togetherWith slideOutHorizontally(spec) { -it } + fadeOut()
                    } else {
                        slideInHorizontally(spec) { -it } + fadeIn() togetherWith slideOutHorizontally(spec) { it } + fadeOut()
                    }
                }, label = "PageTransition"
            ) { target ->
                when (target) {
                    AppDestinations.HOME -> CheckerScreen(onCheckComplete = refreshLogs)
                    AppDestinations.BUSYBOX -> BusyBoxScreen()
                    AppDestinations.GUIDE -> GuideScreen()
                    AppDestinations.SETTINGS -> SettingsScreen(dark, onDark, dyn, onDyn)
                }
            }
        }

        if (showHistorySheet) {
            ModalBottomSheet(onDismissRequest = { showHistorySheet = false }) {
                HistoryContent(logs = logs, onClear = { clearLogs(context); refreshLogs() })
            }
        }
    }
}

@Composable
fun CheckerScreen(onCheckComplete: () -> Unit) {
    var checkState by rememberSaveable { mutableIntStateOf(0) }
    var isRooted by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val circleScale by animateFloatAsState(
        targetValue = if (checkState == 1) 1.15f else 1f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow), label = ""
    )

    Column(
        Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(shape = RoundedCornerShape(24.dp), color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)) {
            Text("${Build.MANUFACTURER} ${Build.MODEL} | Android ${Build.VERSION.RELEASE}", Modifier.padding(horizontal = 16.dp, vertical = 8.dp), style = MaterialTheme.typography.labelLarge)
        }

        Spacer(Modifier.weight(1f))

        Box(contentAlignment = Alignment.Center) {
            if (checkState == 1) CircularProgressIndicator(Modifier.size(240.dp), strokeWidth = 6.dp)
            Surface(
                modifier = Modifier.size(180.dp).graphicsLayer(scaleX = circleScale, scaleY = circleScale),
                shape = CircleShape,
                color = when(checkState) {
                    2 -> if (isRooted) Color(0xFF4CAF50) else Color(0xFFB00020)
                    else -> MaterialTheme.colorScheme.primaryContainer
                }, tonalElevation = 8.dp
            ) {
                Crossfade(targetState = checkState, label = "") { s ->
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        if (s == 2) Icon(if (isRooted) Icons.Rounded.Check else Icons.Rounded.Close, null, Modifier.size(72.dp), Color.White)
                        else Icon(painterResource(id = R.drawable.root_hash), null, Modifier.size(80.dp), MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                }
            }
        }

        Text(
            text = when(checkState) {
                1 -> "Interrogating SU Binaries..."
                2 -> if (isRooted) "Your Device is Rooted" else "Root Access not Available"
                else -> "Ready to verify?"
            },
            modifier = Modifier.padding(top = 24.dp),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.weight(1.2f))

        Button(
            onClick = {
                checkState = 1
                scope.launch(Dispatchers.IO) {
                    delay(1200)
                    val r = checkRoot()
                    withContext(Dispatchers.Main) {
                        isRooted = r; checkState = 2; saveLog(context, r); onCheckComplete()
                        Toast.makeText(context, if (r) "Root Access Verified" else "Root Access not Available", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(0.7f).height(64.dp),
            shape = RoundedCornerShape(32.dp), enabled = checkState != 1
        ) {
            Text("Verify Root")
        }
    }
}

@Composable
fun BusyBoxScreen() {
    var checkState by remember { mutableIntStateOf(0) }
    var foundPath by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "SYSTEM BINARIES",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.height(16.dp))

        // This Box now uses .weight(1f) to take up all available vertical space
        Box(
            Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()) // Allows scrolling if terminal text gets long
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                TerminalLine("guest@android:~$ busybox --version")
                if (checkState == 1) {
                    TerminalLine("Searching system paths...", isDim = true)
                } else if (checkState == 2) {
                    if (foundPath.isNotEmpty()) {
                        TerminalLine("BusyBox detected!", color = MaterialTheme.colorScheme.primary)
                        TerminalLine("Path: $foundPath")
                    } else {
                        TerminalLine("sh: busybox: not found", color = MaterialTheme.colorScheme.error)
                    }
                }
                if (checkState != 1) TerminalLine("guest@android:~$ _")
            }
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                scope.launch(Dispatchers.IO) {
                    checkState = 1; delay(1000)
                    val path = findBusyBoxPath()
                    withContext(Dispatchers.Main) { checkState = 2; foundPath = path }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp), // Fixed height for the button
            enabled = checkState != 1
        ) {
            Text("Verify BusyBox Installation")
        }
    }
}

@Composable
fun TerminalLine(text: String, color: Color = MaterialTheme.colorScheme.onSurfaceVariant, isDim: Boolean = false) {
    Text(text = text, color = if (isDim) color.copy(alpha = 0.5f) else color, fontFamily = FontFamily.Monospace, fontSize = 14.sp)
}

// --- RESTORED OG GUIDE PAGE ---
@Composable
fun GuideScreen() {
    Column(Modifier.padding(16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(MaterialTheme.colorScheme.errorContainer), shape = RoundedCornerShape(24.dp)) {
            Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Warning, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(32.dp))
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(text = "WARNING!", fontWeight = FontWeight.ExtraBold)
                    Text(text = "Never trust 'One-Click Root' apps. Only use open-source binaries like Magisk or KernelSU.", style = MaterialTheme.typography.bodySmall)
                    Text(text = "Certain Manufacturers like Vivo and iQOO don't allow bootloader unlocking so no Root Access!", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
        Text(text = "INSTRUCTIONS", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
        GuideCard("1. Enable OEM Unlocking", "Enable 'OEM Unlocking' and 'USB Debugging under Developer Options'.", Icons.Rounded.Settings)
        GuideCard("2. Unlock Bootloader (via PC)", "Run fastboot flashing unlock to Unlock Bootloader.", Icons.Rounded.LockOpen)
        GuideCard("3. Flash Modified Kernel / Boot Files", "Flash modified boot.img (or init_boot.img) for Magsik and APatch or modified kernel or GKI if using KernelSU. These patched files are provided by the App or from Official Repository.", Icons.Rounded.FlashOn)
        Text(text = "TRUSTED ROOT METHODS", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
        LinkCard("Magisk by @topjohnwu", "https://github.com/topjohnwu/Magisk")
        LinkCard("KernelSU by @tiann", "https://github.com/tiann/KernelSU")
        LinkCard("APatch by @bmax121", "https://github.com/bmax121/APatch")
    }
}

@Composable
fun GuideCard(t: String, d: String, i: ImageVector) {
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(i, null, tint = MaterialTheme.colorScheme.primary); Spacer(Modifier.width(16.dp))
            Column { Text(text = t, fontWeight = FontWeight.Bold); Text(text = d, style = MaterialTheme.typography.bodyMedium) }
        }
    }
}

@Composable
fun LinkCard(t: String, url: String) {
    val ctx = LocalContext.current
    OutlinedCard(onClick = { ctx.startActivity(Intent(Intent.ACTION_VIEW, url.toUri())) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(text = t, modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
            Icon(Icons.AutoMirrored.Rounded.OpenInNew, null, Modifier.size(18.dp))
        }
    }
}

// --- RESTORED OG SETTINGS PAGE ---
@Composable
fun SettingsScreen(dark: Boolean, onDark: (Boolean) -> Unit, dyn: Boolean, onDyn: (Boolean) -> Unit) {
    val ctx = LocalContext.current
    var bTaps by remember { mutableIntStateOf(0) }
    var vTaps by remember { mutableIntStateOf(0) }

    val appVersion = remember {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ctx.packageManager.getPackageInfo(ctx.packageName, PackageManager.PackageInfoFlags.of(0)).versionName
            } else {
                ctx.packageManager.getPackageInfo(ctx.packageName, 0).versionName
            }
        } catch (e: Exception) { "1.0.0" }
    }

    Column(Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(painterResource(id = R.drawable.root_logo), null, Modifier.size(160.dp), MaterialTheme.colorScheme.primary)

        Text(
            text = buildAnnotatedString {
                append("Developer: ")
                withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) { append("Chill-Astro Software") }
            },
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(top = 16.dp).clickable {
                if (++bTaps == 5) { Toast.makeText(ctx, "Chill-Astro Software - TRANSPARENT BY DESIGN", Toast.LENGTH_SHORT).show(); bTaps = 0 }
            }
        )

        Text(
            text = buildAnnotatedString {
                append("Version: ")
                withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) { append(appVersion ?: "1.0.0") }
            },
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable {
                vTaps++
                when (vTaps) {
                    5 -> Toast.makeText(ctx, "Hi there! You Found me. :)", Toast.LENGTH_SHORT).show()
                    10 -> Toast.makeText(ctx, "I hope you like the App! ^_^ ", Toast.LENGTH_SHORT).show()
                    15 -> Toast.makeText(ctx, "Ok now you are just poking me....", Toast.LENGTH_SHORT).show()
                    25 -> Toast.makeText(ctx, "Ok its not funny. Now its hurting my screen." , Toast.LENGTH_SHORT).show()
                    50 -> Toast.makeText(ctx, "Does Tapping give you anything?" , Toast.LENGTH_SHORT).show()
                    100 -> Toast.makeText(ctx, "Or maybe you are an autoclicker?", Toast.LENGTH_SHORT).show()
                    150 -> Toast.makeText(ctx, "At this point, just visit a Mental Health Doctor.", Toast.LENGTH_LONG).show()
                }
            }
        )

        TextButton(onClick = { ctx.startActivity(Intent(Intent.ACTION_VIEW, "https://github.com/Chill-Astro/FOSS-Root-Checker".toUri())) }) {
            Icon(Icons.Rounded.Code, null, Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text(text = "Official Repository")
        }

        HorizontalDivider(Modifier.padding(vertical = 24.dp))

        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = "PREFERENCES", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            ListItem(headlineContent = { Text("Dark Theme") }, leadingContent = { Icon(Icons.Rounded.Brush, null) }, trailingContent = { Switch(checked = dark, onCheckedChange = onDark) })
            if (Build.VERSION.SDK_INT >= 31) {
                ListItem(headlineContent = { Text("Use System Colours") }, leadingContent = { Icon(Icons.Rounded.Palette, null) }, trailingContent = { Switch(checked = dyn, onCheckedChange = onDyn) })
            }
        }
    }
}

@Composable
fun HistoryContent(logs: List<String>, onClear: () -> Unit) {
    Column(Modifier.fillMaxWidth().padding(24.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("History", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
            if (logs.isNotEmpty()) IconButton(onClick = onClear) { Icon(Icons.Rounded.DeleteForever, null, tint = MaterialTheme.colorScheme.error) }
        }
        Spacer(Modifier.height(16.dp))
        if (logs.isEmpty()) Box(Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) { Text("No records", modifier = Modifier.alpha(0.4f)) }
        else LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(logs) { log ->
                val p = log.split("|")
                if (p.size >= 4) Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Surface(Modifier.size(40.dp), shape = CircleShape, color = if (p[0] == "OK") Color(0xFF4CAF50) else Color(0xFFB00020)) {
                            Icon(if (p[0] == "OK") Icons.Rounded.Check else Icons.Rounded.Close, null, Modifier.padding(8.dp), Color.White)
                        }
                        Spacer(Modifier.width(16.dp))
                        Column { Text(if (p[0] == "OK") "Rooted" else "Not Rooted", fontWeight = FontWeight.Bold); Text("${p[1]} • Android ${p[3]}", style = MaterialTheme.typography.bodySmall, color = Color.Gray) }
                    }
                }
            }
        }
    }
}

enum class AppDestinations(val label: String, val icon: ImageVector) {
    HOME("Root", Icons.Rounded.Tag),
    BUSYBOX("BusyBox", Icons.Rounded.Terminal),
    GUIDE("Guide", Icons.AutoMirrored.Rounded.MenuBook),
    SETTINGS("Settings", Icons.Rounded.Settings)
}

fun checkRoot(): Boolean = try {
    Runtime.getRuntime().exec(arrayOf("su", "-c", "id")).inputStream.bufferedReader().readLine()?.contains("uid=0") == true
} catch (_: Exception) {
    arrayOf("/system/xbin/su", "/system/bin/su", "/sbin/su").any { java.io.File(it).exists() }
}

fun findBusyBoxPath(): String {
    val paths = arrayOf("/system/xbin/busybox", "/system/bin/busybox", "/data/adb/magisk/busybox", "/data/adb/ksu/bin/busybox")
    return paths.firstOrNull { java.io.File(it).exists() } ?: ""
}

fun saveLog(c: Context, r: Boolean) {
    val p = c.getSharedPreferences("su_logs", Context.MODE_PRIVATE)
    val t = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(Date())
    val entry = "${if (r) "OK" else "NO"}|$t|${Build.MODEL}|${Build.VERSION.RELEASE}|${Build.ID}"
    val set = p.getStringSet("logs", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
    set.add("${System.currentTimeMillis()}_$entry")
    p.edit { putStringSet("logs", set) }
}

fun getLogs(c: Context): List<String> = c.getSharedPreferences("su_logs", Context.MODE_PRIVATE).getStringSet("logs", emptySet())?.toList()?.sortedByDescending { it.substringBefore("_") }?.map { it.substringAfter("_") } ?: emptyList()

fun clearLogs(c: Context) = c.getSharedPreferences("su_logs", Context.MODE_PRIVATE).edit { remove("logs") }