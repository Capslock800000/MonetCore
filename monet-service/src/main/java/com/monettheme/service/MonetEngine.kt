package com.monettheme.service

import android.annotation.SuppressLint
import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.graphics.drawable.toBitmap
import com.google.android.material.color.utilities.DynamicScheme
import com.google.android.material.color.utilities.Hct
import com.google.android.material.color.utilities.MaterialDynamicColors
import com.google.android.material.color.utilities.QuantizerCelebi
import com.google.android.material.color.utilities.Score
import com.google.android.material.color.utilities.SchemeTonalSpot
import com.monettheme.api.ThemeColors

private const val TAG = "MonetEngine"

/**
 * Monet 颜色引擎 — 100% Google 官方 HCT + Celebi 量化 + Score 评分实现
 *
 * - Android 12+ (API 31): 系统 WallpaperColors API 提取 seed
 * - Android 9-11 (API 28-30): QuantizerCelebi + Score 官方算法提取 seed
 * - 手动图片: QuantizerCelebi + Score 官方算法提取 seed
 *
 * 核心依赖: com.google.android.material:material:1.12.0
 * 色彩空间: HCT (Hue-Chroma-Tone) — 基于 CAM16 感知模型
 */
class MonetEngine(private val context: Context) {

    private val wallpaperManager = WallpaperManager.getInstance(context)
    private val materialColors = MaterialDynamicColors()

    /**
     * 从系统壁纸生成主题
     */
    fun generateFromWallpaper(darkTheme: Boolean): ThemeColors {
        Log.d(TAG, "generateFromWallpaper, dark=$darkTheme")
        val seedColor = extractSeedColor()
        return generateFromColor(seedColor, darkTheme)
    }

    /**
     * 从指定颜色生成主题（HCT 精确算法）
     */
    fun generateFromColor(seedColor: Int, darkTheme: Boolean): ThemeColors {
        Log.d(TAG, "generateFromColor, seed=#${Integer.toHexString(seedColor)}, dark=$darkTheme")
        val hct = Hct.fromInt(seedColor)
        val scheme = SchemeTonalSpot(hct, darkTheme, 0.0)
        return schemeToThemeColors(scheme, seedColor, darkTheme)
    }

    /**
     * 从 Bitmap 生成主题 — 使用官方 QuantizerCelebi + Score 提取种子色
     */
    fun generateFromBitmap(bitmap: Bitmap, darkTheme: Boolean): ThemeColors {
        Log.d(TAG, "generateFromBitmap, size=${bitmap.width}x${bitmap.height}")
        val seedColor = extractSeedFromBitmap(bitmap)
        Log.d(TAG, "bitmap seed=#${Integer.toHexString(seedColor)}")
        return generateFromColor(seedColor, darkTheme)
    }

    /**
     * 从 URI 生成主题
     */
    fun generateFromUri(uri: Uri, darkTheme: Boolean): ThemeColors {
        context.contentResolver.openInputStream(uri)?.use { stream ->
            val bitmap = android.graphics.BitmapFactory.decodeStream(stream)
            if (bitmap != null) {
                return generateFromBitmap(bitmap, darkTheme)
            }
        }
        return generateFromWallpaper(darkTheme)
    }

    /**
     * 获取当前调色板（Map 格式，供 AIDL API 使用）
     */
    fun getCurrentPalette(): Map<String, Int> {
        val seedColor = extractSeedColor()
        val hct = Hct.fromInt(seedColor)
        val scheme = SchemeTonalSpot(hct, false, 0.0)
        return buildMap {
            put("seed", seedColor)
            put("primary", materialColors.primary().getArgb(scheme))
            put("secondary", materialColors.secondary().getArgb(scheme))
            put("tertiary", materialColors.tertiary().getArgb(scheme))
            put("error", materialColors.error().getArgb(scheme))
            put("neutral", materialColors.surface().getArgb(scheme))
            put("neutralVariant", materialColors.surfaceVariant().getArgb(scheme))
        }
    }

    // ==================== 私有方法 ====================

