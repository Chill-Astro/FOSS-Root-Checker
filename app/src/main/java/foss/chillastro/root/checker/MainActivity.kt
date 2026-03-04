@file:OptIn(ExperimentalMaterial3Api::class)
package foss.chillastro.root.checker

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
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.rounded.Brush
import androidx.compose.material.icons.rounded.Check
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
import androidx.compose.material.icons.rounded.Nightlight
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Smartphone
import androidx.compose.material.icons.rounded.Tag
import androidx.compose.material.icons.rounded.Terminal
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
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

// Getting Hardware Props (for Decoration I mean for Informing!)
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
                caRootChecker(
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
fun caRootChecker(dark: Boolean, onDark: (Boolean) -> Unit, dyn: Boolean, onDyn: (Boolean) -> Unit) { // CA -> Chill-Astro. An Astronaut who's not an Astronaut!
    var dest by rememberSaveable { mutableStateOf(AppDestinations.HOME) }
    var showHistorySheet by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var logs by remember { mutableStateOf(getLogs(context)) } // 🪵 <- A LOG (literally 😅)
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
                    AppDestinations.HOME -> rootChecker(onCheckComplete = refreshLogs)
                    AppDestinations.BUSYBOX -> busybox()
                    AppDestinations.GUIDE -> rootGuide()
                    AppDestinations.SETTINGS -> settings(dark, onDark, dyn, onDyn)
                }
            }
        }

        if (showHistorySheet) {
            ModalBottomSheet(onDismissRequest = { showHistorySheet = false }) {
                historyContent(logs = logs, onClear = { clearLogs(context); refreshLogs() })
            }
        }
    }
}

