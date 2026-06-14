package com.afeka.gym.shared.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Shared Material 3 theme. Each app passes its own [accent] colour (blue for
 * the trainer, green for the trainee) so the two apps look distinct while
 * reusing the exact same theming code.
 */
@Composable
fun WorkoutTheme(
    accent: Color,
    content: @Composable () -> Unit
) {
    val colorScheme = if (isSystemInDarkTheme()) {
        darkColorScheme(
            primary = accent,
            secondary = accent
        )
    } else {
        lightColorScheme(
            primary = accent,
            secondary = accent
        )
    }
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
