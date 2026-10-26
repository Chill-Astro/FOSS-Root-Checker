@file:OptIn(ExperimentalMaterial3Api::class, androidx.compose.material3.ExperimentalMaterial3ExpressiveApi::class)
package foss.chillastro.root.checker

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.Android
import androidx.compose.material.icons.rounded.Animation
import androidx.compose.material.icons.rounded.Brush
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.FormatColorReset
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Label
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.LockOpen
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Tag
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenu
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShortNavigationBar
import androidx.compose.material3.ShortNavigationBarItem
import androidx.compose.material3.ShortNavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.edit
import androidx.core.net.toUri
import foss.chillastro.root.checker.ui.theme.AppShapes
import foss.chillastro.root.checker.ui.theme.FOSSRootCheckerTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
            var monochrome by remember {
                mutableStateOf(
                    if (prefs.contains("monochrome")) prefs.getBoolean("monochrome", false)
                    else Build.VERSION.SDK_INT < Build.VERSION_CODES.S
                )
            }

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

            FOSSRootCheckerTheme(darkTheme = darkTheme, dynamicColor = useDynamic, monochrome = monochrome, reducedAnimations = reducedAnimations) {
                UpdateHost()
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
                    monochrome = monochrome,
                    onMonochromeChange = {
                        monochrome = it
                        prefs.edit { putBoolean("monochrome", it) }
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
    monochrome: Boolean,
    onMonochromeChange: (Boolean) -> Unit,
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
    val bootloader = remember { HardwareProbe.getBootloader() }
    val verity = remember { HardwareProbe.getVerity() }
    BackHandler(dest != AppDestinations.HOME) { dest = AppDestinations.HOME }
    val pageMotion = MaterialTheme.motionScheme.defaultSpatialSpec<IntOffset>()
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            ShortNavigationBar {
                AppDestinations.entries.forEach { item ->
                    ShortNavigationBarItem(
                        icon = {
                            val iconPainter = when (val icon = item.icon) {
                                is ImageVector -> rememberVectorPainter(icon)
                                is Int -> painterResource(id = icon)
                                else -> error("Invalid icon type")
                            }
                            Icon(
                                painter = iconPainter,
                                contentDescription = item.label,
                                tint = if (monochrome && dest == item) Color.Black else if (dest == item) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        label = { Text(text = item.label) },
                        selected = dest == item,
                        colors = if (monochrome) ShortNavigationBarItemDefaults.colors(
                            selectedIndicatorColor = Color.White,
                            selectedIconColor = Color.Black,
                            selectedTextColorTopIconPosition = Color.White,
                            selectedTextColorStartIconPosition = Color.Black
                        ) else ShortNavigationBarItemDefaults.colors(),
                        onClick = { dest = item }
                    )
                }
            }
        }
    ) { navigationPadding ->
        Scaffold(
            modifier = Modifier.padding(navigationPadding).consumeWindowInsets(navigationPadding),
            topBar = {
                DeviceInfoPill(
                    bootloader = bootloader,
                    verity = verity,
                    onOpenInfo = { showInfoSheet = true },
                    onOpenHistory = { showHistorySheet = true }
                )
            }
        ) { padding ->
            AnimatedContent(
                targetState = dest,
                modifier = Modifier.padding(padding).fillMaxSize(),
                transitionSpec = {
                    if (reducedAnimations) {
                        fadeIn(animationSpec = tween(150)) togetherWith fadeOut(animationSpec = tween(150))
                    } else {
                        val spec = pageMotion
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
                    AppDestinations.HOME -> RootChecker(
                        reducedAnimations, monochrome,
                        onCheckComplete = refreshLogs
                    )
                    AppDestinations.BUSYBOX -> Busybox()
                    AppDestinations.GUIDE -> RootGuide()
                    AppDestinations.SETTINGS -> Settings(
                        themeMode, onThemeChange, dyn, onDyn, monochrome, onMonochromeChange,
                        reducedAnimations, onReducedAnimationsChange, isLowRam
                    )
                }
            }
        }

        if (showHistorySheet) {
            ModalBottomSheet(
                properties = ModalBottomSheetProperties(shouldDismissOnBackPress = true, shouldDismissOnClickOutside = true),
                onDismissRequest = { showHistorySheet = false },
                shape = MaterialTheme.shapes.extraExtraLarge
            ) {
                HistoryContent(logs = logs, monochrome = monochrome, onClear = { clearLogs(context); refreshLogs() })
            }
        }
        if (showInfoSheet) {
            ModalBottomSheet(
                properties = ModalBottomSheetProperties(shouldDismissOnBackPress = true, shouldDismissOnClickOutside = true),
                onDismissRequest = { showInfoSheet = false },
                shape = MaterialTheme.shapes.extraExtraLarge,
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
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Row(modifier = Modifier.padding(24.dp), verticalAlignment = Alignment.CenterVertically) {
                GuidePointIcon(icon = Icons.Rounded.Info, tone = MaterialTheme.colorScheme.onPrimary, iconSize = 28.dp)
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
        shape = MaterialTheme.shapes.large,
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
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)) {
        Icon(Icons.Rounded.Label, null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(8.dp))
        Text(text, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
    }
}
@Composable
fun CategoryGroup(title: String, data: List<Pair<String, Boolean>>) {
    SectionLabel(title)
    data.forEach { (p, e) -> PathRow(path = p, found = e) }
}
// User INTERFACE (I hope it works like butter on Dumpster Fire Devices!)
@Composable
fun RootChecker(
    reducedAnimations: Boolean,
    monochrome: Boolean,
    onCheckComplete: () -> Unit
) {
    var checkState by rememberSaveable { mutableIntStateOf(0) }
    var isRooted by rememberSaveable { mutableStateOf(false) }
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    val circleScale by animateFloatAsState(
        targetValue = if (!reducedAnimations && (checkState == 1 || checkState == 3)) 1.15f else 1f,
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
        Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 12.dp).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Spacer(Modifier.weight(1f))
        Box(contentAlignment = Alignment.Center) {
            RootEmblem(
                checking = isAnyProcessing,
                reducedAnimations = reducedAnimations,
                modifier = Modifier.size(250.dp).graphicsLayer(scaleX = circleScale, scaleY = circleScale),
                color = when(checkState) {
                    2, 4 -> rootResultColor(isRooted, monochrome)
                    else -> if (monochrome) Color.White else MaterialTheme.colorScheme.primaryContainer
                }
            ) {
                Crossfade(
                    targetState = checkState,
                    label = "icon_fade",
                    animationSpec = if (reducedAnimations) tween(300) else spring(stiffness = Spring.StiffnessLow)
                ) { s ->
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        if (s == 2 || s == 4) {
                            Icon(
                                if (isRooted) Icons.Rounded.Check else Icons.Rounded.Close,
                                null,
                                modifier = Modifier.size(90.dp),
                                tint = if (monochrome) Color.Black else Color.White
                            )
                        } else {
                            Icon(
                                painterResource(id = R.drawable.root_hash),
                                null,
                                modifier = Modifier.size(90.dp),
                                tint = if (monochrome) Color.Black else MaterialTheme.colorScheme.onPrimaryContainer
                            )
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
            modifier = Modifier.widthIn(max = 700.dp).fillMaxWidth().height(64.dp).padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
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
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Rounded.Search, null, modifier = Modifier.size(24.dp))
                    Text("Search Root", textAlign = TextAlign.Center)
                }
            }
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
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Rounded.Security, null, modifier = Modifier.size(24.dp))
                    Text("Verify Root", textAlign = TextAlign.Center)
                }
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
    contentPadding: androidx.compose.foundation.layout.PaddingValues = ButtonDefaults.ContentPadding,
    colors: androidx.compose.material3.ButtonColors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
            disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.75f)
        ),
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed && !reducedAnimations) 0.92f else 1f,
        animationSpec = if (reducedAnimations) tween(100) else spring(0.6f, Spring.StiffnessLow),
        label = "button_scale"
    )
    Button(
        onClick = onClick,
        modifier = modifier.graphicsLayer(scaleX = scale, scaleY = scale),
        enabled = enabled,
        shapes = expressiveButtonShapes(shape),
        interactionSource = interactionSource,
        contentPadding = contentPadding,
        colors = colors
    ) {
        content()
    }
}

