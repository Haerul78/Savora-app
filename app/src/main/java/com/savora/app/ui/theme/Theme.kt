package com.savora.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val SavoraColorScheme = lightColorScheme(
    primary = SavoraPrimary,
    onPrimary = SavoraOnPrimary,
    primaryContainer = SavoraPrimaryContainer,
    onPrimaryContainer = SavoraOnPrimary,
    secondary = SavoraSecondaryContainer,
    onSecondary = SavoraOnSecondaryContainer,
    secondaryContainer = SavoraSecondaryContainer,
    onSecondaryContainer = SavoraOnSecondaryContainer,
    tertiary = SavoraTertiary,
    onTertiary = SavoraOnTertiary,
    tertiaryContainer = SavoraTertiaryContainer,
    onTertiaryContainer = SavoraOnTertiary,
    background = SavoraSurface,
    onBackground = SavoraOnSurface,
    surface = SavoraSurface,
    onSurface = SavoraOnSurface,
    surfaceVariant = SavoraSurfaceContainerLow,
    onSurfaceVariant = SavoraOnSurfaceVariant,
    outline = SavoraOutlineVariant,
    outlineVariant = SavoraOutlineVariant,
    error = SavoraError,
    onError = SavoraOnPrimary,
    surfaceContainerLow = SavoraSurfaceContainerLow,
    surfaceContainerLowest = SavoraSurfaceContainerLowest,
    surfaceContainerHigh = SavoraSurfaceContainerHigh,
)

@Composable
fun SavoraTheme(
    content: @Composable () -> Unit
) {
    // Savora hanya menggunakan light theme — tidak ada dark mode
    // Dynamic color disabled agar brand color konsisten
    MaterialTheme(
        colorScheme = SavoraColorScheme,
        typography = Typography,
        content = content
    )
}