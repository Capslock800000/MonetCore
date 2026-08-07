package com.monettheme.client

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import com.monettheme.api.IMonetColorService
import com.monettheme.api.ThemeColors
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class MonetColorClient(private val context: Context) {

    companion object {
        const val TAG = "MonetColorClient"
        const val SERVICE_ACTION = "com.monettheme.api.IMonetColorService"
        const val SERVICE_PACKAGE = "com.monettheme.service"
    }

    private var service: IMonetColorService? = null
    private var connection: ServiceConnection? = null

    fun isServiceAvailable(): Boolean {
        val intent = Intent(SERVICE_ACTION).setPackage(SERVICE_PACKAGE)
        return context.packageManager.queryIntentServices(intent, 0).isNotEmpty()
    }

    suspend fun connect(): Boolean = suspendCancellableCoroutine { continuation ->
        val intent = Intent(SERVICE_ACTION).setPackage(SERVICE_PACKAGE)

        connection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
                service = IMonetColorService.Stub.asInterface(binder)
                continuation.resume(true)
            }
            override fun onServiceDisconnected(name: ComponentName?) {
                service = null
            }
        }

        try {
            val bound = context.bindService(intent, connection!!, Context.BIND_AUTO_CREATE)
            if (!bound) continuation.resume(false)
        } catch (e: SecurityException) {
            continuation.resumeWithException(
                IllegalStateException("缺少权限 com.monettheme.permission.GENERATE_THEME", e)
            )
        }

        continuation.invokeOnCancellation { disconnect() }
    }

    fun generateThemeFromWallpaper(darkTheme: Boolean): ThemeColors? {
        val bundle = service?.generateThemeFromWallpaper(darkTheme) ?: return null
        return if (bundle.containsKey("error")) null else ThemeColors.fromBundle(bundle)
    }

    fun generateThemeFromColor(seedColor: Int, darkTheme: Boolean): ThemeColors? {
        val bundle = service?.generateThemeFromColor(seedColor, darkTheme) ?: return null
        return if (bundle.containsKey("error")) null else ThemeColors.fromBundle(bundle)
    }

    fun getCurrentPalette(): Map<String, Int>? {
        val bundle = service?.getCurrentPalette() ?: return null
        return if (bundle.containsKey("error")) null
        else bundle.keySet().associateWith { bundle.getInt(it) }
    }

    fun getServiceVersion(): Int = service?.serviceVersion ?: -1

    fun disconnect() {
        connection?.let {
            try { context.unbindService(it) } catch (_: Exception) {}
        }
        connection = null
        service = null
    }
}
