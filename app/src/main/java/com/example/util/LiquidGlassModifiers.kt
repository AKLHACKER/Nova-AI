package com.example.util

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.Blue400
import com.example.ui.theme.Blue500
import com.example.ui.theme.Blue600
import com.example.ui.theme.CanvasBlack
import com.example.ui.theme.FrostedBorder
import com.example.ui.theme.FrostedBorderHighlight
import com.example.ui.theme.FrostedBorderSubtle
import com.example.ui.theme.FrostedSurface
import com.example.ui.theme.FrostedSurfaceElevated
import com.example.ui.theme.FrostedSurfaceGradientTop
import com.example.ui.theme.FrostedSurfaceSubtle

/**
 * Creates an authentic Frosted Glass card surface:
 * Translucent white gradient surface (bg-white/5 to bg-white/[0.03]),
 * framed by a subtle specular border (border-white/10).
 */
fun Modifier.frostedGlassCard(
    shape: Shape = RoundedCornerShape(24.dp),
    backgroundColor: Color = FrostedSurface,
    borderColor: Color = FrostedBorder,
    borderWidth: Dp = 1.dp
): Modifier = this
    .clip(shape)
    .background(
        brush = Brush.verticalGradient(
            colors = listOf(
                FrostedSurfaceGradientTop,
                backgroundColor,
                FrostedSurfaceSubtle
            )
        ),
        shape = shape
    )
    .border(
        width = borderWidth,
        brush = Brush.verticalGradient(
            colors = listOf(
                FrostedBorderHighlight,
                borderColor,
                FrostedBorderSubtle
            )
        ),
        shape = shape
    )

/**
 * Alias maintaining compatibility with existing calls while rendering Frosted Glass.
 */
fun Modifier.liquidGlassCard(
    shape: Shape = RoundedCornerShape(22.dp),
    backgroundColor: Color = FrostedSurface,
    borderColor: Color = FrostedBorder,
    borderWidth: Dp = 1.dp
): Modifier = this.frostedGlassCard(shape, backgroundColor, borderColor, borderWidth)

/**
 * Elevated Frosted Glass card with radiant blue ambient glow
 * and crisp specular border.
 */
fun Modifier.frostedGlassElevated(
    shape: Shape = RoundedCornerShape(24.dp),
    accentColor: Color = Blue500
): Modifier = this
    .clip(shape)
    .background(
        brush = Brush.verticalGradient(
            colors = listOf(
                accentColor.copy(alpha = 0.14f),
                FrostedSurfaceElevated,
                FrostedSurface
            )
        ),
        shape = shape
    )
    .border(
        width = 1.dp,
        brush = Brush.verticalGradient(
            colors = listOf(
                accentColor.copy(alpha = 0.45f),
                FrostedBorderHighlight,
                FrostedBorderSubtle
            )
        ),
        shape = shape
    )

fun Modifier.liquidGlassElevated(
    shape: Shape = RoundedCornerShape(24.dp),
    accentColor: Color = Blue500
): Modifier = this.frostedGlassElevated(shape, accentColor)

/**
 * Renders the two signature ambient blurred orbs from the design HTML:
 * - Top-left: blue-600/20 blur [100px]
 * - Bottom-right: blue-500/10 blur [100px]
 */
fun Modifier.frostedAmbientBackground(): Modifier = this
    .background(CanvasBlack)
    .drawBehind {
        // Top-left radial ambient glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Blue600.copy(alpha = 0.22f),
                    Blue500.copy(alpha = 0.10f),
                    Color.Transparent
                ),
                center = Offset(x = size.width * -0.10f, y = size.height * -0.05f),
                radius = size.width * 0.85f
            )
        )

        // Bottom-right radial ambient glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Blue500.copy(alpha = 0.15f),
                    Blue600.copy(alpha = 0.05f),
                    Color.Transparent
                ),
                center = Offset(x = size.width * 1.15f, y = size.height * 1.05f),
                radius = size.width * 0.95f
            )
        )
    }

/**
 * Pulsing subtle glow for active school hour / real-time synchronization indicator.
 */
@Composable
fun Modifier.liquidPulseGlow(
    color: Color = Blue400,
    enabled: Boolean = true
): Modifier {
    if (!enabled) return this
    val transition = rememberInfiniteTransition(label = "pulse_glow")
    val alpha by transition.animateFloat(
        initialValue = 0.12f,
        targetValue = 0.38f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    return this.drawBehind {
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(color.copy(alpha = alpha), Color.Transparent),
                radius = size.maxDimension / 1.4f
            )
        )
    }
}

