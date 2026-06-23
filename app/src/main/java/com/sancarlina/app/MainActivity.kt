package com.sancarlina.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import com.sancarlina.app.ui.components.MainScaffold
import com.sancarlina.app.ui.theme.SancarlinaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Sancarlina)
        super.onCreate(savedInstanceState)
        android.util.Log.i("GondolApp", "MainActivity: onCreate iniciado.")
        try {
            enableEdgeToEdge()
            android.util.Log.i("GondolApp", "MainActivity: edgeToEdge habilitado.")
        } catch (e: Exception) {
            android.util.Log.e("GondolApp", "MainActivity: ERROR en enableEdgeToEdge", e)
        }
        
        setContent {
            android.util.Log.i("GondolApp", "MainActivity: setContent ejecutándose.")
            val permissionLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission(),
            ) { }

            LaunchedEffect(Unit) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            }

            SancarlinaTheme {
                MainScaffold()
            }
        }
    }
}
