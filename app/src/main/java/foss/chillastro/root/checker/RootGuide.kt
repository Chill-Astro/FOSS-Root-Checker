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
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Warning
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
fun RootGuide() {
    var menuPath by rememberSaveable { mutableStateOf("MAIN") }
    val slot = remember { HardwareProbe.getProp("ro.boot.slot_suffix").replace("_", "") }
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
                                Text("Start with the official instructions for your exact device. Keep a backup and a way to restore its stock firmware.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.9f))
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
                                Text("Grant root only to apps you trust. Review requests in your root manager and revoke access when it is no longer needed.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.9f))
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
                        "More backup options (app and device dependent)" to Icons.Rounded.Memory,
                        "Automation and system tools through compatible modules" to Icons.Rounded.Cloud,
                        "Performance tuning on supported devices" to Icons.Rounded.Animation,
                        "Sound Enhancement" to Icons.AutoMirrored.Rounded.VolumeUp,
                        "Linux on Android (chroot)" to Icons.Rounded.Computer,
                        "Charge control on supported devices (for example, ACC)" to Icons.Rounded.BatteryChargingFull
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
                        "Warranty and support may be affected" to Icons.Rounded.Warning,
                        "Increased Security Risks" to Icons.Rounded.Security,
                        "Some DRM, streaming or device features may stop working" to Icons.Rounded.Info,
                        "OTA updates may require restoring stock images" to Icons.Rounded.Refresh,
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
                    GuideNote("What this app checks", "A scan looks for accessible root traces; the SU check asks whether superuser access can be granted. A clean scan does not prove a device is unmodified, and finding a trace does not prove an app currently has root.")
                    SectionLabel("Key Concepts")
                    Spacer(Modifier.height(8.dp))
                    InfoBlock("Bootloader", "The first piece of software that runs when you turn on your device. It acts as a security guard, directing hardware startup and ensuring stability by preventing malware infection.")
                    Spacer(Modifier.height(12.dp))
                    InfoBlock("Bricking", "When a device becomes completely non-functional due to corrupted software or failed firmware modification.")
                    Spacer(Modifier.height(12.dp))
                    InfoBlock("Soft Brick", "A boot loop or unexpected recovery screen may be recoverable. If it followed a module install, try your root manager's recovery steps first. Restoring a matching stock boot image may help; a factory reset erases data and is not always necessary.")
                    Spacer(Modifier.height(12.dp))
                    InfoBlock("Hard Brick", "A device that cannot reach Android, recovery or fastboot may need a device-specific recovery mode or repair service. A black screen alone does not prove the device is permanently damaged.")
                    Spacer(Modifier.height(12.dp))
                    InfoBlock("dm-verity", "Checks blocks on verified partitions against a hash tree. Android Verified Boot links those checks to trusted metadata. Changing a protected partition can cause verification errors; disabling verification is not a universal rooting step.")
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
                                Text("Magisk is a common starting point with detailed documentation. Check its installation guide for your device before choosing an image or installation method. Do not use a patched image made for another device or firmware build.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
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
                                Text("Bootloader unlocking normally erases user data. Back up files, app data and account recovery codes before confirming the unlock on your phone.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.9f))
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
                                Text("Unlock support depends on the exact model, carrier and region", style = MaterialTheme.typography.bodyMedium)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                GuidePointIcon(icon = Icons.Rounded.Info, tone = MaterialTheme.colorScheme.secondary, iconSize = 28.dp)
                                Spacer(Modifier.width(12.dp))
                                Text("Some manufacturers require an approval process or dedicated tool", style = MaterialTheme.typography.bodyMedium)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                GuidePointIcon(icon = Icons.Rounded.Close, tone = MaterialTheme.colorScheme.error, iconSize = 24.dp)
                                Spacer(Modifier.width(12.dp))
                                Text("Some models do not offer an official unlock method", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    GuideNote("Before unlocking", "Keep a copy of your exact stock firmware, backups and account recovery codes off the phone. Check the manufacturer's unlock policy for your model and confirm that you can reach its recovery or bootloader mode.")
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
                                Text("Use this method only when your manufacturer supports fastboot unlocking.", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        Text("Step 1 : Reboot Phone to Bootloader :")
                        CodeBox("$ adb reboot bootloader")
                        Spacer(Modifier.height(8.dp))
                        Text("Step 2 : Unlock Bootloader using Fastboot :")
                        Text(" • On supported devices, when specified by the manufacturer:")
                        CodeBox("$ fastboot flashing unlock")
                        Text(" • Alternative used by some devices; use only the documented command:")
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
                                Text("✓ Uses the bootloader's documented unlock interface", style = MaterialTheme.typography.bodySmall)
                                Text("✓ Available on devices whose manufacturers support it", style = MaterialTheme.typography.bodySmall)
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
                                Text("✗ Some models require manufacturer approval or a dedicated tool", style = MaterialTheme.typography.bodySmall)
                                Text("✗ Carrier, region or firmware restrictions may prevent unlocking", style = MaterialTheme.typography.bodySmall)
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
                                Text("Advanced MediaTek method: support depends on the chipset and device security configuration.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onErrorContainer)
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        Text("mtkclient supports selected MediaTek devices; it is not a universal unlock tool. Follow its current setup and connection instructions for your model. The examples below are not a sequence to run on every phone. Read back your original partitions and keep backups before writing or erasing anything.\n")
                        Spacer(Modifier.height(16.dp))
                        SectionLabel("Example Commands (device-specific)")
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
                                    Text("Change vbmeta flags (only if required)", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                }
                                Text("Only change verification flags when the instructions for your exact device require it. This changes verified-boot behaviour.", style = MaterialTheme.typography.bodySmall)
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
                                Text("This command erases data. Run it only after backing up and confirming the partition names for your device.", style = MaterialTheme.typography.bodySmall)
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
                                Text("✓ May help recover supported MediaTek devices", style = MaterialTheme.typography.bodySmall)
                                Text("✓ Offers partition read, write and diagnostic tools", style = MaterialTheme.typography.bodySmall)
                                Text("✓ Partition backups can help with recovery", style = MaterialTheme.typography.bodySmall)
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
                                Text("✗ Support varies by chipset and security configuration", style = MaterialTheme.typography.bodySmall)
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
                    GuideNote("Use matching firmware", "Your image must match the device model and installed firmware build. Keep the unpatched original somewhere you can access if Android stops booting. The examples below illustrate partition names; follow the root project's device-specific instructions.")
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
                        Text("Use firmware matching your exact installed build. Follow Magisk's guide to identify the correct image to patch: boot, init_boot, vendor_boot or recovery, depending on the device. Keep the original image on another device before flashing.\n")
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
                                Text("✓ Supports many devices when the documented requirements are met", style = MaterialTheme.typography.bodySmall)
                                Text("✓ Detailed installation and recovery documentation", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        SectionLabel("Cons")
                        GuideCard(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)),
                            shape = MaterialTheme.shapes.small
                        ) {
                            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("✗ Apps may still detect root or an unlocked bootloader", style = MaterialTheme.typography.bodySmall)
                                Text("✗ Compatibility depends on device and module configuration", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        LinkCard("Magisk installation guide", "https://topjohnwu.github.io/Magisk/install.html")
                        LinkCard("Magisk by @topjohnwu", "https://github.com/topjohnwu/Magisk")
                    }
                    ExpandableMethodLocal("KernelSU", R.drawable.ic_ksu) {
                        GuideCard(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                            shape = MaterialTheme.shapes.small
                        ) {
                            Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                GuidePointIcon(icon = Icons.Rounded.Warning, tone = MaterialTheme.colorScheme.error, iconSize = 18.dp)
                                Spacer(Modifier.width(8.dp))
                                Text("Check support in the official KernelSU manager. Kernel version alone does not establish compatibility; GKI/KMI and device configuration matter.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onErrorContainer)
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
                                Text("Kernel-based root; check support in the manager before installing.", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        Text("Follow KernelSU's installation guide for the method supported by your device. Keep its original boot.img and verify the required kernel interface (KMI) before choosing a kernel or image.\n")
                        FlashLogic(isAB, slot, false)
                        SectionLabel("Pros")
                        GuideCard(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                            shape = MaterialTheme.shapes.small
                        ) {
                            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("✓ Fully Systemless", style = MaterialTheme.typography.bodySmall)
                                Text("✓ Kernel-based root with per-app access control", style = MaterialTheme.typography.bodySmall)
                                Text("✓ Can support modules without directly modifying system partitions", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        SectionLabel("Cons")
                        GuideCard(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)),
                            shape = MaterialTheme.shapes.small
                        ) {
                            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("✗ Official prebuilt support depends on the device's GKI/KMI", style = MaterialTheme.typography.bodySmall)
                                Text("✗ Other devices may need a compatible custom kernel", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        LinkCard("KernelSU installation guide", "https://kernelsu.org/guide/installation.html")
                        LinkCard("KernelSU by @tiann", "https://github.com/tiann/KernelSU")
                        SectionLabel("Alternative Forks")
                        LinkCard("KernelSU Next by @KernelSU-Next", "https://github.com/KernelSU-Next/KernelSU-Next")
                        LinkCard("SkiSU Ultra by @SkiSU-Ultra", "https://github.com/SkiSU-Ultra/SkiSU-Ultra")
                    }
                    ExpandableMethodLocal("APatch", R.drawable.ic_apatch) {
                        GuideCard(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                            shape = MaterialTheme.shapes.small
                        ) {
                            Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                GuidePointIcon(icon = Icons.Rounded.Warning, tone = MaterialTheme.colorScheme.error, iconSize = 18.dp)
                                Spacer(Modifier.width(8.dp))
                                Text("Check APatch's kernel and architecture requirements, including kallsyms support. A kernel version number by itself is not enough.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onErrorContainer)
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
                                Text("Supports some non-GKI devices when the kernel requirements are met.", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        Text("Patch the stock boot.img from your exact firmware using APatch. Do not patch or flash init_boot for APatch. Keep the original boot.img and follow the project's compatibility checks first.\n")
                        FlashLogic(isAB, slot, false)
                        SectionLabel("Pros")
                        GuideCard(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                            shape = MaterialTheme.shapes.small
                        ) {
                            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("✓ Fully Systemless", style = MaterialTheme.typography.bodySmall)
                                Text("✓ Kernel-based root with per-app access control", style = MaterialTheme.typography.bodySmall)
                                Text("✓ Can support modules without directly modifying system partitions", style = MaterialTheme.typography.bodySmall)
                                Text("✓ Does not require GKI on supported kernels", style = MaterialTheme.typography.bodySmall)
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
                                Text("✗ Requires a supported architecture and kernel configuration", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        LinkCard("APatch installation guide", "https://apatch.dev/install.html")
                        LinkCard("APatch by @bmax121", "https://github.com/bmax121/APatch")
                    }
                }
                "HIDING" -> {
                    GuideHeader("Root Hiding and App Compatibility", onBack = { menuPath = "MAIN" })
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
                                Text("WHAT ROOT HIDING CAN DO", fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                                Text("Root hiding may help an app run, but it does not make a rooted device safer or restore a locked bootloader.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f))
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
                            Text("Some apps check root traces, injected code or device-integrity verdicts. Hiding can reduce particular signals, but results vary by app and can change after updates. Passing a local root check does not guarantee Play Integrity or banking-app compatibility.", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    GuideNote("If an app still refuses to run", "Check the app's message and the relevant module's troubleshooting guide before changing more settings. Denying root access to an app is different from hiding root. This checker cannot predict another app's decision; use a supported stock setup if that app requires one.")
                    SectionLabel("Zygisk (when a module requires it)")
                    GuideCard(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            GuidePointIcon(icon = Icons.Rounded.Settings, tone = MaterialTheme.colorScheme.primary, iconSize = 20.dp)
                            Spacer(Modifier.width(8.dp))
                            Text("For a module that requires Zygisk, enable it in Magisk's settings and reboot. Check that module's requirements first.", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    Text("For KernelSU, APatch or an alternative Magisk setup, a compatible standalone Zygisk implementation may be needed. Use only one implementation at a time and follow its supported configuration:")
                    LinkCard("ReZygisk by @PerformanC","https://github.com/PerformanC/ReZygisk")
                    LinkCard("Zygisk Next by @Dr-TSNG", "https://github.com/Dr-TSNG/ZygiskNext")
                    Spacer(Modifier.height(8.dp))
                    SectionLabel("Modules and Their Roles")
                    GuideCard(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            GuidePointIcon(icon = Icons.Rounded.Star, tone = MaterialTheme.colorScheme.primary, iconSize = 18.dp)
                            Spacer(Modifier.width(8.dp))
                            Text("Tricky Store (attestation-related module)", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                        }
                    }
                    GuideCard(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Changes aspects of Android key attestation for configured apps; requirements depend on the module version and device.", style = MaterialTheme.typography.bodySmall)
                            Text("Tricky Addon provides a separate configuration interface. Read both projects' instructions before combining them.", style = MaterialTheme.typography.bodySmall)
                            Text("Configure only the apps you need. A passing result is not guaranteed, and keys or workarounds may stop working.", style = MaterialTheme.typography.bodySmall)
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
                            Text("Attempts to hide certain root and Zygisk traces from selected app processes.", style = MaterialTheme.typography.bodySmall)
                            Text("It does not relock the bootloader or guarantee that every root check will pass.", style = MaterialTheme.typography.bodySmall)
                            Text("Use the official release notes to check supported Magisk/Zygisk versions and configuration.", style = MaterialTheme.typography.bodySmall)
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
                            Text("Play Integrity Modules", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                        }
                    }
                    GuideCard(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Attempts to address some Play Integrity checks through software workarounds; verdicts depend on the device and current checks.", style = MaterialTheme.typography.bodySmall)
                            Text("Choose a compatible implementation and follow its current instructions. Do not stack overlapping fixes without checking compatibility.", style = MaterialTheme.typography.bodySmall)
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
                            Text("These projects address different parts of root detection or attestation. Open source allows inspection, but is not a guarantee of compatibility or security.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSecondaryContainer)
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
fun FlashLogic(isAB: Boolean, slot: String, hasInit: Boolean) {
    val suffix = if (isAB && slot in listOf("a", "b")) "_$slot" else ""
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Flashing examples — choose the partition your installation guide specifies.")
        if (isAB) {
            Text("Android reports slot $slot. Confirm the target in the bootloader before flashing; do not flash the other slot just because it exists.", style = MaterialTheme.typography.bodySmall)
            CodeBox("$ fastboot getvar current-slot")
        }
        Text("For a patched boot.img:")
        CodeBox("$ fastboot flash boot$suffix patched.img")
        if (hasInit) {
            Text("Alternative: only for a patched init_boot.img when your installation guide requires it. Do not run both examples.")
            CodeBox("$ fastboot flash init_boot$suffix patched.img")
        }
        Text("patched.img is an example filename. Replace it with your own patched file and use the partition name required by your device.", style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun GuideNote(title: String, message: String) {
    GuideCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            GuidePointIcon(Icons.Rounded.Info)
            Spacer(Modifier.width(12.dp))
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(title, style = MaterialTheme.typography.titleSmall)
                Text(message, style = MaterialTheme.typography.bodySmall)
            }
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
