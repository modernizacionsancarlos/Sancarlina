package com.sancarlina.app.utils

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent

/**
 * Abre una URL en Chrome Custom Tabs (mismo patrón que formularios de comercio).
 */
object BrowserUtils {
    fun openCustomTab(context: Context, url: String) {
        try {
            val colorParams = CustomTabColorSchemeParams.Builder()
                .setToolbarColor(android.graphics.Color.parseColor("#476500"))
                .build()
            val intent = CustomTabsIntent.Builder()
                .setDefaultColorSchemeParams(colorParams)
                .build()
            intent.launchUrl(context, Uri.parse(url))
        } catch (e: Exception) {
            Logger.e("Error opening URL", e)
        }
    }
}
