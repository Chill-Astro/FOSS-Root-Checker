@file:OptIn(androidx.compose.material3.ExperimentalMaterial3ExpressiveApi::class)

package foss.chillastro.root.checker

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.ui.graphics.vector.ImageVector
import foss.chillastro.root.checker.ui.theme.AppShapes
import foss.chillastro.root.checker.ui.theme.LocalMonochrome
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonShapes
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import foss.chillastro.root.checker.ui.theme.LocalReducedMotion

@Composable
internal fun expressiveButtonShapes(shape: Shape = ButtonDefaults.shape): ButtonShapes =
    ButtonDefaults.shapes(
        shape = shape,
        pressedShape = if (LocalReducedMotion.current) shape else RoundedCornerShape(12.dp)
    )

@Composable
internal fun ExpressiveSwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    enabled: Boolean = true
) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        enabled = enabled,
        thumbContent = {
            Icon(
                if (checked) Icons.Filled.Check else Icons.Filled.Close,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        }
    )
}

@Composable
internal fun AppLoadingIndicator(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary
) {
    if (LocalReducedMotion.current) {
        CircularProgressIndicator(modifier = modifier, color = color, strokeWidth = 2.dp)
    } else {
        LoadingIndicator(modifier = modifier, color = color)
    }
}

@Composable
internal fun ExpressiveIconBadge(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    shape: Shape = AppShapes.info
) {
    Surface(
        modifier = modifier.size(48.dp),
        shape = shape,
        color = if (LocalMonochrome.current) Color.White else MaterialTheme.colorScheme.primaryContainer,
        contentColor = if (LocalMonochrome.current) Color.Black else MaterialTheme.colorScheme.onPrimaryContainer
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.padding(12.dp))
    }
}
