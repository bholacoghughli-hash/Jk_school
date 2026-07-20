package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

// Sleek Interface Rounded Corner Shapes (from design: rounded-[2rem] and rounded-2xl)
val SleekShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),  // rounded-2xl
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp) // rounded-[2rem]
)

private val DarkColorScheme = darkColorScheme(
    primary = SchoolPrimaryDark,
    secondary = SchoolSecondaryDark,
    tertiary = SchoolTertiaryDark,
    background = SchoolBgDark,
    surface = SchoolSurfaceDark,
    onPrimary = SchoolBgDark,
    onSecondary = SchoolBgDark,
    onTertiary = SchoolBgDark,
    onBackground = SchoolTextPrimaryDark,
    onSurface = SchoolTextPrimaryDark,
    surfaceVariant = SchoolSurfaceDark,
    onSurfaceVariant = SchoolTextSecondaryDark
)

private val LightColorScheme = lightColorScheme(
    primary = SchoolPrimaryLight,
    secondary = SchoolSecondaryLight,
    tertiary = SchoolTertiaryLight,
    background = SchoolBgLight,
    surface = SchoolSurfaceLight,
    onPrimary = SchoolSurfaceLight,
    onSecondary = SchoolSurfaceLight,
    onTertiary = SchoolSurfaceLight,
    onBackground = SchoolTextPrimaryLight,
    onSurface = SchoolTextPrimaryLight,
    surfaceVariant = SchoolSurfaceLight,
    onSurfaceVariant = SchoolTextSecondaryLight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = SleekShapes,
        content = content
    )
}
