package com.example.vireya.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.example.vireya.R

// Step 1: Define pixel fonts
val VT323 = FontFamily(Font(R.font.vt323))
val PressStart2P = FontFamily(Font(R.font.press_start_2p))

// Step 2: Define pixel color palette
val Teal = Color(0xFF195F55)
val LightMint = Color(0xFFD4EBF2)
val Background = Color(0xFFF9FAF9)
val TextPrimary = Color(0xFF1E3E37)
val TextSecondary = Color(0xFF6C7A89)

// Step 3: Create a pixel typography style
private val PixelTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = PressStart2P,
        fontSize = 28.sp,
        color = TextPrimary
    ),
    bodyLarge = TextStyle(
        fontFamily = VT323,
        fontSize = 16.sp,
        color = TextPrimary
    ),
    labelLarge = TextStyle(
        fontFamily = VT323,
        fontSize = 14.sp,
        color = TextSecondary
    )
)

// Step 4: PixelTheme Composable
@Composable
fun PixelTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (useDarkTheme) darkColorScheme(
        background = Background,
        primary = Teal,
        onPrimary = Color.White
    ) else lightColorScheme(
        background = Background,
        primary = Teal,
        onPrimary = Color.White
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = PixelTypography,
        content = content
    )
}
