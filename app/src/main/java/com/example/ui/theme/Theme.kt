package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import com.example.mludo.ui.theme.AppTypography

private val DarkColorScheme = darkColorScheme(
    primary = GoldButtonTop,
    secondary = BlueSheetHeader,
    tertiary = GoldBorder,
    background = DarkBlueBackground,
    surface = DarkBlueCard,
    onPrimary = DarkBlueText,
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun MLudoTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = DarkBlueBackground.toArgb()
            window.navigationBarColor = DarkBlueBackground.toArgb()
        }
    }

    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = AppTypography,
        content = content
    )
}
