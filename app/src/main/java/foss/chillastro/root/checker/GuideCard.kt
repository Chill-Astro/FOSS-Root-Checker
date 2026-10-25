package foss.chillastro.root.checker

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import foss.chillastro.root.checker.ui.theme.LocalMonochrome

// Scoped to educational cards so the rest of the monochrome app stays dark.
private val MonochromeGuideColors = lightColorScheme(
    primary = Color.Black, onPrimary = Color.White,
    primaryContainer = Color.White, onPrimaryContainer = Color.Black,
    secondary = Color.Black, onSecondary = Color.White,
    secondaryContainer = Color.White, onSecondaryContainer = Color.Black,
    tertiary = Color.Black, onTertiary = Color.White,
    tertiaryContainer = Color.White, onTertiaryContainer = Color.Black,
    error = Color.Black, onError = Color.White,
    errorContainer = Color.White, onErrorContainer = Color.Black,
    background = Color.White, onBackground = Color.Black,
    surface = Color.White, onSurface = Color.Black,
    surfaceVariant = Color.White, onSurfaceVariant = Color.Black,
    surfaceContainerLowest = Color.White, surfaceContainerLow = Color.White,
    surfaceContainer = Color.White, surfaceContainerHigh = Color.White,
    surfaceContainerHighest = Color.White, surfaceDim = Color.White,
    surfaceBright = Color.White, surfaceTint = Color.Transparent,
    outline = Color.Black, outlineVariant = Color.Black
)

@Composable
internal fun GuideCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    colors: CardColors = CardDefaults.cardColors(),
    shape: Shape = MaterialTheme.shapes.large,
    content: @Composable ColumnScope.() -> Unit
) {
    val monochrome = LocalMonochrome.current
    val whiteCard = monochrome && onClick == null
    val cardColors = when {
        whiteCard -> CardDefaults.cardColors(containerColor = Color.White, contentColor = Color.Black)
        monochrome -> CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            contentColor = Color.White
        )
        else -> colors
    }
    val cardContent: @Composable ColumnScope.() -> Unit = {
        if (whiteCard) {
            MaterialTheme(colorScheme = MonochromeGuideColors) { content() }
        } else content()
    }
    if (onClick != null) {
        Card(onClick = onClick, modifier = modifier, shape = shape, colors = cardColors, content = cardContent)
    } else {
        Card(modifier = modifier, shape = shape, colors = cardColors, content = cardContent)
    }
}
