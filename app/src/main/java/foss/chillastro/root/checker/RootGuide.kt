@file:OptIn(ExperimentalMaterial3Api::class, androidx.compose.material3.ExperimentalMaterial3ExpressiveApi::class)
package foss.chillastro.root.checker

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForwardIos
import androidx.compose.material.icons.automirrored.rounded.LibraryBooks
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.rounded.Animation
import androidx.compose.material.icons.rounded.BatteryChargingFull
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Cloud
import androidx.compose.material.icons.rounded.Computer
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.LockOpen
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.ReportProblem
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Tag
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material.icons.rounded.Android
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import foss.chillastro.root.checker.ui.theme.AppShapes

@Composable
fun WarningCard(bodyText: String) { // For newbies
    GuideCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
        shape = MaterialTheme.shapes.large
    ) {
        Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            GuidePointIcon(icon = Icons.Rounded.Warning, tone = MaterialTheme.colorScheme.error, iconSize = 32.dp)
            Spacer(Modifier.width(16.dp))
            Column {
                Text("WARNING!", fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onErrorContainer)
                Text(bodyText, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.9f))
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
        Column(Modifier.fillMaxSize().padding(8.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            when (targetPath) {
                // CHILL-ASTRO PRESENTS : A GOATED ROOTING GUIDE 🐐🐐🐐🐐🐐🐐🐐🐐🐐
                "MAIN" -> {
                    GuideCard(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Row(Modifier.padding(24.dp), verticalAlignment = Alignment.CenterVertically) {
                            GuidePointIcon(icon = Icons.Rounded.Warning, tone = MaterialTheme.colorScheme.error, iconSize = 36.dp)
                            Spacer(Modifier.width(20.dp))
                            Column {
                                Text("SAFETY FIRST!", fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onErrorContainer)
                                Text("Never trust 'One-Click Root' Apps", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.9f))
                            }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    SectionLabel("Quick Start Guide")
                    Spacer(Modifier.height(8.dp))
                    NavCard("1. Rooting: An Introduction", Icons.AutoMirrored.Rounded.LibraryBooks) { menuPath = "INTRO" }
                    Spacer(Modifier.height(6.dp))
                    NavCard("2. Unlocking Bootloader", Icons.Rounded.LockOpen) { menuPath = "UNLOCK" }
                    Spacer(Modifier.height(6.dp))
                    NavCard("3. Rooting Methods", Icons.Rounded.Tag) { menuPath = "METHODS" }
                    Spacer(Modifier.height(6.dp))
                    NavCard("4. Root Hiding", Icons.Rounded.VisibilityOff) { menuPath = "HIDING" }
                }
                "INTRO" -> {
                    GuideHeader("Rooting : An Introduction", onBack = { menuPath = "MAIN" })
                    Spacer(Modifier.height(16.dp))
                    GuideCard(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Row(Modifier.padding(24.dp), verticalAlignment = Alignment.CenterVertically) {
                            GuidePointIcon(icon = Icons.Rounded.Warning, tone = MaterialTheme.colorScheme.error, iconSize = 36.dp)
                            Spacer(Modifier.width(20.dp))
                            Column {
                                Text("PERMISSION WARNING!", fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onErrorContainer)
                                Text("Please BE CAREFUL what apps you are giving Root Permissions to", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.9f))
                            }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    SectionLabel("What is Rooting?")
                    Spacer(Modifier.height(8.dp))
                    GuideCard(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                            GuidePointIcon(icon = Icons.Rounded.LockOpen, tone = MaterialTheme.colorScheme.primary, iconSize = 28.dp)
                            Spacer(Modifier.width(16.dp))
                            Text("Rooting an Android device means gaining full administrative (superuser) control, similar to an administrator on a computer, by unlocking deep system access restricted by manufacturers.", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    SectionLabel("Benefits of Rooting")
                    Spacer(Modifier.height(8.dp))
                    listOf(
                        "Bloatware Removal" to Icons.Rounded.DeleteForever,
                        "System-wide Adblocking" to Icons.Rounded.Cancel,
                        "Advanced Theming (LSPosed)" to Icons.Rounded.Palette,
                        "Full Data Backups" to Icons.Rounded.Memory,
                        "Unlimited Google Photos Backups" to Icons.Rounded.Cloud,
                        "Higher FPS in Games" to Icons.Rounded.Animation,
                        "Sound Enhancement" to Icons.Rounded.VolumeUp,
                        "Linux on Android (chroot)" to Icons.Rounded.Computer,
                        "Battery Longevity (ACC)" to Icons.Rounded.BatteryChargingFull
                    ).forEach { (text, icon) ->
                        GuideCard(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                            shape = MaterialTheme.shapes.small
                        ) {
                            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                GuidePointIcon(icon = icon, tone = MaterialTheme.colorScheme.primary, iconSize = 28.dp)
                                Spacer(Modifier.width(16.dp))
                                Text(text, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                    }
                    Spacer(Modifier.height(8.dp))
                    SectionLabel("Risks of Rooting")
                    Spacer(Modifier.height(8.dp))
                    listOf(
                        "Usually Voids Warranty" to Icons.Rounded.Warning,
                        "Increased Security Risks" to Icons.Rounded.Security,
                        "Loss of Hardware Encoding" to Icons.Rounded.Info,
                        "No Official Updates (OTA)" to Icons.Rounded.Refresh,
                        "Data Loss Risk" to Icons.Rounded.Warning,
                        "Device Bricking Risk" to Icons.Rounded.ReportProblem
                    ).forEach { (text, icon) ->
                        GuideCard(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)),
                            shape = MaterialTheme.shapes.small
                        ) {
                            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                GuidePointIcon(icon = icon, tone = MaterialTheme.colorScheme.error, iconSize = 28.dp)
                                Spacer(Modifier.width(16.dp))
                                Text(text, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onErrorContainer)
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                    }
                    Spacer(Modifier.height(8.dp))
                    SectionLabel("Key Concepts")
                    Spacer(Modifier.height(8.dp))
                    InfoBlock("Bootloader", "The first piece of software that runs when you turn on your device. It acts as a security guard, directing hardware startup and ensuring stability by preventing malware infection.")
                    Spacer(Modifier.height(12.dp))
                    InfoBlock("Bricking", "When a device becomes completely non-functional due to corrupted software or failed firmware modification.")
                    Spacer(Modifier.height(12.dp))
                    InfoBlock("Soft Brick", "Recoverable state - device stuck in boot loop or recovery mode. Fix with factory reset, cache clear, or firmware reflash.")
                    Spacer(Modifier.height(12.dp))
                    InfoBlock("Hard Brick", "Serious failure - device shows no signs of life. May require specialized hardware tools or motherboard replacement.")
                    Spacer(Modifier.height(12.dp))
                    InfoBlock("dm-verity", "Linux kernel feature that ensures data integrity on critical partitions by comparing hash trees against a signed root hash.")
                    Spacer(Modifier.height(12.dp))
                    SectionLabel("Expert Tip")
                    Spacer(Modifier.height(8.dp))
                    GuideCard(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                            GuidePointIcon(icon = Icons.Rounded.Star, tone = MaterialTheme.colorScheme.primary, iconSize = 28.dp)
                            Spacer(Modifier.width(16.dp))
                            Column {
                                Text("Recommended Approach", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                                Text("Use Magisk if unsure - it works on almost every device and can be flashed via PC or custom recovery. Avoid exploits on old devices to prevent bricking!", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
                            }
                        }
                    }
                }
                "UNLOCK" -> {
                    GuideHeader("Unlocking Bootloader", onBack = { menuPath = "MAIN" })
                    Spacer(Modifier.height(16.dp))
                    GuideCard(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Row(Modifier.padding(24.dp), verticalAlignment = Alignment.CenterVertically) {
                            GuidePointIcon(icon = Icons.Rounded.Warning, tone = MaterialTheme.colorScheme.error, iconSize = 36.dp)
                            Spacer(Modifier.width(20.dp))
                            Column {
                                Text("DATA WIPE WARNING!", fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onErrorContainer)
                                Text("This process will wipe all user data. Backup before proceeding!", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.9f))
                            }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    SectionLabel("Device Compatibility")
                    Spacer(Modifier.height(8.dp))
                    GuideCard(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                GuidePointIcon(icon = Icons.Rounded.CheckCircle, tone = MaterialTheme.colorScheme.primary, iconSize = 28.dp)
                                Spacer(Modifier.width(12.dp))
                                Text("Most Android devices", style = MaterialTheme.typography.bodyMedium)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                GuidePointIcon(icon = Icons.Rounded.Info, tone = MaterialTheme.colorScheme.secondary, iconSize = 28.dp)
                                Spacer(Modifier.width(12.dp))
                                Text("Xiaomi, Oppo, Realme require additional steps", style = MaterialTheme.typography.bodyMedium)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                GuidePointIcon(icon = Icons.Rounded.Close, tone = MaterialTheme.colorScheme.error, iconSize = 24.dp)
                                Spacer(Modifier.width(12.dp))
                                Text("Vivo, iQOO may not support unlocking", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    SectionLabel("Unlock Methods")
                    Spacer(Modifier.height(8.dp))
                    ExpandableMethod("Fastboot Method (Recommended)", Icons.Rounded.Computer) {
                        GuideCard(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                            shape = MaterialTheme.shapes.small
                        ) {
                            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                GuidePointIcon(icon = Icons.Rounded.Check, tone = MaterialTheme.colorScheme.primary, iconSize = 20.dp)
                                Spacer(Modifier.width(12.dp))
                                Text("Safe and doesn't brick immediately", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        Text("Step 1 : Reboot Phone to Bootloader :")
                        CodeBox("$ adb reboot bootloader")
                        Spacer(Modifier.height(8.dp))
                        Text("Step 2 : Unlock Bootloader using Fastboot :")
                        Text(" • For most devices :")
                        CodeBox("$ fastboot flashing unlock")
                        Text(" • For some older devices :")
                        CodeBox("$ fastboot oem unlock")
                        Spacer(Modifier.height(16.dp))
                        SectionLabel("Pros & Cons")
                        Spacer(Modifier.height(8.dp))
                        GuideCard(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                            shape = MaterialTheme.shapes.small
                        ) {
                            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("✓ Safe and easy to use", style = MaterialTheme.typography.bodySmall)
                                Text("✓ Official method for most devices", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        GuideCard(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)),
                            shape = MaterialTheme.shapes.small
                        ) {
                            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("✗ Not available on all devices", style = MaterialTheme.typography.bodySmall)
                                Text("✗ Xiaomi requires community permission", style = MaterialTheme.typography.bodySmall)
                                Text("✗ Oppo/Realme need Deep Testing", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    ExpandableMethod("MTKClient (For MTK Devices)", Icons.Rounded.Memory) {
                        GuideCard(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                            shape = MaterialTheme.shapes.small
                        ) {
                            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                GuidePointIcon(icon = Icons.Rounded.Warning, tone = MaterialTheme.colorScheme.error, iconSize = 20.dp)
                                Spacer(Modifier.width(12.dp))
                                Text("Advanced method - use with caution!", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onErrorContainer)
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        Text("Hardware-level bypass for locked MediaTek chipsets.\n\nFirst install USBdk if using Windows (Recommended).\n\nNOTE: For Each Step, Run the Command, Press both Volume Buttons and Connect Phone to PC.\n")
                        Spacer(Modifier.height(16.dp))
                        SectionLabel("Step-by-Step Guide")
                        Spacer(Modifier.height(8.dp))
                        GuideCard(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                            shape = MaterialTheme.shapes.small
                        ) {
                            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("1.", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    Spacer(Modifier.width(12.dp))
                                    Text("Dump vbmeta", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                }
                                CodeBox("$ python mtk.py r vbmeta_a,vbmeta_b vbmeta_a.img,vbmeta_b.img")
                                CodeBox("$ python mtk.py r vbmeta vbmeta.img # For Old Devices")
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        GuideCard(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                            shape = MaterialTheme.shapes.small
                        ) {
                            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("2.", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    Spacer(Modifier.width(12.dp))
                                    Text("Unlock Bootloader", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                }
                                CodeBox("$ python mtk.py da seccfg unlock")
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        GuideCard(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                            shape = MaterialTheme.shapes.small
                        ) {
                            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("3.", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    Spacer(Modifier.width(12.dp))
                                    Text("Disable dm-verity", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                }
                                CodeBox("$ python mtk.py da vbmeta 3")
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        GuideCard(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                            shape = MaterialTheme.shapes.small
                        ) {
                            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("4.", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    Spacer(Modifier.width(12.dp))
                                    Text("Erase Userdata", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                }
                                CodeBox("$ python mtk.py e metadata,userdata")
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        GuideCard(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                            shape = MaterialTheme.shapes.small
                        ) {
                            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("5.", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    Spacer(Modifier.width(12.dp))
                                    Text("Reboot Device", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                }
                                CodeBox("$ python mtk.py reset")
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                        SectionLabel("Pros & Cons")
                        Spacer(Modifier.height(8.dp))
                        GuideCard(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                            shape = MaterialTheme.shapes.small
                        ) {
                            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("✓ Can fix hard-bricks", style = MaterialTheme.typography.bodySmall)
                                Text("✓ Fast and easy to use", style = MaterialTheme.typography.bodySmall)
                                Text("✓ Easy to recover with backups", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        GuideCard(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)),
                            shape = MaterialTheme.shapes.small
                        ) {
                            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("✗ Only for MediaTek devices", style = MaterialTheme.typography.bodySmall)
                                Text("✗ High risk of bricking", style = MaterialTheme.typography.bodySmall)
                                Text("✗ Doesn't work on very new devices", style = MaterialTheme.typography.bodySmall)
                                Text("✗ Fastboot may not be usable", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        LinkCard("mtkclient by @bkerler", "https://github.com/bkerler/mtkclient")
                    }
                }
                "METHODS" -> {
                    GuideHeader("Rooting Methods", onBack = { menuPath = "MAIN" })
                    Spacer(Modifier.height(8.dp))
                    GuideCard(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                GuidePointIcon(icon = Icons.Rounded.CheckCircle, tone = MaterialTheme.colorScheme.primary, iconSize = 24.dp)
                                Spacer(Modifier.width(12.dp))
                                Text("Use Official Sources Only", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                GuidePointIcon(icon = Icons.Rounded.Cancel, tone = MaterialTheme.colorScheme.primary, iconSize = 24.dp)
                                Spacer(Modifier.width(12.dp))
                                Text("Don't use 'One-Click Root' Apps", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                GuidePointIcon(icon = Icons.Rounded.LockOpen, tone = MaterialTheme.colorScheme.primary, iconSize = 28.dp)
                                Spacer(Modifier.width(12.dp))
                                Text("UNLOCK Bootloader first", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                GuidePointIcon(icon = Icons.Rounded.Computer, tone = MaterialTheme.colorScheme.primary, iconSize = 24.dp)
                                Spacer(Modifier.width(12.dp))
                                Text("FASTBOOT devices ONLY (No Samsung/Odin)", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    SectionLabel("Root Methods")
                    ExpandableMethodLocal("Magisk (Recommended)", R.drawable.ic_magisk) {
                        GuideCard(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                            shape = MaterialTheme.shapes.small
                        ) {
                            Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                GuidePointIcon(icon = Icons.Rounded.Star, tone = MaterialTheme.colorScheme.primary, iconSize = 18.dp)
                                Spacer(Modifier.width(8.dp))
                                Text("Most widely supported option", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        Text("First obtain your stock boot.img or init_boot.img and patch it using Magisk App and then Flash it.\n")
                        FlashLogic(isAB, slot, true)
                        SectionLabel("Pros")
                        GuideCard(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                            shape = MaterialTheme.shapes.small
                        ) {
                            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("✓ Truly Systemless", style = MaterialTheme.typography.bodySmall)
                                Text("✓ Widest Module Support", style = MaterialTheme.typography.bodySmall)
                                Text("✓ Works on almost any device", style = MaterialTheme.typography.bodySmall)
                                Text("✓ Best documentation & compatibility", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        SectionLabel("Cons")
                        GuideCard(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)),
                            shape = MaterialTheme.shapes.small
                        ) {
                            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("✗ Easily detectable", style = MaterialTheme.typography.bodySmall)
                                Text("✗ Leaves detectable traces", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        LinkCard("Magisk by @topjohnwu", "https://github.com/topjohnwu/Magisk")
                    }
                    ExpandableMethodLocal("KernelSU", R.drawable.ic_ksu) {
                        if(kernelVersion < "5.10") {
                            GuideCard(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                                shape = MaterialTheme.shapes.small
                            ) {
                                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    GuidePointIcon(icon = Icons.Rounded.Warning, tone = MaterialTheme.colorScheme.error, iconSize = 18.dp)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Kernel 5.10+ required for official support", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onErrorContainer)
                                }
                            }
                        }
                        GuideCard(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                            shape = MaterialTheme.shapes.small
                        ) {
                            Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                GuidePointIcon(icon = Icons.Rounded.Security, tone = MaterialTheme.colorScheme.primary, iconSize = 18.dp)
                                Spacer(Modifier.width(8.dp))
                                Text("Best for banking app compatibility", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        Text("First obtain your stock boot.img or init_boot.img and patch it using KernelSU App and then Flash it.\n")
                        FlashLogic(isAB, slot, true)
                        SectionLabel("Pros")
                        GuideCard(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                            shape = MaterialTheme.shapes.small
                        ) {
                            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("✓ Fully Systemless", style = MaterialTheme.typography.bodySmall)
                                Text("✓ Very hard to detect by banking apps", style = MaterialTheme.typography.bodySmall)
                                Text("✓ Leaves no traces", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        SectionLabel("Cons")
                        GuideCard(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)),
                            shape = MaterialTheme.shapes.small
                        ) {
                            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("✗ Only supports GKI devices", style = MaterialTheme.typography.bodySmall)
                                Text("✗ Requires kernel 5.10+", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        LinkCard("KernelSU by @tiann", "https://github.com/tiann/KernelSU")
                        SectionLabel("Alternative Forks")
                        LinkCard("KernelSU Next by @KernelSU-Next", "https://github.com/KernelSU-Next/KernelSU-Next")
                        LinkCard("SkiSU Ultra by @SkiSU-Ultra", "https://github.com/SkiSU-Ultra/SkiSU-Ultra")
                    }
                    ExpandableMethodLocal("APatch", R.drawable.ic_apatch) {
                        if(kernelVersion < "5.10") {
                            GuideCard(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                                shape = MaterialTheme.shapes.small
                            ) {
                                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    GuidePointIcon(icon = Icons.Rounded.Warning, tone = MaterialTheme.colorScheme.error, iconSize = 18.dp)
                                    Spacer(Modifier.width(8.dp))
                                    Text("May not work on all devices - research required", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onErrorContainer)
                                }
                            }
                        }
                        GuideCard(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                            shape = MaterialTheme.shapes.small
                        ) {
                            Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                GuidePointIcon(icon = Icons.Rounded.Verified, tone = MaterialTheme.colorScheme.primary, iconSize = 18.dp)
                                Spacer(Modifier.width(8.dp))
                                Text("Good for non-GKI devices", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        Text("First obtain your stock boot.img and patch it using Apatch App and then Flash it.\n")
                        FlashLogic(isAB, slot, false)
                        SectionLabel("Pros")
                        GuideCard(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                            shape = MaterialTheme.shapes.small
                        ) {
                            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("✓ Fully Systemless", style = MaterialTheme.typography.bodySmall)
                                Text("✓ Hard to detect by banking apps", style = MaterialTheme.typography.bodySmall)
                                Text("✓ Leaves no traces", style = MaterialTheme.typography.bodySmall)
                                Text("✓ Doesn't need GKI device", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        SectionLabel("Cons")
                        GuideCard(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)),
                            shape = MaterialTheme.shapes.small
                        ) {
                            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("✗ Doesn't work on every device", style = MaterialTheme.typography.bodySmall)
                                Text("✗ Newer solution, less tested", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        LinkCard("APatch by @bmax121", "https://github.com/bmax121/APatch")
                    }
                }
                "HIDING" -> {
                    GuideHeader("Root Hiding : A Step-by-Step Guide", onBack = { menuPath = "MAIN" })
                    Spacer(Modifier.height(8.dp))
                    GuideCard(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Row(Modifier.padding(22.dp), verticalAlignment = Alignment.CenterVertically) {
                            GuidePointIcon(icon = Icons.Rounded.Security, tone = MaterialTheme.colorScheme.primary, iconSize = 32.dp)
                            Spacer(Modifier.width(16.dp))
                            Column {
                                Text("BANKING APP COMPATIBILITY", fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                                Text("Bypass root checks for financial safety", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f))
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    SectionLabel("What is Root Hiding?")
                    GuideCard(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                            GuidePointIcon(icon = Icons.Rounded.VisibilityOff, tone = MaterialTheme.colorScheme.primary, iconSize = 24.dp)
                            Spacer(Modifier.width(12.dp))
                            Text("Certain apps like banking apps and games with anti-cheat check for root traces. With systemless rooting and modules, your device can hide its unlocked status from all apps!", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    SectionLabel("Step 1: Enable Zygisk")
                    GuideCard(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            GuidePointIcon(icon = Icons.Rounded.Settings, tone = MaterialTheme.colorScheme.primary, iconSize = 20.dp)
                            Spacer(Modifier.width(8.dp))
                            Text("If using Magisk, enable 'Zygisk' in Settings", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    Text("Flash these modules if using KernelSU, APatch, or Magisk with built-in Zygisk turned off:")
                    LinkCard("ReZygisk by @PerformanC","https://github.com/PerformanC/ReZygisk")
                    LinkCard("Zygisk Next by @Dr-TSNG", "https://github.com/Dr-TSNG/ZygiskNext")
                    Spacer(Modifier.height(8.dp))
                    SectionLabel("Step 2: Root Hiding Modules")
                    GuideCard(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            GuidePointIcon(icon = Icons.Rounded.Star, tone = MaterialTheme.colorScheme.primary, iconSize = 18.dp)
                            Spacer(Modifier.width(8.dp))
                            Text("Tricky Store (Recommended)", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                        }
                    }
                    GuideCard(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Spoofs hardware-backed attestation by injecting valid KeyBox.xml", style = MaterialTheme.typography.bodySmall)
                            Text("Combined with Tricky Addon and WebUI for easy configuration", style = MaterialTheme.typography.bodySmall)
                            Text("After reboot: Tap 'Action' button → Select all apps → 'Set Valid Keybox'", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    LinkCard("Tricky Store by @5ec1cff", "https://github.com/5ec1cff/TrickyStore")
                    LinkCard("Tricky Addon by @KOWX712", "https://github.com/KOWX712/Tricky-Addon-Update-Target-List")
                    GuideCard(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            GuidePointIcon(icon = Icons.Rounded.Shield, tone = MaterialTheme.colorScheme.primary, iconSize = 18.dp)
                            Spacer(Modifier.width(8.dp))
                            Text("Shamiko", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                        }
                    }
                    GuideCard(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Hides root status and all traces of Zygisk and root paths", style = MaterialTheme.typography.bodySmall)
                            Text("Fakes unlocked bootloader status", style = MaterialTheme.typography.bodySmall)
                            Text("Get from latest LSPosed releases", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    LinkCard("Shamiko by @LSPosed","https://github.com/LSPosed/LSPosed.github.io/releases/")
                    GuideCard(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            GuidePointIcon(icon = Icons.Rounded.Verified, tone = MaterialTheme.colorScheme.primary, iconSize = 18.dp)
                            Spacer(Modifier.width(8.dp))
                            Text("Play Integrity Fix (Custom ROM Users)", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                        }
                    }
                    GuideCard(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Assigns valid fingerprint of locked device systemlessly", style = MaterialTheme.typography.bodySmall)
                            Text("Flash ONE module and tap 'Action' button after reboot", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    LinkCard("Play Integrity Fork by @osm0sis","https://github.com/osm0sis/PlayIntegrityFork")
                    LinkCard("Play Integrity Fix by @KOWX712","https://github.com/KOWX712/PlayIntegrityFix")
                    Spacer(Modifier.height(8.dp))
                    SectionLabel("Open Source Alternatives")
                    GuideCard(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            GuidePointIcon(icon = Icons.Rounded.Info, tone = MaterialTheme.colorScheme.onSecondaryContainer, iconSize = 18.dp)
                            Spacer(Modifier.width(8.dp))
                            Text("FOSS alternatives for transparency-conscious users", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSecondaryContainer)
                        }
                    }
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
    GuideCard(onClick = onClick, modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            ExpressiveIconBadge(icon = icon, shape = AppShapes.bun)
            Spacer(Modifier.width(16.dp))
            Text(title, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Icon(Icons.AutoMirrored.Rounded.ArrowForwardIos, null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
        }
    }
}
@Composable
fun ExpandableMethod(title: String, icon: ImageVector, content: @Composable ColumnScope.() -> Unit) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    GuideCard(onClick = { expanded = !expanded }, modifier = Modifier.fillMaxWidth().animateContentSize(), shape = MaterialTheme.shapes.large) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ExpressiveIconBadge(icon = icon, shape = AppShapes.flower)
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
    GuideCard(onClick = { expanded = !expanded }, modifier = Modifier.fillMaxWidth().animateContentSize(), shape = MaterialTheme.shapes.large) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(painterResource(id = resId), null, modifier = Modifier.size(24.dp), tint = MaterialTheme.colorScheme.primary)
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
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { onBack() }.padding(horizontal = 8.dp, vertical = 8.dp)) {
        Icon(Icons.AutoMirrored.Rounded.ArrowBack, null, tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(8.dp))
        Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
    }
}
@Composable
fun InfoBlock(t: String, d: String) {
    Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            GuidePointIcon(icon = Icons.Rounded.Info, iconSize = 20.dp)
            Spacer(Modifier.width(8.dp))
            Text(t, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }
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
fun LinkCard(t: String, url: String) {
    val ctx = LocalContext.current
    OutlinedCard(onClick = { ctx.startActivity(Intent(Intent.ACTION_VIEW, url.toUri())) }, modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(t, Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Icon(Icons.AutoMirrored.Rounded.OpenInNew, null, modifier = Modifier.size(14.dp))
        }
    }
}
