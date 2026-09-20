// SPDX-License-Identifier: GPL-3.0-only
package helium314.keyboard.latin.utils

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import helium314.keyboard.latin.R

private val GreenLightColorScheme = lightColorScheme(
    primary = Color(0xFF4C662B),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFCDEDA3),
    onPrimaryContainer = Color(0xFF0F2000),
    secondary = Color(0xFF55624C),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFD8E7CB),
    onSecondaryContainer = Color(0xFF131F0D),
    tertiary = Color(0xFF386663),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFBBECE8),
    onTertiaryContainer = Color(0xFF00201E),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFF9FAEF),
    onBackground = Color(0xFF1A1C16),
    surface = Color(0xFFF9FAEF),
    onSurface = Color(0xFF1A1C16),
    surfaceVariant = Color(0xFFE1E5D6),
    onSurfaceVariant = Color(0xFF44483D),
    outline = Color(0xFF75796C),
    outlineVariant = Color(0xFFC3C7BA),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF3F4E8),
    surfaceContainer = Color(0xFFEEEFE3),
    surfaceContainerHigh = Color(0xFFE8E9DE),
    surfaceContainerHighest = Color(0xFFE2E3D8),
)

private val GreenDarkColorScheme = darkColorScheme(
    primary = Color(0xFFB1D18A),
    onPrimary = Color(0xFF213700),
    primaryContainer = Color(0xFF354E16),
    onPrimaryContainer = Color(0xFFCDEDA3),
    secondary = Color(0xFFBCCBB0),
    onSecondary = Color(0xFF28341F),
    secondaryContainer = Color(0xFF3E4B36),
    onSecondaryContainer = Color(0xFFD8E7CB),
    tertiary = Color(0xFFA0D0CB),
    onTertiary = Color(0xFF003735),
    tertiaryContainer = Color(0xFF1E4E4B),
    onTertiaryContainer = Color(0xFFBBECE8),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF12140E),
    onBackground = Color(0xFFE2E3D8),
    surface = Color(0xFF12140E),
    onSurface = Color(0xFFE2E3D8),
    surfaceVariant = Color(0xFF44483D),
    onSurfaceVariant = Color(0xFFC3C7BA),
    outline = Color(0xFF8F9285),
    outlineVariant = Color(0xFF44483D),
    surfaceContainerLowest = Color(0xFF0C0F09),
    surfaceContainerLow = Color(0xFF1E201A),
    surfaceContainer = Color(0xFF1E201A),
    surfaceContainerHigh = Color(0xFF292B24),
    surfaceContainerHighest = Color(0xFF34362E),
)

private val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(20.dp)
)

@Composable
fun Theme(dark: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colorScheme = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (dark) dynamicDarkColorScheme(LocalContext.current)
        else dynamicLightColorScheme(LocalContext.current)
    } else {
        if (dark) GreenDarkColorScheme
        else GreenLightColorScheme
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(
            titleLarge = Typography().titleLarge.copy(fontWeight = FontWeight.Bold),
            titleMedium = Typography().titleMedium.copy(fontWeight = FontWeight.Bold),
            titleSmall = Typography().titleSmall.copy(fontWeight = FontWeight.Bold)
        ),
        shapes = AppShapes,
        content = content
    )
}

const val previewDark = true
