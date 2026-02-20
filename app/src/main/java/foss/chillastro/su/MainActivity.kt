@file:OptIn(ExperimentalMaterial3Api::class)

package foss.chillastro.su

import android.content.pm.PackageManager
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.interaction.MutableInteractionSource
import android.widget.Toast
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
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
import java.util.Date
import java.util.Locale

// --- SYSTEM LOGIC ---
object HardwareProbe {
    fun getProp(prop: String): String = try {
        Runtime.getRuntime().exec(arrayOf("getprop", prop)).inputStream.bufferedReader().readLine() ?: ""
    } catch (e: Exception) { "" }

    fun getBootloader(): String = if (getProp("ro.boot.flash.locked") == "0") "Unlocked" else "Locked"
    fun getVerity(): String = getProp("ro.boot.veritymode").ifEmpty { "disabled" }
}

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
                modifier = Modifier.padding(padding).fillMaxSize(), // Full height ensured here
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

// --- SCREENS ---
@Composable
fun CheckerScreen(onCheckComplete: () -> Unit) {
    var checkState by rememberSaveable { mutableIntStateOf(0) }
    var isRooted by rememberSaveable { mutableStateOf(false) }
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    // Hardware info (cached so it doesn't re-run)
    val bootloader = remember { HardwareProbe.getBootloader() }
    val verity = remember { HardwareProbe.getVerity() }

    // Smooth scaling animation
    val circleScale by animateFloatAsState(
        targetValue = if (checkState == 1 || checkState == 3) 1.15f else 1f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow),
        label = "pulse"
    )

    Column(
        Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- Header Section ---
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ) {
            Row(
                Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Rounded.Info, null, Modifier.size(20.dp), MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(12.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${Build.MANUFACTURER} ${Build.MODEL} | Android ${Build.VERSION.RELEASE}", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    Text("Bootloader: $bootloader | dm-verity: $verity", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
                }
            }
        }

        Spacer(Modifier.weight(1f))

        // --- Visual Indicator (The Circle) ---
        Box(contentAlignment = Alignment.Center) {
            // Show spinner during "Searching" (1) and "Interrogating" (3)
            if (checkState == 1 || checkState == 3) {
                CircularProgressIndicator(Modifier.size(240.dp), strokeWidth = 6.dp)
            }

            Surface(
                modifier = Modifier.size(180.dp).graphicsLayer(scaleX = circleScale, scaleY = circleScale),
                shape = CircleShape,
                color = when(checkState) {
                    2, 4 -> if (isRooted) Color(0xFF4CAF50) else Color(0xFFB00020)
                    else -> MaterialTheme.colorScheme.primaryContainer
                },
                tonalElevation = 8.dp
            ) {
                Crossfade(targetState = checkState, label = "icon_fade") { s ->
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        if (s == 2 || s == 4) {
                            Icon(if (isRooted) Icons.Rounded.Check else Icons.Rounded.Close, null, Modifier.size(72.dp), Color.White)
                        } else {
                            Icon(painterResource(id = R.drawable.root_hash), null, Modifier.size(80.dp), MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                    }
                }
            }
        }

        // --- Status Text ---
        Text(
            text = when(checkState) {
                1 -> "Searching for Paths..."
                2 -> "Your Device is Rooted"
                3 -> "Interrogating SU Binary..."
                4 -> if (isRooted) "Your Device is Rooted" else "Root Access not Available"
                else -> "Ready to verify?"
            },
            modifier = Modifier.padding(top = 24.dp),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.weight(1.2f))

        // --- Action Button ---
        Button(
            onClick = {
                // Reset states for a fresh check
                checkState = 1
                isRooted = false

                scope.launch(Dispatchers.IO) {
                    delay(1000) // Visual pause

                    // 1. Path Check (Execution is inside IO thread to prevent lag)
                    val paths = arrayOf(
                        // Standard Binaries
                        "/system/xbin/su",
                        "/system/bin/su",
                        "/sbin/su",
                        "/system/sd/xbin/su",
                        "/system/bin/failsafe/su",
                        "/data/local/xbin/su",
                        "/data/local/bin/su",
                        "/data/local/su",

                        // KernelSU & APatch Specific
                        "/data/adb/ksu/bin/su",
                        "/data/adb/apatch/bin/su",
                        "/data/adb/magisk/su",

                        // Mount Points & Internal Folders (Huge Red Flags)
                        "/data/adb/modules",      // If this exists, Magisk/KSU is active
                        "/data/adb/ksu",          // KernelSU data
                        "/data/adb/apatch",       // APatch data
                        "/data/adb/magisk.db",    // Magisk Database
                    )

                    val foundViaPath = paths.any { path ->
                        try {
                            Runtime.getRuntime().exec(arrayOf("ls", path)).waitFor() == 0
                        } catch (e: Exception) { false }
                    }

                    if (foundViaPath) {
                        // 2. FLAG AS ROOTED (Path Found)
                        withContext(Dispatchers.Main) {
                            isRooted = true
                            checkState = 2
                            if (bootloader == "Locked") {
                                Toast.makeText(ctx, "Root Access is Verified. Nice Spoofing! :)", Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(ctx, "Root Access Verified", Toast.LENGTH_SHORT).show()
                            }
                            onCheckComplete()
                        }
                    } else {
                        // 3. NO PATH? -> Move to Interrogation (State 3)
                        withContext(Dispatchers.Main) { checkState = 3 }
                        delay(1200)

                        // 4. THE FINAL INTERROGATION (State 4)
                        val suWorks = isSUWorking()
                        withContext(Dispatchers.Main) {
                            isRooted = suWorks
                            checkState = 4

                            if (isRooted && bootloader == "Locked") {
                                Toast.makeText(ctx, "Root Access is Verified. Nice Spoofing! :)", Toast.LENGTH_LONG).show()
                            } else if (isRooted) {
                                Toast.makeText(ctx, "Root Access Verified", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(ctx, "Root Access not Available", Toast.LENGTH_SHORT).show()
                            }
                            onCheckComplete()
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(0.7f).height(64.dp),
            shape = RoundedCornerShape(32.dp),
            enabled = checkState != 1 && checkState != 3
        ) {
            Text(if (checkState == 0) "Verify Root" else "Verify Root Again")
        }
    }
}
@Composable
fun WarningCard(bodyText: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
        shape = RoundedCornerShape(24.dp)
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
fun BusyBoxScreen() {
    var checkState by remember { mutableIntStateOf(0) }
    var foundPath by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Column(Modifier.fillMaxSize().padding(24.dp)) {
        Text("SYSTEM BINARIES", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(16.dp))
        Box(Modifier.fillMaxWidth().weight(1f).clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.surfaceContainerHigh).padding(16.dp).verticalScroll(rememberScrollState())) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TerminalLine("Ready to Verify?")
                if (checkState == 2) {
                    if (foundPath.isNotEmpty()) TerminalLine("BusyBox Path: $foundPath", MaterialTheme.colorScheme.primary)
                    else TerminalLine("Busybox not Found", MaterialTheme.colorScheme.error)
                }
                HorizontalDivider(Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant)
                TerminalLine("NOTE: Magisk and other Root Solutions have their own BusyBox but are kept hidden to avoid detection.")
                TerminalLine("Install 'BusyBox for NDK Module' if really needed. Else its not needed that much.")
            }
        }
        Button(
            onClick = {
                scope.launch(Dispatchers.IO) {
                    checkState = 1; delay(800)
                    val p = findBusyBoxPath()
                    withContext(Dispatchers.Main) { foundPath = p; checkState = 2 }
                }
            },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            enabled = checkState != 1
        ) {
            Text(if (checkState == 1) "Searching..." else "Verify BusyBox Installation")
        }
    }
}

@Composable
fun GuideScreen() {
    var menuPath by rememberSaveable { mutableStateOf("MAIN") }
    val slot = remember { HardwareProbe.getProp("ro.boot.slot_suffix").replace("_", "").ifEmpty { "" } }
    val isAB = slot.isNotEmpty()

    BackHandler(menuPath != "MAIN") { menuPath = "MAIN" }

    AnimatedContent(
        targetState = menuPath,
        modifier = Modifier.fillMaxSize(), // Prevent shrinking
        transitionSpec = {
            fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
        }, label = "SubMenuTransition"
    ) { targetPath ->
        Column(Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            when (targetPath) {
                "MAIN" -> {
                    WarningCard("Never trust 'One-Click Root' Apps and Please BE CAREFUL while following this guide. I am not responsible for any damages to your device.")
                    Text("GUIDE SECTIONS", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    GuideNavCard("1. Rooting: An Introduction", Icons.AutoMirrored.Rounded.LibraryBooks) { menuPath = "INTRO" }
                    GuideNavCard("2. Unlocking Bootloader", Icons.Rounded.LockOpen) { menuPath = "UNLOCK" }
                    GuideNavCard("3. Rooting Methods", Icons.Rounded.Tag) { menuPath = "METHODS" }
                }
                "INTRO" -> {
                    GuideHeader("Rooting : An Introduction", onBack = { menuPath = "MAIN" })
                    WarningCard("Please BE CAREFUL what apps you are giving Root Permissions to. I am not responsible for Data or Money Theft by Malware on your Device.")
                    InfoBlock("Introduction : What is Rooting?", "\nRooting an Android device means gaining full administrative (superuser) control, similar to an administrator on a computer, by unlocking deep system access restricted by manufacturers.")
                    InfoBlock("Pros :", "\n✓ Bloatware Removal\n✓ System-wide Adblocking\n✓ Overclocking and Underclocking Device\n✓ Modifying User Experience\n✓ Deep level Customization\n✓ Full Data Backups")
                    InfoBlock("Cons :", "\n✗ Usually Voids Warranty\n✗ Increased Security Risks\n✗ Loss of Hardware Encoding\n✗ No Official Updates (OTA)\n✗ Data loss\n✗ Risk of Bricking Device")
                    InfoBlock("Suggestion from My Experience :", "\nAs from my little experience from Rooting, use Magisk if you are not sure. It works on almost every device and it can be flashed with PC and Custom Recovery ( like TWRP or OrangeFox ) and does the job very well. Unless your device is old, DO NOT USE EXPLOITS! I had bricked my own device like this so BE CAREFUL! If you want to explore more options, I recommend APatch and KernelSU ( if Supported ). They don't work on every device but are pretty reliable.")
                }
                "UNLOCK" -> {
                    GuideHeader("Unlocking Bootloader", onBack = { menuPath = "MAIN" })
                    WarningCard("This process will wipe all user data. Ensure you have a backup before proceeding. Also Xiaomi, Oppo and Realme have Additional Steps. Vivo, iQOO and certain Manufacturers don't support Bootloader Unlocking.")
                    ExpandableMethod("Fastboot Method (Recommended)", Icons.Rounded.Computer) {
                        Text("Step 1 : Reboot Phone to Bootloader :")
                        CodeBox("adb reboot bootloader")
                        Text("Step 2 : Unlock Bootloader using Fastboot :")
                        Text(" • For most devices :")
                        CodeBox("fastboot flashing unlock")
                        Text(" • For some older devices :")
                        CodeBox("fastboot flashing unlock")
                        Text("Pros :\n✓ Unlocking doesn't brick device immediately.\n✓ Safe and Easy to Use.\n\nCons :\n✗ Not available on all devices.\n✗ Xiaomi Devices need permission from Xiaomi Community and then Mi Unlock Tool is used.\n✗ Oppo and Realme Devices use 'Deep Testing' or 'In-Depth Test' for Fastboot Permissions.")
                    }
                    ExpandableMethod("Device Unlock Mode (for Samsung)", Icons.Rounded.Smartphone) {
                        WarningCard("NOTE : I don't own a Samsung Device. This is the General Information I have. Also, this disables KNOX Security permanently and many Samsung Apps Stop Working.")
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("\nStep 1 : Turn on 'OEM Unlocking' in Developer Options.")
                            Text("Step 2 : Connect your Charging Cable to a PC (but not Phone).")
                            Text("Step 3 : Press and hold Volume Up + Volume Down simultaneously.")
                            Text("Step 4 : While holding both buttons, plug the USB cable into your phone.")
                            Text("Step 5 : Release the buttons when you see a teal/blue warning screen.")
                            Text("Step 6 : Press Volume Up once to continue to Download Mode.")
                            Text("Step 7 : Long Press Volume Up for Unlocking Device.")
                        }
                    }
                    ExpandableMethod("MTKClient (For MTK Devices)", Icons.Rounded.Memory) {
                        WarningCard("Please BE CAREFUL as it doesn't work on very new device and can cause 'System is Destroyed' and 'dm-verity corruption' Ensure that your device has no Replay Protected Memory Block (RPMB) before proceeding.")
                        Text("\nHardware-level bypass for locked MediaTek chipsets.\n\nFirst install USBdk if using Windows (Recommended).\n\nNOTE: For Each Step, Run the Command, Press both Volume Buttons and Connect Phone to PC.\n")
                        Text("Step 1 : Dump vbmeta (Don't use _a and _b if newer device) : ")
                        CodeBox("python mtk.py r vbmeta_a,vbmeta_b vbmeta_a.img,vbmeta_b.img")
                        Text("Step 2 : Unlock Bootloader : ")
                        CodeBox("python mtk.py da seccfg unlock")
                        Text("Step 3 : Disable dm-verity (Easy Way) :  ")
                        CodeBox("python mtk.py da vbmeta 3")
                        Text("Step 4 : Erase Userdata : ")
                        CodeBox("python mtk.py e metadata,userdata")
                        Text("Step 5 : Reboot Device : ")
                        CodeBox("python mtk.py reset")
                        Text("Pros :\n✓ Easy to Recover with Backups.\n✓ Can fix Hard-Bricks.\n✓ Fast and Easy to Use.\n\nCons :\n✗ Does not Support QualComm and UniSOC Devices.\n✗ High Chances of Bricking.\n✗ Doesn't work on very new devices.\n✗ Fastboot may not be usable as on Realme Devices.\n")
                        LinkCard("mtkclient by @bkerler", "https://github.com/bkerler/mtkclient")
                    }
                }
                "METHODS" -> {
                    GuideHeader("Rooting Methods", onBack = { menuPath = "MAIN" })
                    WarningCard("Please download the following apps from their Official Sources. Do not modify or delete System Files. Do not use 'One-Click' Root Apps.")
                    ExpandableMethodLocal("Magisk (Recommended)", R.drawable.ic_magisk) {
                        Text("First obtain your stock boot.img or init_boot.img and patch it using Magisk App and then Flash it.\n")
                        FlashLogic(isAB, slot, true)
                        Text("Pros :\n✓ Truly Systemless\n✓ Widest Module Support\n✓ Works on pretty much anything.\n✓ Best possible documentation and compatitibility.\n\nCons :\n✗ Easily Detectable as it leaves Traces.\n")
                        LinkCard("Magisk by @topjohnwu", "https://github.com/topjohnwu/Magisk")
                    }

                    ExpandableMethodLocal("KernelSU / SkiSU Ultra", R.drawable.ic_ksu) {
                        Text("First obtain your stock boot.img or init_boot.img and patch it using KernelSU or SkiSU App and then Flash it.\n")
                        FlashLogic(isAB, slot, true)
                        Text("Pros :\n✓ Fully Systemless.\n✓ Very hard to detect by Banking Apps.\n✓ Leaves no Traces.\n\nCons :\n✗ Only Supports devices with Generic Kernel Image.\n")
                        LinkCard("KernelSU by @tiann", "https://github.com/tiann/KernelSU")
                    }

                    ExpandableMethodLocal("APatch", R.drawable.ic_apatch) {
                        Text("First obtain your stock boot.img and patch it using Apatch App and then Flash it.\n")
                        FlashLogic(isAB, slot, false)
                        Text("Pros :\n✓ Fully Systemless.\n✓ Very hard to detect by Banking Apps.\n✓ Leaves no Traces.\n✓ Doesn't need a GKI Device.\n\nCons :\n✗ Doesn't work on every device.\n")
                        LinkCard("APatch by @bmax121", "https://github.com/bmax121/APatch")
                    }
                }
            }
        }
    }
}

// --- UI COMPONENTS ---

@Composable
fun FlashLogic(isAB: Boolean, slot: String, hasInit: Boolean) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (isAB) {
            if (slot == "a") {
                CodeBox("fastboot flash boot_a patched.img")
                if (hasInit) CodeBox("fastboot flash init_boot_a patched.img")
                CodeBox("fastboot flash boot_b patched.img")
                if(hasInit) CodeBox("fastboot flash init_boot_b patched.img")
                CodeBox("fastboot flash boot patched.img")
            }
            if (slot == "b") {
                CodeBox("fastboot flash boot_b patched.img")
                if (hasInit) CodeBox("fastboot flash init_boot_b patched.img")
                CodeBox("fastboot flash boot_a patched.img")
                if(hasInit) CodeBox("fastboot flash init_boot_a patched.img")
                CodeBox("fastboot flash boot patched.img")
            }
        } else {
            // A-only: boot is first, then init_boot if requested
            CodeBox("fastboot flash boot patched.img")

        }
    }
}

@Composable
fun GuideNavCard(title: String, icon: ImageVector, onClick: () -> Unit) {
    Card(Modifier.fillMaxWidth().clickable { onClick() }, shape = RoundedCornerShape(16.dp)) {
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
    var expanded by remember { mutableStateOf(false) }
    Card(Modifier.fillMaxWidth().clickable { expanded = !expanded }, shape = RoundedCornerShape(16.dp)) {
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
    var expanded by remember { mutableStateOf(false) }
    Card(Modifier.fillMaxWidth().clickable { expanded = !expanded }, shape = RoundedCornerShape(16.dp)) {
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
    Surface(color = Color.Black, shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(cmd, color = Color.Green, modifier = Modifier.padding(8.dp), fontFamily = FontFamily.Monospace, fontSize = 11.sp)
    }
}

@Composable
fun TerminalLine(text: String, color: Color = MaterialTheme.colorScheme.onSurfaceVariant) {
    Text(text, color = color, fontFamily = FontFamily.Monospace, fontSize = 13.sp)
}

@Composable
fun LinkCard(t: String, url: String) {
    val ctx = LocalContext.current
    OutlinedCard(onClick = { ctx.startActivity(Intent(Intent.ACTION_VIEW, url.toUri())) }, modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(t, Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Icon(Icons.AutoMirrored.Rounded.OpenInNew, null, Modifier.size(14.dp))
        }
    }
}

// --- SETTINGS PAGE ---
@Composable
fun SettingsScreen(
    dark: Boolean,
    onDark: (Boolean) -> Unit,
    dyn: Boolean,
    onDyn: (Boolean) -> Unit
) {
    val ctx = LocalContext.current
    val noRipple = remember { MutableInteractionSource() } // Shared source to disable ripples

    var bTaps by remember { mutableIntStateOf(0) }
    var vTaps by remember { mutableIntStateOf(0) }
    var logoTaps by remember { mutableIntStateOf(0) }
    var showPoem by remember { mutableStateOf(false) }

    val appVersion = remember {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ctx.packageManager.getPackageInfo(ctx.packageName, PackageManager.PackageInfoFlags.of(0)).versionName
            } else {
                @Suppress("DEPRECATION")
                ctx.packageManager.getPackageInfo(ctx.packageName, 0).versionName
            }
        } catch (e: Exception) { "1.0.0" }
    }

    // --- MATERIAL YOU POPUP ---
    if (showPoem) {
        BasicAlertDialog(onDismissRequest = { showPoem = false }) {
            // Surface uses the Material You "Surface" color and shape
            Surface(
                shape = MaterialTheme.shapes.extraLarge,
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                modifier = Modifier
                    .widthIn(max = 400.dp) // Ensures it doesn't get too wide on large screens
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Inner Box for the Tri-ranga effect
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFFFF9933),
                                        Color(0xFFFFFFFF),
                                        Color(0xFF128807)
                                    )
                                ),
                                shape = MaterialTheme.shapes.large
                            )
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "\"यूनान-ओ-मिस्र-ओ-रूमा, सब मिट गए जहाँ से\n" +
                                    "अब तक मगर है बाक़ी, नाम-ओ-निशाँ हमारा\n" +
                                    "कुछ बात है कि हस्ती मिटती नहीं हमारी\n" +
                                    "सदियों रहा है दुश्मन दौर-ए-ज़माँ हमारा\"",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                lineHeight = 30.sp,
                                color = Color.Black // Consistent for flag visibility
                            ),
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    // Material You Close Button
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { showPoem = false; logoTaps = 0 },
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        Text("Close")
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo (No Ripple)
        Icon(
            painter = painterResource(id = R.drawable.root_logo),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(160.dp)
                .clickable(indication = null, interactionSource = noRipple) {
                    logoTaps++
                    if (logoTaps == 108) showPoem = true
                }
        )

        // Developer Text (No Ripple)
        Text(
            text = buildAnnotatedString {
                append("Developer: ")
                withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) { append("Chill-Astro Software") }
            },
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier
                .padding(top = 16.dp)
                .clickable(indication = null, interactionSource = noRipple) {
                    if (++bTaps == 5) {
                        Toast.makeText(ctx, "Chill-Astro Software - TRANSPARENT BY DESIGN", Toast.LENGTH_SHORT).show()
                        bTaps = 0
                    }
                }
        )

        // Version Text (No Ripple)
        Text(
            text = buildAnnotatedString {
                append("Version: ")
                withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) { append(appVersion ?: "1.0.0") }
            },
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable(indication = null, interactionSource = noRipple) {
                vTaps++
                val msg = when (vTaps) {
                    5 -> "Hi there! You Found me. :)"
                    10 -> "I hope you like the App! ^_^"
                    15 -> "Ok now you are just poking me...."
                    25 -> "Ok its not funny. Now its hurting my screen."
                    50 -> "Does Tapping give you anything?"
                    100 -> "Or maybe you are an autoclicker?"
                    150 -> "At this point, just visit a Mental Health Doctor."
                    else -> null
                }
                msg?.let { Toast.makeText(ctx, it, if(vTaps >= 150) Toast.LENGTH_LONG else Toast.LENGTH_SHORT).show() }
            }
        )

        TextButton(
            modifier = Modifier.padding(top = 8.dp),
            onClick = { ctx.startActivity(Intent(Intent.ACTION_VIEW, "https://github.com/Chill-Astro/FOSS-Root-Checker".toUri())) }
        ) {
            Icon(Icons.Rounded.Code, null, Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text(text = "Official Repository")
        }

        HorizontalDivider(Modifier.padding(vertical = 24.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "PREFERENCES", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)

            ListItem(
                headlineContent = { Text("Dark Theme") },
                leadingContent = { Icon(Icons.Rounded.Brush, null) },
                trailingContent = { Switch(checked = dark, onCheckedChange = onDark) }
            )

            if (Build.VERSION.SDK_INT >= 31) {
                ListItem(
                    headlineContent = { Text("Use System Colours") },
                    leadingContent = { Icon(Icons.Rounded.Palette, null) },
                    trailingContent = { Switch(checked = dyn, onCheckedChange = onDyn) }
                )
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
        if (logs.isEmpty()) Box(Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) { Text("Aww no History? Let's make some!", modifier = Modifier.alpha(0.4f)) }
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

fun isSUWorking(): Boolean {
    return try {
        // Run the command "id" via su
        val process = Runtime.getRuntime().exec(arrayOf("su", "-c", "id"))

        // Read the output
        val output = process.inputStream.bufferedReader().use { it.readLine() }

        // Wait for the process to finish
        process.waitFor()

        // Check if the output contains "uid=0" (which signifies root)
        output?.contains("uid=0") == true
    } catch (e: Exception) {
        // If "su" is not found or permission is denied, an exception is thrown
        false
    }
}

fun findBusyBoxPath(): String {
    val paths = arrayOf("/system/xbin/busybox", "/system/bin/busybox", "/data/adb/magisk/busybox")
    return paths.firstOrNull { java.io.File(it).exists() } ?: ""
}

fun saveLog(c: Context, r: Boolean) {
    val p = c.getSharedPreferences("su_logs", Context.MODE_PRIVATE)
    // Original history format relied on 4 parts for the UI to render
    val entry = "${if (r) "OK" else "NO"}|${SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(Date())}|${Build.MODEL}|${Build.VERSION.RELEASE}"
    val set = p.getStringSet("logs", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
    set.add("${System.currentTimeMillis()}_$entry")
    p.edit { putStringSet("logs", set) }
}

fun getLogs(c: Context): List<String> = c.getSharedPreferences("su_logs", Context.MODE_PRIVATE).getStringSet("logs", emptySet())?.toList()?.sortedByDescending { it.substringBefore("_") }?.map { it.substringAfter("_") } ?: emptyList()

fun clearLogs(c: Context) = c.getSharedPreferences("su_logs", Context.MODE_PRIVATE).edit { remove("logs") }