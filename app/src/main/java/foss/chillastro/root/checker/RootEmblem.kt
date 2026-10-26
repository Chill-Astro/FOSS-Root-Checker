@file:OptIn(androidx.compose.material3.ExperimentalMaterial3ExpressiveApi::class)

package foss.chillastro.root.checker

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.toPath
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.graphics.shapes.Morph
import kotlin.math.floor

@Composable
internal fun RootEmblem(
    checking: Boolean,
    reducedAnimations: Boolean,
    color: Color,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val polygons = remember {
        listOf(
            MaterialShapes.Cookie12Sided,
            MaterialShapes.Cookie9Sided,
            MaterialShapes.Cookie6Sided
        )
    }
    val morphs = remember(polygons) {
        polygons.indices.map { Morph(polygons[it], polygons[(it + 1) % polygons.size]) }
    }
    val path = remember { Path() }
    val matrix = remember { Matrix() }
    var angle by remember { mutableFloatStateOf(0f) }
    var phase by remember { mutableFloatStateOf(0f) }
    val reducedScale = remember { Animatable(1f) }
    val speed by animateFloatAsState(
        targetValue = if (checking) 140f else 12f,
        animationSpec = tween(700), label = "emblem_spin_speed"
    )
    val morphSpeed by animateFloatAsState(
        targetValue = if (checking) 0.65f else 0.16f,
        animationSpec = tween(700), label = "emblem_morph_speed"
    )
    val fill by animateColorAsState(color, tween(if (reducedAnimations) 0 else 350), label = "emblem_color")

    LaunchedEffect(reducedAnimations, if (reducedAnimations) checking else false) {
        if (reducedAnimations) {
            phase = 0f
            if (!checking) {
                reducedScale.animateTo(1f, tween(200))
                return@LaunchedEffect
            }
            coroutineScope {
                launch { reducedScale.animateTo(1.12f, tween(200)) }
                var previous = withFrameNanos { it }
                while (true) {
                    withFrameNanos { now ->
                        val seconds = ((now - previous) / 1_000_000_000f).coerceIn(0f, 0.05f)
                        previous = now
                        angle = (angle + speed * seconds) % 360f
                    }
                }
            }
            return@LaunchedEffect
        }
        reducedScale.snapTo(1f)
        var previous = withFrameNanos { it }
        while (true) {
            withFrameNanos { now ->
                // Ignore long gaps when returning from the background.
                val seconds = ((now - previous) / 1_000_000_000f).coerceIn(0f, 0.05f)
                previous = now
                angle = (angle + speed * seconds) % 360f
                phase = (phase + morphSpeed * seconds) % morphs.size
            }
        }
    }

    Box(modifier = modifier.graphicsLayer {
        scaleX = reducedScale.value
        scaleY = reducedScale.value
    }, contentAlignment = Alignment.Center) {
        Canvas(Modifier.matchParentSize()) {
            val index = floor(phase).toInt().coerceIn(morphs.indices)
            val fraction = phase - index
            val eased = fraction * fraction * (3f - 2f * fraction)
            path.rewind()
            morphs[index].toPath(progress = eased, path = path)
            matrix.reset()
            matrix.scale(size.width, size.height)
            path.transform(matrix)
            path.translate(center - path.getBounds().center)
            rotate(angle, pivot = center) { drawPath(path, fill) }
        }
        // Keep the status icon upright as the silhouette spins beneath it.
        content()
    }
}

internal fun rootResultColor(positive: Boolean, monochrome: Boolean): Color = when {
    monochrome -> Color.White
    positive -> Color(0xFF4CAF50)
    else -> Color(0xFFB00020)
}