@Composable
fun Busybox() { // Let's hope She ain't Busy!
    var checkState by rememberSaveable { mutableIntStateOf(0) }
    var foundPath by rememberSaveable { mutableStateOf("") }
    var suPath by rememberSaveable { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val ctx = LocalContext.current
    Column(Modifier.fillMaxSize().padding(24.dp)) {
        Spacer(Modifier.height(16.dp))
        GuideCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            shape = MaterialTheme.shapes.large
        ) {
            Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                GuidePointIcon(icon = Icons.Rounded.Info, iconSize = 32.dp)
                Spacer(Modifier.width(16.dp))
                Column {
                    Text("BUSYBOX INFO", fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Text(
                        "This detects 'Legacy BusyBox Installations' and 'BusyBox for Android NDK' Module.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f)
                    )
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        Box(Modifier.fillMaxWidth().weight(1f).clip(MaterialTheme.shapes.medium).background(MaterialTheme.colorScheme.surfaceContainerHigh).padding(16.dp).verticalScroll(rememberScrollState())) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (checkState == 0) {
                    Text("Ready to Verify?", color = MaterialTheme.colorScheme.onSurfaceVariant, fontFamily = FontFamily.Monospace, fontSize = 13.sp)
                }
                if (checkState >= 1) {
                    Text("Searching for paths...  ♪(´▽｀)", color = MaterialTheme.colorScheme.onSurfaceVariant, fontFamily = FontFamily.Monospace, fontSize = 13.sp)
                }
                if (foundPath.isNotEmpty()) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TerminalLine("BusyBox path verified.", MaterialTheme.colorScheme.primary)
                        Icon(Icons.Rounded.Verified, null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
                    }
                    TerminalLine(foundPath, MaterialTheme.colorScheme.primary)
                }
                else if (checkState >= 2) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TerminalLine("BusyBox not found in PATH.", MaterialTheme.colorScheme.error)
                        Icon(Icons.Rounded.Close, null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.error)
                    }
                    TerminalLine("Fine, but su never lies. ^_~")
                    if (checkState >= 3) {
                        TerminalLine("Running su -c which busybox")
                        if (suPath.isNotEmpty()) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                TerminalLine("BusyBox path verified.", MaterialTheme.colorScheme.primary)
                                Icon(Icons.Rounded.Verified, null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
                            }
                            TerminalLine(suPath, MaterialTheme.colorScheme.primary)
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                TerminalLine("BusyBox not found as root.", MaterialTheme.colorScheme.error)
                                Icon(Icons.Rounded.Close, null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.error)
                            }
                        }
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
                        withContext(Dispatchers.Main) {
                            checkState = 1
                            foundPath = ""
                            suPath = ""
                        }
                        delay(800)
                        val p = findBusyBoxPath()
                        withContext(Dispatchers.Main) {
                            foundPath = p
                            checkState = if (p.isNotEmpty()) 2 else 2
                        }
                        if (p.isEmpty()) {
                            delay(350)
                            val rootedBusyBox = findBusyBoxPathBySU()
                            withContext(Dispatchers.Main) {
                                suPath = rootedBusyBox
                                checkState = 3
                            }
                        }
                    }
                },
                modifier = Modifier
                    .widthIn(max = 500.dp)
                    .fillMaxWidth()
                    .height(70.dp)
                    .padding(top = 12.dp),
                enabled = checkState != 1,
                shapes = expressiveButtonShapes(MaterialTheme.shapes.large),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.75f)
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (checkState == 1) {
                        AppLoadingIndicator(modifier = Modifier.size(28.dp), color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Icon(Icons.Rounded.Shield, null, modifier = Modifier.size(24.dp))
                    }
                    Text(if (checkState == 1) "Searching..." else "Verify BusyBox Installation")
                }
            }
        }
    }
}
@Composable
fun TerminalLine(text: String, color: Color = MaterialTheme.colorScheme.onSurfaceVariant) {
    Text(text, color = color, fontFamily = FontFamily.Monospace, fontSize = 13.sp)
}
@Composable
fun Settings(
    themeMode: Int,
    onThemeChange: (Int) -> Unit,
    dyn: Boolean,
    onDyn: (Boolean) -> Unit,
    monochrome: Boolean,
    onMonochromeChange: (Boolean) -> Unit,
    reducedAnimations: Boolean,
    onReducedAnimationsChange: (Boolean) -> Unit,
    isLowRam: Boolean
) {
    val ctx = LocalContext.current
    var showLicense by rememberSaveable { mutableStateOf(false) }
    var expanded by rememberSaveable { mutableStateOf(false) }

    if (showLicense) {
        BasicAlertDialog(onDismissRequest = { showLicense = false }, properties = DialogProperties(dismissOnClickOutside = false)) {
            Surface(shape = MaterialTheme.shapes.extraLarge, color = MaterialTheme.colorScheme.surface, tonalElevation = 6.dp, modifier = Modifier.widthIn(max = 500.dp).padding(10.dp)) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(text = "MIT License", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(32.dp))
                    Box(modifier = Modifier.heightIn(max = 300.dp).verticalScroll(rememberScrollState())) {
                        Text(text = "MIT License\n\nCopyright (c) 2026 Dev. Chill-Astro\n\nPermission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the \"Software\"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:\n\nThe above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.\n\nTHE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Spacer(Modifier.height(24.dp))
                    Button(
                        onClick = { showLicense = false },
                        modifier = Modifier.fillMaxWidth(),
                        shapes = expressiveButtonShapes()
                    ) {
                        Icon(Icons.Rounded.Close, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Close")
                    }
                }
            }
        }
    }
    Column(Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {

        EasterEggsHeader()
        Row(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), horizontalArrangement = Arrangement.Center) {
            SpringButton(
                reducedAnimations = reducedAnimations,
                onClick = { ctx.startActivity(Intent(Intent.ACTION_VIEW, "https://github.com/Chill-Astro/FOSS-Root-Checker".toUri())) },
                modifier = Modifier.height(48.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 32.dp, vertical = 8.dp),
                shape = RoundedCornerShape(topStart = 28.dp, bottomStart = 28.dp, topEnd = 4.dp, bottomEnd = 4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.75f)
                )
            ) {
                Icon(Icons.Rounded.Code, null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.onPrimary)
                Spacer(Modifier.width(8.dp))
                Text("Repository", fontSize = 13.sp)
            }
            Spacer(Modifier.width(4.dp))
            SpringButton(
                reducedAnimations = reducedAnimations,
                onClick = { showLicense = true },
                modifier = Modifier.height(48.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 32.dp, vertical = 8.dp),
                shape = RoundedCornerShape(topEnd = 28.dp, bottomEnd = 28.dp, topStart = 4.dp, bottomStart = 4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.75f)
                )
            ) {
                Icon(Icons.Rounded.Info, null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.onPrimary)
                Spacer(Modifier.width(8.dp))
                Text("License", fontSize = 13.sp)
            }
        }
        HorizontalDivider(Modifier.padding(vertical = 24.dp)) // No Divide and Rule Please!
        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Card(
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
            ) {
                ListItem(
                    headlineContent = { Text("Theme", color = if (monochrome) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f) else MaterialTheme.colorScheme.onSurface) },
                    leadingContent = { Icon(Icons.Rounded.Palette, null, tint = if (monochrome) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f) else MaterialTheme.colorScheme.onSurface) },
                    trailingContent = {
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { if (!monochrome) expanded = !expanded },
                            modifier = Modifier.width(130.dp).padding(end = 4.dp)
                        ) {
                                TextField(
                                    value = when (themeMode) { 1 -> "Light"; 2 -> "Dark"; else -> "System" },
                                    onValueChange = {},
                                    readOnly = true,
                                    enabled = !monochrome,
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                    colors = ExposedDropdownMenuDefaults.textFieldColors(unfocusedContainerColor = Color.Transparent, focusedContainerColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent, focusedIndicatorColor = Color.Transparent),
                                    modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = !monochrome),
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
            Card(shape = MaterialTheme.shapes.large, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow), modifier = Modifier.fillMaxWidth()) {
                ListItem(
                    headlineContent = { Text("Monochrome Mode") },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Rounded.FormatColorReset,
                            contentDescription = "Color Reset",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    trailingContent = { ExpressiveSwitch(checked = monochrome, onCheckedChange = onMonochromeChange) },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                )
            }
            if (Build.VERSION.SDK_INT >= 31) { // System Colors (Android 12+)
                Card(shape = MaterialTheme.shapes.large, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow), modifier = Modifier.fillMaxWidth()) {
                    ListItem(
                        headlineContent = { Text("Use System Colours", color = if (monochrome) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f) else MaterialTheme.colorScheme.onSurface) },
                        leadingContent = { Icon(Icons.Rounded.Brush, null, tint = if (monochrome) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f) else MaterialTheme.colorScheme.onSurface) },
                        trailingContent = { ExpressiveSwitch(checked = dyn, onCheckedChange = onDyn, enabled = !monochrome) },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                    )
                }
            }
            Card(shape = MaterialTheme.shapes.large, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow), modifier = Modifier.fillMaxWidth()) {
                ListItem(
                    headlineContent = { Text("Reduced Animations") },
                    supportingContent = { if (isLowRam) Text("Forced on Low RAM Devices (<4GB)", color = MaterialTheme.colorScheme.primary) },
                    leadingContent = { Icon(Icons.Rounded.Animation, null) },
                    trailingContent = { ExpressiveSwitch(checked = reducedAnimations, onCheckedChange = onReducedAnimationsChange, enabled = !isLowRam) },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                )
            }
            UpdateSettings(monochrome)
        }
        Spacer(Modifier.height(24.dp))
        Text(text = "Made with 💖 by Chill-Astro", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
@Composable
fun HistoryContent(logs: List<String>, monochrome: Boolean, onClear: () -> Unit) { // This History doesn't include boring movements that didn't affect much.
    Column(Modifier.fillMaxWidth().padding(24.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("History", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
            if (logs.isNotEmpty()) {
                androidx.compose.material3.FilledIconButton(
                    onClick = onClear,
                    modifier = Modifier.size(48.dp),
                    shape = AppShapes.root,
                    colors = androidx.compose.material3.IconButtonDefaults.filledIconButtonColors(
                        containerColor = rootResultColor(positive = false, monochrome = false).copy(alpha = 0.14f),
                        contentColor = rootResultColor(positive = false, monochrome = false)
                    )
                ) {
                    Icon(Icons.Rounded.DeleteForever, contentDescription = "Clear history")
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        if (logs.isEmpty()) {
            Column(
                Modifier.fillMaxWidth().padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ExpressiveIconBadge(Icons.Rounded.History, shape = AppShapes.puffy)
                Text("Aww no History? Let's make some!", color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
            }
        }
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
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.large,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest)
                    ) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            RootEmblem(
                                checking = false,
                                reducedAnimations = true,
                                modifier = Modifier.size(48.dp),
                                color = rootResultColor(isOk, monochrome)
                            ) {
                                Icon(
                                    if (isOk) Icons.Rounded.Check else Icons.Rounded.Close,
                                    null,
                                    modifier = Modifier.size(24.dp),
                                    tint = if (monochrome) Color.Black else Color.White
                                )
                            }
                            Spacer(Modifier.width(16.dp))
                            Column {
                                Text(displayText, fontWeight = FontWeight.Bold)
                                Text("${p[1]} • Android ${p[3]}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
