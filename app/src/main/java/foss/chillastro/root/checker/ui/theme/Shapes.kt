package foss.chillastro.root.checker.ui.theme

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Shape

// Resolve Material shape outlines inside composition; text containers use the theme corner scale.
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
object AppShapes {
    val root: Shape
        @Composable get() = MaterialShapes.Cookie12Sided.toShape()
    val info: Shape
        @Composable get() = MaterialShapes.Cookie12Sided.toShape()
    val flower: Shape
        @Composable get() = MaterialShapes.Cookie9Sided.toShape()
    val puffy: Shape
        @Composable get() = MaterialShapes.Cookie12Sided.toShape()
    val bun: Shape
        @Composable get() = MaterialShapes.Cookie6Sided.toShape()
}
