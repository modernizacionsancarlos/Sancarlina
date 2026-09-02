package com.sancarlina.app

import android.os.Bundle
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.sancarlina.app.ui.components.MainScaffold
import com.sancarlina.app.ui.theme.SancarlinaTheme
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
            android.util.Log.i("GondolApp", "MainActivity: setContent ejecutándose.")
            SancarlinaTheme {
                MainScaffold(
                    initialRoute = notificationRoute.value,
                    onInitialRouteConsumed = { notificationRoute.value = null }
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        notificationRoute.value = intent.getStringExtra(GondolMessagingService.EXTRA_ROUTE)
    }
}
