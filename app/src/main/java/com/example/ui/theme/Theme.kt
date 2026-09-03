package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkSchoolColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = ObsidianBlack,
    primaryContainer = CyberBlue,
    onPrimaryContainer = TextPrimary,
    secondary = SoftCyan,
    onSecondary = ObsidianBlack,
    secondaryContainer = DeepSpace,
    onSecondaryContainer = SkyGlow,
    tertiary = ElectricBlue,
    onTertiary = TextPrimary,
    background = ObsidianBlack,
    onBackground = TextPrimary,
    surface = MidnightNavy,
    onSurface = TextPrimary,
    surfaceVariant = GlassSurface,
    onSurfaceVariant = TextSecondary,
    outline = GlassBorder,
    outlineVariant = GlassBorderSubtle
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = DarkSchoolColorScheme,
        typography = Typography,
        content = content
    )
}
