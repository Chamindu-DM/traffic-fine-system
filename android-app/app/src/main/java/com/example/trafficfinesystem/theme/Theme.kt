package com.example.trafficfinesystem.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = SapphireDark,
    secondary = AmberDark,
    tertiary = CrimsonDark,
    background = NeutralDarkBg,
    surface = CardDarkBg,
    onPrimary = NeutralDarkBg,
    onSecondary = NeutralDarkBg,
    onBackground = Color(0xFFF1F5F9),
    onSurface = Color(0xFFF1F5F9)
)

private val LightColorScheme = lightColorScheme(
    primary = SapphireLight,
    secondary = AmberLight,
    tertiary = CrimsonLight,
    background = NeutralLightBg,
    surface = CardLightBg,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF0F172A),
    onSurface = Color(0xFF0F172A)
)

@Composable
fun TrafficFineSystemTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Dynamic color is available on Android 12+
  dynamicColor: Boolean = false, // Disable dynamic colors to ensure our custom branding is prominent
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
