package com.sancarlina.app.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent

/**
 * Abre una URL en Chrome Custom Tabs; si falla, intenta ACTION_VIEW; si no hay navegador, Toast.
 */
object BrowserUtils {
    fun openCustomTab(context: Context, url: String) {
        val uri = Uri.parse(url)
        try {
            val colorParams = CustomTabColorSchemeParams.Builder()
                .setToolbarColor(android.graphics.Color.parseColor("#476500"))
                .build()
            val intent = CustomTabsIntent.Builder()
                .setDefaultColorSchemeParams(colorParams)
                .build()
            intent.launchUrl(context, uri)
        } catch (e: Exception) {
            Logger.e("Error opening Custom Tab", e)
            if (!openWithViewIntent(context, uri)) {
                Toast.makeText(
                    context,
                    "No se pudo abrir el enlace. Verificá que tengas un navegador instalado.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    /** Fallback cuando Custom Tabs no está disponible. */
    private fun openWithViewIntent(context: Context, uri: Uri): Boolean {
        return try {
            val intent = Intent(Intent.ACTION_VIEW, uri)
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Logger.e("Error opening URL with ACTION_VIEW", e)
            false
        }
    }
}
