package com.monettheme.service

import android.app.Service
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import com.monettheme.api.IMonetColorService
import com.monettheme.api.ThemeColors

class MonetColorService : Service() {

    companion object {
        const val TAG = "MonetColorService"
        const val SERVICE_VERSION = 1
    }

    private lateinit var engine: MonetEngine

    override fun onCreate() {
        super.onCreate()
        engine = MonetEngine(this)
        Log.d(TAG, "MonetColorService created")
    }

    override fun onBind(intent: Intent): IBinder {
        Log.d(TAG, "Client bound: ${intent.`package`}")
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(TAG, "Client unbound")
        return super.onUnbind(intent)
    }

    private val binder = object : IMonetColorService.Stub() {
        override fun generateThemeFromWallpaper(darkTheme: Boolean): Bundle {
            return try {
                engine.generateFromWallpaper(darkTheme).toBundle()
            } catch (e: Exception) {
                Log.e(TAG, "Failed to generate theme from wallpaper", e)
                Bundle().apply { putString("error", e.message) }
            }
        }

        override fun generateThemeFromColor(seedColor: Int, darkTheme: Boolean): Bundle {
            return try {
                engine.generateFromColor(seedColor, darkTheme).toBundle()
            } catch (e: Exception) {
                Log.e(TAG, "Failed to generate theme from color", e)
                Bundle().apply { putString("error", e.message) }
            }
        }

        override fun getCurrentPalette(): Bundle {
            return try {
                Bundle().apply {
                    engine.getCurrentPalette().forEach { (k, v) -> putInt(k, v) }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to get palette", e)
                Bundle().apply { putString("error", e.message) }
            }
        }

        override fun getServiceVersion(): Int = SERVICE_VERSION
    }
}