// User INTERFACE (I hope it works like butter on Dumpster Fire Devices!)
@Composable
fun rootChecker(onCheckComplete: () -> Unit) {
    var checkState by rememberSaveable { mutableIntStateOf(0) }
    var isRooted by rememberSaveable { mutableStateOf(false) }
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val bootloader = remember { HardwareProbe.getBootloader() }
    val verity = remember { HardwareProbe.getVerity() }
    val circleScale by animateFloatAsState(
        targetValue = if (checkState == 1 || checkState == 3) 1.15f else 1f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow),
        label = "pulse"
    )
    Column(
        Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Info Pill (Good Hardware Props)
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
        Box(contentAlignment = Alignment.Center) {
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
        // "Trasparency"
        Text(
            text = when(checkState) {
                1 -> "Searching for Paths..."
                2 -> "Your Device is Rooted"
                3 -> "Interrogating SU Binary..."
                4 -> if (isRooted) "Your Device is Rooted" else "Root Access not Available"
                else -> "Ready to verify?" // Nope
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
                isRooted = false

                scope.launch(Dispatchers.IO) {
                    delay(1000) // Visual pause
                    // A Rabbit Hole of a HELL LOT OF PATHS! (Feels like I am a Security Official tbh.)
                    val paths = arrayOf(
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
                        // Kernel SU or APatch
                        "/data/adb/ksu",
                        "/data/adb/ksu/bin/su",
                        "/data/adb/apatch",
                        "/data/adb/apatch/bin/su",
                        "/data/adb/ap/bin/su",
                        "/sys/kernel/debug/tracing/su",
                        "/proc/kernelsu",
                        "/dev/ksu",
                        "/dev/apatch",
                        // Old Root Methods + Other Stuff that need Root.
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
                        "/system/xbin/busybox",
                        "/system/bin/busybox",
                        "/sbin/busybox",
                        "/vendor/bin/busybox",
                        "/data/local/busybox"
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
                                Toast.makeText(ctx, "Root Access is Verified. Nice Spoofing! :)", Toast.LENGTH_LONG).show() // Ms. Mobile is Rooted (+ Satire for Spoofing!)
                            } else {
                                Toast.makeText(ctx, "Root Access Verified", Toast.LENGTH_SHORT).show() // Ms. Mobile not Rooted
                            }
                            saveLog(ctx, isRooted)
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
                                Toast.makeText(ctx, "Root Access is Verified. Nice Spoofing! :)", Toast.LENGTH_LONG).show() // Ms. Mobile is Rooted (Again)
                            } else if (isRooted) {
                                Toast.makeText(ctx, "Root Access Verified", Toast.LENGTH_SHORT).show() // Ms. Mobile not Rooted (Again)
                            } else {
                                Toast.makeText(ctx, "Root Access not Available", Toast.LENGTH_SHORT).show()
                            }
                            saveLog(ctx, isRooted)
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
fun busybox() { // Let's hope Busybox isn't Busy!
    var checkState by remember { mutableIntStateOf(0) }
    var foundPath by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val ctx = LocalContext.current
    Column(Modifier.fillMaxSize().padding(24.dp)) {
        Spacer(Modifier.height(16.dp))
        Box(Modifier.fillMaxWidth().weight(1f).clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.surfaceContainerHigh).padding(16.dp).verticalScroll(rememberScrollState())) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                terminalLine("NOTE : Most Modern Root Solutions hide their BusyBox Installation to Avoid Detection!")
                terminalLine("Install 'BusyBox for NDK Module' if needed...") // For turning Android into LINUX ig
                HorizontalDivider(Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant)
                terminalLine("Ready to Verify?") // Nope
                if (checkState == 2) {
                    terminalLine("Searching for BusyBox Paths...  ♪(´▽｀)")
                    Thread.sleep(1000)
                    if (foundPath.isNotEmpty()) {
                        terminalLine("BusyBox Path Verified!", MaterialTheme.colorScheme.primary)
                        terminalLine("$foundPath", MaterialTheme.colorScheme.primary)
                        Toast.makeText(ctx, "BusyBox found via Path", Toast.LENGTH_SHORT).show()
                    }
                    else { terminalLine("Busybox not Found in Path!", MaterialTheme.colorScheme.error)
                        Toast.makeText(ctx, "BusyBox not Found. Is it Installed?", Toast.LENGTH_SHORT).show()
                        terminalLine("Fine, but su Never Lies! ^_~")
                        terminalLine("Launching Shell....")
                        terminalLine("usr@android $ su")
                        findBusyBoxPathBySU().takeIf { it.isNotEmpty() }?.let { path ->
                            // This runs if a path is found
                            terminalLine("root@android $ which busybox")
                            terminalLine("BusyBox Path Verified!", MaterialTheme.colorScheme.primary)
                            Toast.makeText(ctx, "BusyBox found via Path as Root Nice Spoofing! :)", Toast.LENGTH_SHORT).show()
                            terminalLine(path, MaterialTheme.colorScheme.primary)
                            terminalLine("root@android $ exit")
                        } ?: run {
                            // This runs if the method returns ""
                            terminalLine("Busybox not Found in Path as Root!", MaterialTheme.colorScheme.error)
                            Toast.makeText(ctx, "BusyBox not Installed.", Toast.LENGTH_SHORT).show()
                        }
                        // This line displays regardless of the result
                        terminalLine("usr@android $ _")
                    }
                }
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
fun rootGuide() {
    var menuPath by rememberSaveable { mutableStateOf("MAIN") }
    val slot = remember { HardwareProbe.getProp("ro.boot.slot_suffix").replace("_", "").ifEmpty { "" } }
    val kernelVersion = remember { HardwareProbe.getProp("ro.kernel.version") } // Calling Ms. Kernel. I hope Madam Responds!
    val isAB = slot.isNotEmpty()
    BackHandler(menuPath != "MAIN") { menuPath = "MAIN" }
    AnimatedContent(
        targetState = menuPath,
        modifier = Modifier.fillMaxSize(),
        transitionSpec = {
            fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
        }, label = "SubMenuTransition"
    ) { targetPath ->
        Column(Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            when (targetPath) {
                // CHILL-ASTRO PRESENTS : A GOATED ROOTING GUIDE 🐐🐐🐐🐐🐐🐐🐐🐐🐐
                // THIS IS NOT AI-GENERATED. I WROTE EVERYTHING! 🥺
                "MAIN" -> {
                    WarningCard("Never trust 'One-Click Root' Apps and Please BE CAREFUL while following this guide. I am not responsible for any damages to your device.")
                    Text("GUIDE SECTIONS", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    navCard("1. Rooting: An Introduction", Icons.AutoMirrored.Rounded.LibraryBooks) { menuPath = "INTRO" }
                    navCard("2. Unlocking Bootloader", Icons.Rounded.LockOpen) { menuPath = "UNLOCK" }
                    navCard("3. Rooting Methods", Icons.Rounded.Tag) { menuPath = "METHODS" }
                    navCard("4. Root Hiding", Icons.Rounded.VisibilityOff) { menuPath = "HIDING" }
                }
                "INTRO" -> {
                    GuideHeader("Rooting : An Introduction", onBack = { menuPath = "MAIN" })
                    WarningCard("Please BE CAREFUL what apps you are giving Root Permissions to. I am not responsible for Data or Money Theft by Malware on your Device.")
                    InfoBlock("Introduction : What is Rooting?", "\nRooting an Android device means gaining full administrative (superuser) control, similar to an administrator on a computer, by unlocking deep system access restricted by manufacturers.")
                    InfoBlock("What is Bootloader?", "\nA bootloader is the first piece of software that runs every time you turn on your Android device. It acts as a security guard and a guide, directing the hardware on how to start up and which operating system to \"hand off\" control to. This is locked by default to ensure stability and prevent malware from infecting the device.\n" +
                            "\n" +
                            "Since hardware components (like the processor and memory) don't know how to talk to the Android OS immediately upon receiving power, the bootloader provides the necessary instructions to bridge that gap.")
                    InfoBlock("Functions of Bootloader : ", "\n• Hardware Initialization: It \"wakes up\" the processor, RAM, and storage.\n• Security Check: It verifies the integrity of the boot and recovery partitions to ensure the software hasn't been tampered with (a process known as Android Verified Boot). This is why we Unlock Bootloaders to Root as we are modifying System Partitions.\n• OS Loading: Once verified, it locates the Android kernel and loads it into the system memory to start the actual operating system.\n• Selection Mode: It allows you to boot into different modes, such as Recovery Mode (for system repairs) or Fastboot/Download Mode (for flashing software). ")
                    InfoBlock("Locked vs Unlocked Bootloader : ","\n- Features of Locked Bootloader : \n\n" +
                            "• Security: Prevents unauthorized software or malware from being installed at the deepest level of the device.\n" +
                            "• Stability: Ensures the device only runs the version of Android specifically optimized for its hardware.\n" +
                            "• Warranty: Modifying the bootloader often voids manufacturer warranties.\n" +
                            "\n- Features of Unlocked Bootloader : \n\n" +
                            "• Enables Deep Customization : Allows users to install Custom ROMs (like LineageOS), Generic System Images (GSI) or different versions of Android by allowing us to Boot Third Party Firmware.\n" +
                            "• Allows Root Access: An unlocked bootloader is usually a prerequisite for gaining \"root\" (administrative) privileges because it allows booting modified boot files.")
                    InfoBlock("Pros of Rooting :", "\n✓ Bloatware Removal\n✓ System-wide Adblocking\n✓ Overclocking and Underclocking Device\n✓ Modifying User Experience\n✓ Deep level Customization\n✓ Full Data Backups")
                    InfoBlock("Cons of Rooting :", "\n✗ Usually Voids Warranty\n✗ Increased Security Risks\n✗ Loss of Hardware Encoding\n✗ No Official Updates (OTA)\n✗ Data loss\n✗ Risk of Bricking Device")
                    InfoBlock("What is Bricking?\n", "Bricking refers to a device becoming completely non-functional, usually due to a corrupted software update or a failed firmware modification.")
                    InfoBlock("Types of Bricking and How to fix them :\n","1. Soft Brick : A soft brick is a \"recoverable\" state. The device might be stuck in a boot loop (constantly restarting at the logo) or booting straight into recovery mode.\n\n" +
                            "- The Cause : Usually a minor software error, incompatible app, or a failed \"rooting\" attempt.\n" +
                            "- The Fix: Can often be fixed by a factory reset, clearing the cache, or reflashing the original firmware using a computer.\n\n" +
                            "2. Hard Brick : A hard brick is much more serious. The device shows no signs of life. No lights, no vibration, and the screen remains black.\n\n" +
                            "- The Cause: This happens when the bootloader (the \"first-stage\" software we discussed earlier) or the kernel is corrupted or deleted.\n" +
                            "- The Fix: This often requires specialized hardware tools to bypass the main software, or in many cases, a physical replacement of the motherboard. Tools suck as SP Flash Tool and MTKClient can do this Work. However FASTBOOT is not Accessible during this time.")
                    InfoBlock("What is Device Mapper Verity (dm-verity)?\n", "Device Mapper Verity is a transparent integrity checking feature of the Linux kernel. Its sole job is to ensure that the data on critical partitions (like /system, /vendor, or /product) has not been modified even by a single bit. This is why it is Sometimes Patched or Made Blank when Modding.")
                    InfoBlock("How does dm-verity work?\n", "1. The Layered Hashes\n" +
                            "\n" +
                            "The system creates a \"Hash Tree\" (Merkle Tree).\n" +
                            "\n" +
                            "• It hashes every 4KB block on the partition.\n" +
                            "\n" +
                            "• It then hashes those hashes.\n" +
                            "\n" +
                            "• It keeps doing this until only one hash remains at the very top.\n" +
                            "\n" +
                            "2. The Root Hash\n" +
                            "\n" +
                            "This final single hash is called the Root Hash. This hash is digitally signed by the manufacturer (Google, Samsung, etc.) and stored in a read-only area (the VBMeta partition).\n" +
                            "3. Real-Time Verification\n" +
                            "\n" +
                            "When the Android OS wants to read a file:\n" +
                            "\n" +
                            "• The kernel reads the 4KB block from the disk.\n" +
                            "\n" +
                            "• It calculates the hash of that block.\n" +
                            "\n" +
                            "• It compares it against the \"parent\" hash in the tree, all the way up to the Root Hash.\n" +
                            "\n" +
                            "• If the math doesn't match perfectly, it knows the block was tampered with.")
                    InfoBlock("Suggestion from My Experience :", "\nAs from my little experience from Rooting, use Magisk if you are not sure. It works on almost every device and it can be flashed with PC and Custom Recovery ( like TWRP or OrangeFox ) and does the job very well. Unless your device is old, DO NOT USE EXPLOITS! I had soft-bricked my own device like this so BE CAREFUL! If you want to explore more options, I recommend APatch and KernelSU ( if Supported ). They don't work on every device but are pretty reliable.")
                }
                "UNLOCK" -> {
                    GuideHeader("Unlocking Bootloader", onBack = { menuPath = "MAIN" })
                    WarningCard("This process will wipe all user data. Ensure you have a backup before proceeding. Also Xiaomi, Oppo and Realme have Additional Steps. Vivo, iQOO and certain Manufacturers don't support Bootloader Unlocking.")
                    expandableMethod("Fastboot Method (Recommended)", Icons.Rounded.Computer) {
                        Text("Step 1 : Reboot Phone to Bootloader :")
                        codeBox("$ adb reboot bootloader")
                        Text("Step 2 : Unlock Bootloader using Fastboot :")
                        Text(" • For most devices :")
                        codeBox("$ fastboot flashing unlock")
                        Text(" • For some older devices :")
                        codeBox("$ fastboot oem unlock")
                        Text("Pros :\n✓ Unlocking doesn't brick device immediately.\n✓ Safe and Easy to Use.\n\nCons :\n✗ Not available on all devices.\n✗ Xiaomi Devices need permission from Xiaomi Community and then Mi Unlock Tool is used.\n✗ Oppo and Realme Devices use 'Deep Testing' or 'In-Depth Test' for Fastboot Permissions.")
                    }
                    expandableMethod("Device Unlock Mode (for Samsung)", Icons.Rounded.Smartphone) {
                        WarningCard("NOTE : I don't own a Samsung Device. This is the General Information I have. Also, this disables KNOX Security permanently and many Samsung Apps Stop Working.")
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Step 1 : Turn on 'OEM Unlocking' in Developer Options.")
                            Text("Step 2 : Connect your Charging Cable to a PC (but not Phone).")
                            Text("Step 3 : Press and hold Volume Up + Volume Down simultaneously.")
                            Text("Step 4 : While holding both buttons, plug the USB cable into your phone.")
                            Text("Step 5 : Release the buttons when you see a teal/blue warning screen.")
                            Text("Step 6 : Press Volume Up once to continue to Download Mode.")
                            Text("Step 7 : Long Press Volume Up for Unlocking Device.")
                        }
                    }
                    expandableMethod("MTKClient (For MTK Devices)", Icons.Rounded.Memory) {
                        WarningCard("Please BE CAREFUL as it doesn't work on very new device and can cause 'System is Destroyed' and 'dm-verity corruption' Ensure that your device has no Replay Protected Memory Block (RPMB) before proceeding.")
                        Text("Hardware-level bypass for locked MediaTek chipsets.\n\nFirst install USBdk if using Windows (Recommended).\n\nNOTE: For Each Step, Run the Command, Press both Volume Buttons and Connect Phone to PC.\n")
                        Text("Step 1 : Dump vbmeta : ")
                        codeBox("$ python mtk.py r vbmeta_a,vbmeta_b vbmeta_a.img,vbmeta_b.img")
                        codeBox("$ python mtk.py r vbmeta vbmeta.img # For Old Devices")
                        Text("Step 2 : Unlock Bootloader : ")
                        codeBox("$ python mtk.py da seccfg unlock")
                        Text("Step 3 : Disable dm-verity (Easy Way) :  ")
                        codeBox("$ python mtk.py da vbmeta 3")
                        Text("Step 4 : Erase Userdata : ")
                        codeBox("$ python mtk.py e metadata,userdata")
                        Text("Step 5 : Reboot Device : ")
                        codeBox("$ python mtk.py reset")
                        Text("Pros :\n✓ Easy to Recover with Backups.\n✓ Can fix Hard-Bricks.\n✓ Fast and Easy to Use.\n\nCons :\n✗ Does not Support QualComm and UniSOC Devices.\n✗ High Chances of Bricking.\n✗ Doesn't work on very new devices.\n✗ Fastboot may not be usable as on Realme Devices.\n")
                        linkCard("mtkclient by @bkerler", "https://github.com/bkerler/mtkclient")
                    }
                }
                "HIDING" -> {
                    GuideHeader("Rooting Hiding : An Introduction", onBack = { menuPath = "MAIN" })
                    WarningCard("NOTE : This allows you to Bypass Root Checks used by Banking apps for YOUR FINANCIAL SAFETY! Please be cautious while hiding Root.")
                    InfoBlock("Introduction : What is Rooting Hiding?", "\nNow that your Device is Unlocked and Rooted, it's time to Hide this Unlocked Status! Basically, certain Apps like Banking Apps and Game with Anti-Cheat check the prescence of Zygisk, Magisk, the 'su' Binary and many more ( for user safety ). But with the power of Systemless Rooting and 'Modules', the device can give a Software-Level Lie to ALL APPS!\n\nBefore Starting Ensure to Enable 'Zygisk' in 'Magisk Settings' or Install ReZygisk or Zygisk Next (Closed Source) in Magisk ( with Built-in Zygisk turned OFF ) , KernelSU or APatch before Flashing these.\n")
                    InfoBlock("Root Hiding Modules :", "\n1. Tricky Store (Recommended) :\n\nThis Module spoofs Hardware Backed Attestation by Software / Hardware Trusted Execution Environment (TEE) by injecting a Valid 'KeyBox.xml'.\n\nThis combined with Tricky Addon and a WebUI Interface can make this Process EASY!\n\nFirst obtain the .ZIP Files from these two links and Flash them. After Reboot Tap the 'Action' Button under Tricky Store and in WebUI, Select All Apps and Tap 'Set Valid Keybox'.")
                    linkCard("Tricky Store by @5ec1cff", "https://github.com/5ec1cff/TrickyStore")
                    linkCard("Tricky Addon by @KOWX712", "https://github.com/KOWX712/Tricky-Addon-Update-Target-List")
                    Text("\n2. Shamiko (Closed Source) :\n\nUsed to hide Root Status and ALL TRACES OF ZYGISK AND ROOT PATHS and it Fakes the UNLOCKED Status of Bootloader!\n\nGet the Module from the Latest Release and Flash it.")
                    linkCard("Shamiko by @LSPosed","https://github.com/LSPosed/LSPosed.github.io/releases/")
                    Text("\n3. Play Integrity Fix (For Custom ROM Users) :\n\nThis Assigns a Valid Fingerprint of a Locked Device Systemlessly.\n\nFlash any 1 of these Modules and Tap the 'Action' Button under Module on Reboot.")
                    linkCard("Play Integrity Fork by @osm0sis","https://github.com/osm0sis/PlayIntegrityFork")
                    linkCard("Play Integrity Fix by @KOWX712","https://github.com/KOWX712/PlayIntegrityFix")
                }
                "METHODS" -> {
                    GuideHeader("Rooting Methods", onBack = { menuPath = "MAIN" })
                    WarningCard("Please download the following apps from their Official Sources. Do not modify or delete System Files. Do not use 'One-Click' Root Apps. Do not flash them on a Device with a Locked Bootloader.")
                    expandableMethodLocal("Magisk (Recommended)", R.drawable.ic_magisk) {
                        Text("First obtain your stock boot.img or init_boot.img and patch it using Magisk App and then Flash it.\n")
                        FlashLogic(isAB, slot, true)
                        Text("Pros :\n✓ Truly Systemless\n✓ Widest Module Support\n✓ Works on pretty much anything.\n✓ Best possible documentation and compatibility.\n\nCons :\n✗ Easily Detectable as it leaves Traces.\n")
                        linkCard("Magisk by @topjohnwu", "https://github.com/topjohnwu/Magisk")
                    }
                    expandableMethodLocal("Magisk Guide for Samsung", R.drawable.ic_magisk) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Unlike other Manufacturers who use FASTBOOT, Samsung uses ODIN for Flashing. This is one of the Key Reasons why Rooting a Samsung Device is Harder. You don't flash a 32 MB to 64 MB .img File, but the ENTIRE Firmware with a Part Modified. Ok I will Explain!\n\nHere are the Named '.tar' files you get in Stock Firmware :\n\n1. BL -> Contains the Bootloader.\n2. AP -> Contains the Kernel, Recovery and System.\n3. CP -> Conatins Modem Firmware.\n4. CSC -> Contains Region-Specefic Stuff\n5. HOME_CSC -> Same as CSC.\n6. USERDATA -> Contains your Data ( Blank by Default ).\n")
                            Text("Follow these steps to Flash Modified AP :")
                            Text("STEP 1 : Download Stock Firmware and Transfer the '.tar' having the Text 'AP' in its Name to your Device.")
                            Text("STEP 2 : Patch this '.tar' and Transfer it to PC.")
                            Text("STEP 3 : In Odin ( Windows Only ), Select the Respective '.tar' files to their Respective Flash Prompts except AP.")
                            Text("STEP 4 : Select your Magisk Modified AP '.tar' file and Leave Userdata Blank. Flash all of this.")
                            Text("STEP 5 : If the Phone Bootloops and boots into Recovery, Wipe User Data and Reboot Again.")
                        }
                    }
                    expandableMethodLocal("KernelSU", R.drawable.ic_ksu) {
                        if(kernelVersion < "5.10") WarningCard("This Device doesn't Support KernelSU. You have to compile your Device's Kernel and inteegrate KernelSU into it YOURSELF! Else follow these steps on Another Device!")
                        Text("First obtain your stock boot.img or init_boot.img and patch it using KernelSU App and then Flash it.\n")
                        FlashLogic(isAB, slot, true)
                        Text("Pros :\n✓ Fully Systemless.\n✓ Very hard to detect by Banking Apps.\n✓ Leaves no Traces.\n\nCons :\n✗ Only Supports devices with Generic Kernel Image.\n")
                        linkCard("KernelSU by @tiann", "https://github.com/tiann/KernelSU")
                        terminalLine("Alternative Forks for Older Devices :", MaterialTheme.colorScheme.primary)
                        linkCard("KernelSU Next by @KernelSU-Next", "https://github.com/KernelSU-Next/KernelSU-Next")
                        linkCard("SkiSU Ultra by @SkiSU-Ultra", "https://github.com/SkiSU-Ultra/SkiSU-Ultra")
                    }
                    expandableMethodLocal("APatch", R.drawable.ic_apatch) {
                        if(kernelVersion < "5.10") WarningCard("This device may or may not Support APatch! Use it AT YOUR OWN RISK!")
                        Text("First obtain your stock boot.img and patch it using Apatch App and then Flash it.\n")
                        FlashLogic(isAB, slot, false)
                        Text("Pros :\n✓ Fully Systemless.\n✓ Very hard to detect by Banking Apps.\n✓ Leaves no Traces.\n✓ Doesn't need a GKI Device.\n\nCons :\n✗ Doesn't work on every device.\n")
                        linkCard("APatch by @bmax121", "https://github.com/bmax121/APatch")
                    }
                }
            }
        }
    }
}
@Composable
fun FlashLogic(isAB: Boolean, slot: String, hasInit: Boolean) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (isAB) {
            if (slot == "a") {
                Text("Flash to Active Slot : ")
                codeBox("$ fastboot flash boot_a patched.img")
                if (hasInit) codeBox("$ fastboot flash init_boot_a patched.img")
                Text("Flash to Inactive Slot if Needed : ")
                codeBox("$ fastboot flash boot_b patched.img")
                if(hasInit) codeBox("$ fastboot flash init_boot_b patched.img")
                Text("For Older Devices : ")
                codeBox("$ fastboot flash boot patched.img")
            }
            if (slot == "b") {
                Text("Flash to Active Slot : ")
                codeBox("$ fastboot flash boot_b patched.img")
                if (hasInit) codeBox("$ fastboot flash init_boot_b patched.img")
                Text("Flash to Inactive Slot if Needed : ")
                codeBox("$ fastboot flash boot_a patched.img")
                if(hasInit) codeBox("$ fastboot flash init_boot_a patched.img")
                Text("For Older Devices : ")
                codeBox("$ fastboot flash boot patched.img")
            }
        } else {
            Text("For Older Devices : ")
            codeBox("$ fastboot flash boot patched.img")
            Text("Commands not for this Device : ")
            codeBox("$ fastboot flash boot_a patched.img")
            if(hasInit) codeBox("$ fastboot flash init_boot_a patched.img")
            codeBox("$ fastboot flash boot_b patched.img")
            if(hasInit) codeBox("$ fastboot flash inti_boot_b patched.img")

        }
    }
}
@Composable
fun navCard(title: String, icon: ImageVector, onClick: () -> Unit) {
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
fun expandableMethod(title: String, icon: ImageVector, content: @Composable ColumnScope.() -> Unit) {
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
fun expandableMethodLocal(title: String, resId: Int, content: @Composable ColumnScope.() -> Unit) {
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
fun codeBox(cmd: String) {
    Surface(color = Color.Black, shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(cmd, color = Color.White, modifier = Modifier.padding(8.dp), fontFamily = FontFamily.Monospace, fontSize = 11.sp)
    }
}
@Composable
fun terminalLine(text: String, color: Color = MaterialTheme.colorScheme.onSurfaceVariant) {
    Text(text, color = color, fontFamily = FontFamily.Monospace, fontSize = 13.sp)
}
@Composable
fun linkCard(t: String, url: String) {
    val ctx = LocalContext.current
    OutlinedCard(onClick = { ctx.startActivity(Intent(Intent.ACTION_VIEW, url.toUri())) }, modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(t, Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Icon(Icons.AutoMirrored.Rounded.OpenInNew, null, Modifier.size(14.dp))
        }
    }
}
@Composable
fun settings(
    dark: Boolean,
    onDark: (Boolean) -> Unit,
    dyn: Boolean,
    onDyn: (Boolean) -> Unit
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val noRipple = remember { MutableInteractionSource() }
    var showLicense by remember { mutableStateOf(false) }
    var bTaps by remember { mutableIntStateOf(0) }
    var vTaps by remember { mutableIntStateOf(0) }
    var logoTaps by remember { mutableIntStateOf(0) }
    var showPoem by remember { mutableStateOf(false) }
    var isChecking by remember { mutableStateOf(false) }
    val appVersion = remember {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ctx.packageManager.getPackageInfo(ctx.packageName, PackageManager.PackageInfoFlags.of(0)).versionName
            } else {
                @Suppress("DEPRECATION")
                ctx.packageManager.getPackageInfo(ctx.packageName, 0).versionName
            }
        } catch (e: Exception) { "36.23.1.0" }
    }
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
    fun performUpdateCheck() {
        scope.launch(Dispatchers.IO) {
            isChecking = true
            try {
                val url = "https://gist.githubusercontent.com/Chill-Astro/b8d2cb9ba2ea314babf65de1bed88662/raw/FRC-SU_V.txt"
                val remoteVersion = URL(url).readText().trim()
                withContext(Dispatchers.Main) {
                    if (isNewer(appVersion ?: "36.23.1.0", remoteVersion)) {
                        Toast.makeText(ctx, "🎉 $remoteVersion OUT NOW!", Toast.LENGTH_LONG).show()
                    } else if (isNewer(appVersion ?: remoteVersion, "36.23.1.0")) {
                        Toast.makeText(ctx, "⚠️ You are using an UNSTABLE BUILD!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(ctx, "🎉 Your Version is UP TO DATE!", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(ctx, "❌ Please Verify Internet Connection!", Toast.LENGTH_SHORT).show()
                }
            } finally {
                isChecking = false
            }
        }
    }
    if (showLicense) {
        BasicAlertDialog(onDismissRequest = { showLicense = false }) {
            Surface(
                shape = MaterialTheme.shapes.extraLarge,
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                modifier = Modifier
                    .widthIn(max = 500.dp)
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "MIT License",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(32.dp))
                    Box(modifier = Modifier.heightIn(max = 300.dp).verticalScroll(rememberScrollState())) {
                        Text(
                            text = """
                            MIT License
                            
                            Copyright (c) 2025 Dev. Chill-Astro
                            
                            Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
                            
                            The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
                            
                            THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
                        """.trimIndent(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(Modifier.height(24.dp))
                    TextButton(
                        modifier = Modifier.align(Alignment.End),
                        onClick = { showLicense = false }
                    ) {
                        Text("Dismiss")
                    }
                }
            }
        }
    }
    if (showPoem) {
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = R.drawable.root_logo),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(160.dp)
                .clickable(indication = null, interactionSource = noRipple) {
                    logoTaps++
                    if (logoTaps == 5) Toast.makeText(ctx, "It is our duty to pay for our liberty...", Toast.LENGTH_SHORT).show()
                    if (logoTaps == 108) {
                        Toast.makeText(ctx, "वन्दे मातरम्!", Toast.LENGTH_SHORT).show()
                        showPoem = true
                        logoTaps = 0
                    }
                }
        )
        Text(
            text = buildAnnotatedString {
                append("Developer: ")
                withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) { append("Chill-Astro Software") }
            },
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(top = 16.dp).clickable(indication = null, interactionSource = noRipple) {
                if (++bTaps == 5) {
                    Toast.makeText(ctx, "Chill-Astro Software - TRANSPARENT BY DESIGN", Toast.LENGTH_SHORT).show()
                    bTaps = 0
                }
            }
        )
        Text(
            text = buildAnnotatedString {
                append("Version: ")
                withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) { append(appVersion ?: "36.23.1.0") }
            },
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable(indication = null, interactionSource = noRipple) {
                vTaps++
                if (vTaps == 5) Toast.makeText(ctx, "Hi there! You Found me. :)", Toast.LENGTH_SHORT).show()
            }
        )
        TextButton(
            modifier = Modifier.padding(top = 8.dp),
            onClick = { ctx.startActivity(Intent(Intent.ACTION_VIEW, "https://github.com/Chill-Astro/FOSS-Root-Checker".toUri())) }
        ) {
            Icon(Icons.Rounded.Code, null, Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Official Repository")
            Spacer(Modifier.width(12.dp))
        }
        TextButton(onClick = { showLicense = true }) {
            Icon(Icons.Rounded.Info, null, Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("MIT LICENCE")
        }
        HorizontalDivider(Modifier.padding(vertical = 24.dp))
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.align(Alignment.Start),
                text = "PREFERENCES",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            ListItem(
                headlineContent = { Text("Dark Theme") },
                leadingContent = { Icon(Icons.Rounded.Nightlight, null) },
                trailingContent = { Switch(checked = dark, onCheckedChange = onDark) }
            )
            if (Build.VERSION.SDK_INT >= 31) {
                ListItem(
                    headlineContent = { Text("Use System Colours") },
                    leadingContent = { Icon(Icons.Rounded.Palette, null) },
                    trailingContent = { Switch(checked = dyn, onCheckedChange = onDyn) }
                )
            }
            Spacer(Modifier.height(16.dp))
            FilledTonalButton(
                onClick = { if (!isChecking) performUpdateCheck() },
                modifier = Modifier.width(220.dp),
                shape = CircleShape,
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                if (isChecking) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                } else {
                    Icon(Icons.Rounded.Refresh, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Check for Updates")
                }
            }
        }
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 24.dp).fillMaxWidth(0.3f),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )
        Text(
            text = "Made with 💖 by Chill-Astro",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
@Composable
fun historyContent(logs: List<String>, onClear: () -> Unit) {
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
                        Column { Text(if (p[0] == "OK") "Rooted ${Build.MODEL}" else "Non-Rooted ${Build.MODEL}", fontWeight = FontWeight.Bold); Text("${p[1]} • Android ${p[3]}", style = MaterialTheme.typography.bodySmall, color = Color.Gray) }
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
        val process = Runtime.getRuntime().exec(arrayOf("su", "-c", "id"))
        val output = process.inputStream.bufferedReader().use { it.readLine() }
        process.waitFor()
        output?.contains("uid=0") == true // Root User is ALWAYS 0
    } catch (e: Exception) {
        false
    }
}
fun findBusyBoxPath(): String {
    val paths = arrayOf("/system/xbin/busybox", "/system/bin/busybox", "/data/adb/magisk/busybox", "/data/adb/magisk/busybox", "/data/adb/ksu/bin/busybox", "/data/adb/ap/bin/busybox")
    return paths.firstOrNull { java.io.File(it).exists() } ?: ""
}
fun findBusyBoxPathBySU(): String {
    return try {
        val process = Runtime.getRuntime().exec(arrayOf("su", "-c", "which busybox"))
        val output = process.inputStream.bufferedReader().use { it.readLine() }
        process.waitFor()
        if (output.isNullOrEmpty()) "" else output.trim()
    } catch (e: Exception) {
        ""
    }
}
fun saveLog(c: Context, r: Boolean) {
    val p = c.getSharedPreferences("su_logs", Context.MODE_PRIVATE)
    val entry = "${if (r) "OK" else "NO"}|${SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(Date())}|${Build.MODEL}|${Build.VERSION.RELEASE}"
    val set = p.getStringSet("logs", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
    set.add("${System.currentTimeMillis()}_$entry")
    p.edit { putStringSet("logs", set) }
}
fun getLogs(c: Context): List<String> = c.getSharedPreferences("su_logs", Context.MODE_PRIVATE).getStringSet("logs", emptySet())?.toList()?.sortedByDescending { it.substringBefore("_") }?.map { it.substringAfter("_") } ?: emptyList()
fun clearLogs(c: Context) = c.getSharedPreferences("su_logs", Context.MODE_PRIVATE).edit { remove("logs") }