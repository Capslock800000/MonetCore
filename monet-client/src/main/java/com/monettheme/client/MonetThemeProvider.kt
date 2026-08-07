package com.monettheme.client

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.monettheme.api.ThemeColors

/**
 * 将 ThemeColors 转换为 Compose ColorScheme
 */
fun ThemeColors.toColorScheme(): ColorScheme {
    return if (isDarkTheme) {
        darkColorScheme(
            primary = Color(primary),
            onPrimary = Color(onPrimary),
            primaryContainer = Color(primaryContainer),
            onPrimaryContainer = Color(onPrimaryContainer),
            secondary = Color(secondary),
            onSecondary = Color(onSecondary),
            secondaryContainer = Color(secondaryContainer),
            onSecondaryContainer = Color(onSecondaryContainer),
            tertiary = Color(tertiary),
            onTertiary = Color(onTertiary),
            tertiaryContainer = Color(tertiaryContainer),
            onTertiaryContainer = Color(onTertiaryContainer),
            error = Color(error),
            onError = Color(onError),
            errorContainer = Color(errorContainer),
            onErrorContainer = Color(onErrorContainer),
            background = Color(background),
            onBackground = Color(onBackground),
            surface = Color(surface),
            onSurface = Color(onSurface),
            surfaceVariant = Color(surfaceVariant),
            onSurfaceVariant = Color(onSurfaceVariant),
            outline = Color(outline),
            outlineVariant = Color(outlineVariant),
            inverseSurface = Color(inverseSurface),
            inverseOnSurface = Color(inverseOnSurface),
            inversePrimary = Color(inversePrimary),
            surfaceTint = Color(surfaceTint),
            scrim = Color(scrim)
        )
    } else {
        lightColorScheme(
            primary = Color(primary),
            onPrimary = Color(onPrimary),
            primaryContainer = Color(primaryContainer),
            onPrimaryContainer = Color(onPrimaryContainer),
            secondary = Color(secondary),
            onSecondary = Color(onSecondary),
            secondaryContainer = Color(secondaryContainer),
            onSecondaryContainer = Color(onSecondaryContainer),
            tertiary = Color(tertiary),
            onTertiary = Color(onTertiary),
            tertiaryContainer = Color(tertiaryContainer),
            onTertiaryContainer = Color(onTertiaryContainer),
            error = Color(error),
            onError = Color(onError),
            errorContainer = Color(errorContainer),
            onErrorContainer = Color(onErrorContainer),
            background = Color(background),
            onBackground = Color(onBackground),
            surface = Color(surface),
            onSurface = Color(onSurface),
            surfaceVariant = Color(surfaceVariant),
            onSurfaceVariant = Color(onSurfaceVariant),
            outline = Color(outline),
            outlineVariant = Color(outlineVariant),
            inverseSurface = Color(inverseSurface),
            inverseOnSurface = Color(inverseOnSurface),
            inversePrimary = Color(inversePrimary),
            surfaceTint = Color(surfaceTint),
            scrim = Color(scrim)
        )
    }
}

/**
 * 便捷 Composable：使用 Monet 主题包裹内容
 *
 * 示例：
 * ```
 * MonetTheme(client = monetClient, darkTheme = isSystemInDarkTheme()) {
 *     YourAppContent()
 * }
 * ```
 */
@Composable
fun MonetTheme(
    colors: ThemeColors,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = colors.toColorScheme(),
        content = content
    )
}
