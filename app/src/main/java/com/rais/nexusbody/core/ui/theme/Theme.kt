package com.rais.nexusbody.core.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val nexusbodydarkcolorscheme = darkColorScheme(
    primary = premiumaccent,
    secondary = statusgood,
    background = spatialblack,
    surface = glasssurface,
    onPrimary = Color.White,
    onBackground = textprimary,
    onSurface = textprimary,
    error = statusdanger
)

@Composable
fun NexusBodyTheme(
    dynamiccolor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorscheme = when {
        dynamiccolor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            dynamicDarkColorScheme(context)
        }
        else -> nexusbodydarkcolorscheme
    }

    MaterialTheme(
        colorScheme = colorscheme,
        typography = NexusBodyTypography,
        content = content
    )
}