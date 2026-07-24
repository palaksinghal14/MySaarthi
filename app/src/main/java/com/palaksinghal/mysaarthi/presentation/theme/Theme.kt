package com.palaksinghal.mysaarthi.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


private val MySaarthiScheme = lightColorScheme(
    primary = Accent,
    onPrimary = Bg,
    primaryContainer = Terracotta100,
    onPrimaryContainer = Terracotta700,

    secondary = Accent2,
    onSecondary = TextInk,
    secondaryContainer = Sage100,
    onSecondaryContainer = Sage700,

    tertiary = Accent2,
    onTertiary = TextInk,
    tertiaryContainer = Sage100,
    onTertiaryContainer = Sage700,

    background = Bg,
    onBackground = TextInk,

    surface = Surface,
    onSurface = TextInk,
    surfaceVariant = Neutral200,
    onSurfaceVariant = Neutral700,

    outline = Neutral400,
    outlineVariant = Neutral300,

    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002)
)

@Composable
fun MySaarthiTheme(
    content: @Composable () -> Unit
) {

    MaterialTheme(
        colorScheme = MySaarthiScheme,
        typography = MySaarthiTypography,
        content = content
    )
}