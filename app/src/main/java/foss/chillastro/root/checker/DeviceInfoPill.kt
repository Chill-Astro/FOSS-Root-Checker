package foss.chillastro.root.checker

import android.os.Build
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.LockOpen
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material.icons.rounded.Close
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Android
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.History
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.ui.graphics.Color
import foss.chillastro.root.checker.ui.theme.AppShapes
import foss.chillastro.root.checker.ui.theme.LocalMonochrome
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
internal fun DeviceInfoPill(
    bootloader: String,
    verity: String,
    onOpenInfo: () -> Unit,
    onOpenHistory: () -> Unit
) {
    val deviceName = if (Build.MODEL.startsWith(Build.MANUFACTURER, ignoreCase = true)) {
        Build.MODEL
    } else "${Build.MANUFACTURER} ${Build.MODEL}"
    val detailIcons = mapOf(
        "phone" to Icons.Rounded.PhoneAndroid,
        "android" to Icons.Rounded.Android,
        "bootloader" to if (bootloader == "Locked") Icons.Rounded.Lock else Icons.Rounded.LockOpen,
        "verity" to if (verity.contains("enforcing", ignoreCase = true)) Icons.Rounded.Verified else Icons.Rounded.Close
    ).mapValues { (_, vector) ->
        InlineTextContent(Placeholder(14.sp, 14.sp, PlaceholderVerticalAlign.TextCenter)) {
            Icon(vector, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RectangleShape,
        color = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        Row(
            Modifier.windowInsetsPadding(WindowInsets.statusBars).padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PillAction(Icons.Rounded.Info, "Device information", onOpenInfo)
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    buildAnnotatedString {
                        append("$deviceName ")
                        appendInlineContent("phone", "\uFFFC")
                        append(" • Android ${Build.VERSION.RELEASE} ")
                        appendInlineContent("android", "\uFFFC")
                    },
                    inlineContent = detailIcons,
                    style = MaterialTheme.typography.labelLarge,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Text(
                    buildAnnotatedString {
                        append("Bootloader: $bootloader ")
                        appendInlineContent("bootloader", "\uFFFC")
                        append(" • dm-verity: $verity ")
                        appendInlineContent("verity", "\uFFFC")
                    },
                    inlineContent = detailIcons,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
            PillAction(Icons.Rounded.History, "History", onOpenHistory)
        }
    }
}

@Composable
private fun PillAction(icon: ImageVector, description: String, onClick: () -> Unit) {
    FilledIconButton(
        onClick = onClick,
        modifier = Modifier.size(48.dp),
        shape = AppShapes.root,
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = if (LocalMonochrome.current) Color.White else MaterialTheme.colorScheme.primaryContainer,
            contentColor = if (LocalMonochrome.current) Color.Black else MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Icon(icon, contentDescription = description)
    }
}
