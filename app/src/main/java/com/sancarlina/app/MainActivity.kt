package com.sancarlina.app

import android.os.Bundle
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.sancarlina.app.ui.components.MainScaffold
import com.sancarlina.app.ui.theme.AppTheme
import com.sancarlina.app.ui.theme.LocalThemeController
import com.sancarlina.app.ui.theme.SancarlinaTheme
import com.sancarlina.app.ui.theme.ThemeController
import com.sancarlina.app.utils.PrefsManager
import com.sancarlina.app.notifications.GondolMessagingService

class MainActivity : ComponentActivity() {
    private val notificationRoute = mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        // Uses the device theme during startup; SancarlinaTheme keeps icon
        // appearance synchronized after Compose is attached.
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        notificationRoute.value = intent.getStringExtra(GondolMessagingService.EXTRA_ROUTE)
        android.util.Log.i("GondolApp", "MainActivity: onCreate iniciado.")
        
        setContent {
            val context = LocalContext.current
            val prefsManager = remember(context) { PrefsManager(context.applicationContext) }
            var currentTheme by remember { mutableStateOf(prefsManager.getAppTheme()) }
            val systemInDark = isSystemInDarkTheme()

            // Modo claro es el de la app de forma predeterminada
            val isDark = when (currentTheme) {
                AppTheme.LIGHT -> false
                AppTheme.DARK -> true
                AppTheme.SYSTEM -> systemInDark
            }

            val themeController = remember(currentTheme, isDark) {
                ThemeController(
                    currentTheme = currentTheme,
                    isDark = isDark,
                    setTheme = { newTheme ->
                        currentTheme = newTheme
                        prefsManager.setAppTheme(newTheme)
                    }
                )
            }

            CompositionLocalProvider(LocalThemeController provides themeController) {
                SancarlinaTheme(darkTheme = isDark) {
                    MainScaffold(
                        initialRoute = notificationRoute.value,
                        onInitialRouteConsumed = { notificationRoute.value = null }
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        notificationRoute.value = intent.getStringExtra(GondolMessagingService.EXTRA_ROUTE)
    }
}
