package com.wyllyw.huertoplan.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Paleta de colores personalizada
private val HuertoPlanLightColorScheme = lightColorScheme(
    primary = Color(0xFF2E7D32),          // Verde oscuro
    onPrimary = Color.White,
    primaryContainer = Color(0xFFA5D6A7),  // Verde claro
    onPrimaryContainer = Color(0xFF1B5E20), // Verde muy oscuro
    secondary = Color(0xFF795548),        // Marrón
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD7CCC8), // Marrón claro
    onSecondaryContainer = Color(0xFF3E2723), // Marrón oscuro
    tertiary = Color(0xFF558B2F),         // Verde oliva
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFC5E1A5), // Verde oliva claro
    onTertiaryContainer = Color(0xFF33691E), // Verde oliva oscuro
    background = Color(0xFFF5F5F5),       // Gris muy claro
    onBackground = Color(0xFF1C1B1F),     // Casi negro
    surface = Color.White,
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFE8F5E9),   // Verde muy claro
    onSurfaceVariant = Color(0xFF1B5E20), // Verde oscuro
    error = Color(0xFFB00020),            // Rojo para errores
    onError = Color.White
)

private val HuertoPlanDarkColorScheme = darkColorScheme(
    primary = Color(0xFF81C784),          // Verde claro
    onPrimary = Color(0xFF1B5E20),        // Verde oscuro
    primaryContainer = Color(0xFF2E7D32),  // Verde medio
    onPrimaryContainer = Color(0xFFA5D6A7), // Verde muy claro
    secondary = Color(0xFFA1887F),        // Marrón claro
    onSecondary = Color(0xFF3E2723),      // Marrón oscuro
    secondaryContainer = Color(0xFF795548), // Marrón medio
    onSecondaryContainer = Color(0xFFD7CCC8), // Marrón muy claro
    tertiary = Color(0xFF8BC34A),         // Verde lima
    onTertiary = Color(0xFF33691E),       // Verde oliva oscuro
    tertiaryContainer = Color(0xFF558B2F), // Verde oliva
    onTertiaryContainer = Color(0xFFC5E1A5), // Verde oliva claro
    background = Color(0xFF121212),       // Negro
    onBackground = Color(0xFFE0E0E0),     // Gris claro
    surface = Color(0xFF1E1E1E),          // Gris muy oscuro
    onSurface = Color(0xFFE0E0E0),
    surfaceVariant = Color(0xFF1B5E20),   // Verde oscuro
    onSurfaceVariant = Color(0xFFA5D6A7), // Verde claro
    error = Color(0xFFCF6679),            // Rojo claro para errores
    onError = Color(0xFF000000)
)

@Composable
fun HuertoPlanTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> HuertoPlanDarkColorScheme
        else -> HuertoPlanLightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}