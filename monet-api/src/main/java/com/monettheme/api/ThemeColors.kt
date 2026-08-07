package com.monettheme.api

import android.os.Bundle
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Material 3 完整主题色板（28 个颜色角色）
 * 可直接映射到 Compose MaterialTheme colorScheme
 */
@Parcelize
data class ThemeColors(
    val primary: Int,
    val onPrimary: Int,
    val primaryContainer: Int,
    val onPrimaryContainer: Int,
    val secondary: Int,
    val onSecondary: Int,
    val secondaryContainer: Int,
    val onSecondaryContainer: Int,
    val tertiary: Int,
    val onTertiary: Int,
    val tertiaryContainer: Int,
    val onTertiaryContainer: Int,
    val error: Int,
    val onError: Int,
    val errorContainer: Int,
    val onErrorContainer: Int,
    val background: Int,
    val onBackground: Int,
    val surface: Int,
    val onSurface: Int,
    val surfaceVariant: Int,
    val onSurfaceVariant: Int,
    val outline: Int,
    val outlineVariant: Int,
    val inverseSurface: Int,
    val inverseOnSurface: Int,
    val inversePrimary: Int,
    val surfaceTint: Int,
    val scrim: Int,
    val seedColor: Int,
    val isDarkTheme: Boolean
) : Parcelable {

    companion object {
        private const val P = "monet_"

        fun fromBundle(b: Bundle) = ThemeColors(
            primary = b.getInt("${P}primary"),
            onPrimary = b.getInt("${P}onPrimary"),
            primaryContainer = b.getInt("${P}primaryContainer"),
            onPrimaryContainer = b.getInt("${P}onPrimaryContainer"),
            secondary = b.getInt("${P}secondary"),
            onSecondary = b.getInt("${P}onSecondary"),
            secondaryContainer = b.getInt("${P}secondaryContainer"),
            onSecondaryContainer = b.getInt("${P}onSecondaryContainer"),
            tertiary = b.getInt("${P}tertiary"),
            onTertiary = b.getInt("${P}onTertiary"),
            tertiaryContainer = b.getInt("${P}tertiaryContainer"),
            onTertiaryContainer = b.getInt("${P}onTertiaryContainer"),
            error = b.getInt("${P}error"),
            onError = b.getInt("${P}onError"),
            errorContainer = b.getInt("${P}errorContainer"),
            onErrorContainer = b.getInt("${P}onErrorContainer"),
            background = b.getInt("${P}background"),
            onBackground = b.getInt("${P}onBackground"),
            surface = b.getInt("${P}surface"),
            onSurface = b.getInt("${P}onSurface"),
            surfaceVariant = b.getInt("${P}surfaceVariant"),
            onSurfaceVariant = b.getInt("${P}onSurfaceVariant"),
            outline = b.getInt("${P}outline"),
            outlineVariant = b.getInt("${P}outlineVariant"),
            inverseSurface = b.getInt("${P}inverseSurface"),
            inverseOnSurface = b.getInt("${P}inverseOnSurface"),
            inversePrimary = b.getInt("${P}inversePrimary"),
            surfaceTint = b.getInt("${P}surfaceTint"),
            scrim = b.getInt("${P}scrim"),
            seedColor = b.getInt("${P}seedColor"),
            isDarkTheme = b.getBoolean("${P}isDarkTheme")
        )
    }

    fun toBundle() = Bundle().apply {
        putInt("${P}primary", primary)
        putInt("${P}onPrimary", onPrimary)
        putInt("${P}primaryContainer", primaryContainer)
        putInt("${P}onPrimaryContainer", onPrimaryContainer)
        putInt("${P}secondary", secondary)
        putInt("${P}onSecondary", onSecondary)
        putInt("${P}secondaryContainer", secondaryContainer)
        putInt("${P}onSecondaryContainer", onSecondaryContainer)
        putInt("${P}tertiary", tertiary)
        putInt("${P}onTertiary", onTertiary)
        putInt("${P}tertiaryContainer", tertiaryContainer)
        putInt("${P}onTertiaryContainer", onTertiaryContainer)
        putInt("${P}error", error)
        putInt("${P}onError", onError)
        putInt("${P}errorContainer", errorContainer)
        putInt("${P}onErrorContainer", onErrorContainer)
        putInt("${P}background", background)
        putInt("${P}onBackground", onBackground)
        putInt("${P}surface", surface)
        putInt("${P}onSurface", onSurface)
        putInt("${P}surfaceVariant", surfaceVariant)
        putInt("${P}onSurfaceVariant", onSurfaceVariant)
        putInt("${P}outline", outline)
        putInt("${P}outlineVariant", outlineVariant)
        putInt("${P}inverseSurface", inverseSurface)
        putInt("${P}inverseOnSurface", inverseOnSurface)
        putInt("${P}inversePrimary", inversePrimary)
        putInt("${P}surfaceTint", surfaceTint)
        putInt("${P}scrim", scrim)
        putInt("${P}seedColor", seedColor)
        putBoolean("${P}isDarkTheme", isDarkTheme)
    }
}