    /**
     * 提取种子色 — Android 12+ 用系统 API，低版本用官方 Celebi + Score
     */
    @SuppressLint("MissingPermission")
    private fun extractSeedColor(): Int {
        // Android 12+: 使用系统 WallpaperColors API（官方原生）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            try {
                val colors = wallpaperManager.getWallpaperColors(WallpaperManager.FLAG_SYSTEM)
                val color = colors?.primaryColor?.toArgb()
                    ?: colors?.secondaryColor?.toArgb()
                if (color != null) {
                    Log.d(TAG, "System WallpaperColors seed=#${Integer.toHexString(color)}")
                    return color
                }
            } catch (e: Exception) {
                Log.w(TAG, "getWallpaperColors failed, fallback to Celebi", e)
            }
        }

        // Android 9-11: 使用官方 QuantizerCelebi + Score 算法
        return try {
            val drawable = wallpaperManager.drawable
            if (drawable == null) {
                Log.w(TAG, "wallpaperManager.drawable is null, fallback to default")
                return DEFAULT_SEED
            }

            val bitmap = when (drawable) {
                is BitmapDrawable -> drawable.bitmap
                else -> drawable.toBitmap(256, 256, Bitmap.Config.ARGB_8888)
            }

            val seedColor = extractSeedFromBitmap(bitmap)
            Log.d(TAG, "Celebi+Score seed=#${Integer.toHexString(seedColor)}")
            seedColor
        } catch (e: Exception) {
            Log.e(TAG, "extractSeedColor failed", e)
            DEFAULT_SEED
        }
    }

    /**
     * 官方 Monet 图片种子色提取算法：
     * 1. 缩小到 128×128
     * 2. QuantizerCelebi 量化 → Map<color, population>
     * 3. Score 评分排序 → 取最高分
     */
    private fun extractSeedFromBitmap(bitmap: Bitmap): Int {
        // Step 1: 缩小到 128×128（官方推荐尺寸）
        val scaled = Bitmap.createScaledBitmap(bitmap, 128, 128, true)
        val width = scaled.width
        val height = scaled.height
        val pixels = IntArray(width * height)
        scaled.getPixels(pixels, 0, width, 0, 0, width, height)

        // Step 2: Celebi 量化（官方默认 maxColors = 128）
        val colorToPopulation = QuantizerCelebi.quantize(pixels, 128)

        // Step 3: Score 评分排序（官方默认 desired = 4, fallback = Google Blue）
        val rankedColors = Score.score(colorToPopulation)

        return rankedColors.firstOrNull() ?: DEFAULT_SEED
    }

    /**
     * 将 DynamicScheme 映射为 ThemeColors（28 色角色）
     */
    private fun schemeToThemeColors(scheme: DynamicScheme, seedColor: Int, darkTheme: Boolean): ThemeColors {
        return ThemeColors(
            primary = materialColors.primary().getArgb(scheme),
            onPrimary = materialColors.onPrimary().getArgb(scheme),
            primaryContainer = materialColors.primaryContainer().getArgb(scheme),
            onPrimaryContainer = materialColors.onPrimaryContainer().getArgb(scheme),
            secondary = materialColors.secondary().getArgb(scheme),
            onSecondary = materialColors.onSecondary().getArgb(scheme),
            secondaryContainer = materialColors.secondaryContainer().getArgb(scheme),
            onSecondaryContainer = materialColors.onSecondaryContainer().getArgb(scheme),
            tertiary = materialColors.tertiary().getArgb(scheme),
            onTertiary = materialColors.onTertiary().getArgb(scheme),
            tertiaryContainer = materialColors.tertiaryContainer().getArgb(scheme),
            onTertiaryContainer = materialColors.onTertiaryContainer().getArgb(scheme),
            error = materialColors.error().getArgb(scheme),
            onError = materialColors.onError().getArgb(scheme),
            errorContainer = materialColors.errorContainer().getArgb(scheme),
            onErrorContainer = materialColors.onErrorContainer().getArgb(scheme),
            background = materialColors.background().getArgb(scheme),
            onBackground = materialColors.onBackground().getArgb(scheme),
            surface = materialColors.surface().getArgb(scheme),
            onSurface = materialColors.onSurface().getArgb(scheme),
            surfaceVariant = materialColors.surfaceVariant().getArgb(scheme),
            onSurfaceVariant = materialColors.onSurfaceVariant().getArgb(scheme),
            outline = materialColors.outline().getArgb(scheme),
            outlineVariant = materialColors.outlineVariant().getArgb(scheme),
            inverseSurface = materialColors.inverseSurface().getArgb(scheme),
            inverseOnSurface = materialColors.inverseOnSurface().getArgb(scheme),
            inversePrimary = materialColors.inversePrimary().getArgb(scheme),
            surfaceTint = materialColors.surfaceTint().getArgb(scheme),
            scrim = materialColors.scrim().getArgb(scheme),
            seedColor = seedColor,
            isDarkTheme = darkTheme
        )
    }

    companion object {
        private const val DEFAULT_SEED = 0xFF6750A4.toInt()
    }
}