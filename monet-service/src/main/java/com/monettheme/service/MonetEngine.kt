package com.monettheme.service

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import com.monettheme.api.ThemeColors
import kotlin.math.max
import kotlin.math.min

/**
 * Monet 颜色引擎
 *
 * - Android 12+ (API 31): 使用系统 WallpaperColors
 * - Android 9-11 (API 28-30): Palette API + HSL 近似 HCT 算法
 */
class MonetEngine(private val context: Context) {

    private val wallpaperManager = WallpaperManager.getInstance(context)

    fun generateFromWallpaper(darkTheme: Boolean): ThemeColors {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            generateFromSystemWallpaper(darkTheme)
        } else {
            generateFromLegacyWallpaper(darkTheme)
        }
    }

    fun generateFromColor(seedColor: Int, darkTheme: Boolean): ThemeColors {
        return createColorScheme(seedColor, darkTheme)
    }

    fun getCurrentPalette(): Map<String, Int> {
        val seedColor = extractSeedColor()
        val hsl = FloatArray(3)
        ColorUtils.colorToHSL(seedColor, hsl)
        return buildMap {
            put("seed", seedColor)
            put("primary", seedColor)
            put("secondary", harmonize(hsl[0], hsl[1], hsl[2], 0f, 0.6f))
            put("tertiary", harmonize(hsl[0], hsl[1], hsl[2], 60f, 0.7f))
            put("neutral", harmonize(hsl[0], hsl[1], hsl[2], 0f, 0.08f))
            put("neutralVariant", harmonize(hsl[0], hsl[1], hsl[2], 0f, 0.12f))
        }
    }

    private fun generateFromSystemWallpaper(darkTheme: Boolean): ThemeColors {
        val seedColor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val colors = wallpaperManager.getWallpaperColors(WallpaperManager.FLAG_SYSTEM)
            colors?.primaryColor?.toArgb()
                ?: colors?.secondaryColor?.toArgb()
                ?: Color.parseColor("#6750A4")
        } else Color.parseColor("#6750A4")
        return createColorScheme(seedColor, darkTheme)
    }

    private fun generateFromLegacyWallpaper(darkTheme: Boolean): ThemeColors {
        val seedColor = extractSeedColor()
        return createColorScheme(seedColor, darkTheme)
    }

    private fun extractSeedColor(): Int {
        return try {
            val drawable = wallpaperManager.drawable
                ?: return Color.parseColor("#6750A4")
            val bitmap = when (drawable) {
                is BitmapDrawable -> drawable.bitmap
                else -> drawable.toBitmap(256, 256, Bitmap.Config.ARGB_8888)
            }
            val palette = Palette.from(bitmap).maximumColorCount(32).generate()
            palette.vibrantSwatch?.rgb
                ?: palette.lightVibrantSwatch?.rgb
                ?: palette.darkVibrantSwatch?.rgb
                ?: palette.dominantSwatch?.rgb
                ?: Color.parseColor("#6750A4")
        } catch (e: Exception) {
            Color.parseColor("#6750A4")
        }
    }

    private fun createColorScheme(seedColor: Int, darkTheme: Boolean): ThemeColors {
        val seedHsl = FloatArray(3)
        ColorUtils.colorToHSL(seedColor, seedHsl)

        val primaryPalette = TonalPalette(seedHsl[0], max(seedHsl[1], 0.45f))
        val secondaryPalette = TonalPalette(seedHsl[0], max(seedHsl[1] * 0.55f, 0.18f))
        val tertiaryPalette = TonalPalette(rotateHue(seedHsl[0], 60f), max(seedHsl[1] * 0.65f, 0.28f))
        val neutralPalette = TonalPalette(seedHsl[0], min(seedHsl[1] * 0.08f, 0.04f))
        val neutralVariantPalette = TonalPalette(seedHsl[0], min(seedHsl[1] * 0.12f, 0.07f))
        val errorPalette = TonalPalette(10f, 0.78f)

        return if (darkTheme) {
            ThemeColors(
                primary = primaryPalette.tone(80),
                onPrimary = primaryPalette.tone(20),
                primaryContainer = primaryPalette.tone(30),
                onPrimaryContainer = primaryPalette.tone(90),
                secondary = secondaryPalette.tone(80),
                onSecondary = secondaryPalette.tone(20),
                secondaryContainer = secondaryPalette.tone(30),
                onSecondaryContainer = secondaryPalette.tone(90),
                tertiary = tertiaryPalette.tone(80),
                onTertiary = tertiaryPalette.tone(20),
                tertiaryContainer = tertiaryPalette.tone(30),
                onTertiaryContainer = tertiaryPalette.tone(90),
                error = errorPalette.tone(80),
                onError = errorPalette.tone(20),
                errorContainer = errorPalette.tone(30),
                onErrorContainer = errorPalette.tone(90),
                background = neutralPalette.tone(10),
                onBackground = neutralPalette.tone(90),
                surface = neutralPalette.tone(10),
                onSurface = neutralPalette.tone(90),
                surfaceVariant = neutralVariantPalette.tone(30),
                onSurfaceVariant = neutralVariantPalette.tone(80),
                outline = neutralVariantPalette.tone(60),
                outlineVariant = neutralVariantPalette.tone(30),
                inverseSurface = neutralPalette.tone(90),
                inverseOnSurface = neutralPalette.tone(10),
                inversePrimary = primaryPalette.tone(40),
                surfaceTint = primaryPalette.tone(80),
                scrim = neutralPalette.tone(0),
                seedColor = seedColor,
                isDarkTheme = true
            )
        } else {
            ThemeColors(
                primary = primaryPalette.tone(40),
                onPrimary = primaryPalette.tone(100),
                primaryContainer = primaryPalette.tone(90),
                onPrimaryContainer = primaryPalette.tone(10),
                secondary = secondaryPalette.tone(40),
                onSecondary = secondaryPalette.tone(100),
                secondaryContainer = secondaryPalette.tone(90),
                onSecondaryContainer = secondaryPalette.tone(10),
                tertiary = tertiaryPalette.tone(40),
                onTertiary = tertiaryPalette.tone(100),
                tertiaryContainer = tertiaryPalette.tone(90),
                onTertiaryContainer = tertiaryPalette.tone(10),
                error = errorPalette.tone(40),
                onError = errorPalette.tone(100),
                errorContainer = errorPalette.tone(90),
                onErrorContainer = errorPalette.tone(10),
                background = neutralPalette.tone(99),
                onBackground = neutralPalette.tone(10),
                surface = neutralPalette.tone(99),
                onSurface = neutralPalette.tone(10),
                surfaceVariant = neutralVariantPalette.tone(90),
                onSurfaceVariant = neutralVariantPalette.tone(30),
                outline = neutralVariantPalette.tone(50),
                outlineVariant = neutralVariantPalette.tone(80),
                inverseSurface = neutralPalette.tone(20),
                inverseOnSurface = neutralPalette.tone(95),
                inversePrimary = primaryPalette.tone(80),
                surfaceTint = primaryPalette.tone(40),
                scrim = neutralPalette.tone(0),
                seedColor = seedColor,
                isDarkTheme = false
            )
        }
    }

    private class TonalPalette(private val hue: Float, private val saturation: Float) {
        fun tone(toneValue: Int): Int {
            val lightness = when (toneValue) {
                0 -> 0.00f; 10 -> 0.08f; 20 -> 0.15f; 30 -> 0.25f; 40 -> 0.38f
                50 -> 0.50f; 60 -> 0.60f; 70 -> 0.70f; 80 -> 0.80f; 90 -> 0.92f
                95 -> 0.96f; 99 -> 0.99f; 100 -> 1.00f
                else -> toneValue / 100f
            }
            return ColorUtils.HSLToColor(floatArrayOf(hue, saturation.coerceIn(0f, 1f), lightness.coerceIn(0f, 1f)))
        }
    }

    private fun rotateHue(hue: Float, degrees: Float): Float {
        var result = (hue + degrees) % 360f
        if (result < 0) result += 360f
        return result
    }

    private fun harmonize(h: Float, s: Float, l: Float, hueShift: Float, satScale: Float): Int {
        return ColorUtils.HSLToColor(floatArrayOf(rotateHue(h, hueShift), (s * satScale).coerceIn(0f, 1f), l.coerceIn(0f, 1f)))
    }
}
