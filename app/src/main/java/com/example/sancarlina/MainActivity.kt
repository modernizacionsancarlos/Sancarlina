package com.example.sancarlina

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import com.example.sancarlina.ui.components.MainScaffold
import com.example.sancarlina.ui.theme.SancarlinaTheme
import com.example.sancarlina.utils.UpdateManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val updateManager = UpdateManager(this)
        
        enableEdgeToEdge()
        
        setContent {
            var showUpdateDialog by remember { mutableStateOf(false) }
            var apkUrl by remember { mutableStateOf("") }
            var releaseNotes by remember { mutableStateOf("") }

            LaunchedEffect(Unit) {
                updateManager.checkForUpdates { url, notes ->
                    apkUrl = url
                    releaseNotes = notes
                    showUpdateDialog = true
                }
            }

            SancarlinaTheme {
                MainScaffold()

                if (showUpdateDialog) {
                    AlertDialog(
                        onDismissRequest = { showUpdateDialog = false },
                        title = { Text("Actualización Disponible") },
                        text = { Text(releaseNotes) },
                        confirmButton = {
                            TextButton(onClick = {
                                updateManager.downloadAndInstall(apkUrl)
                                showUpdateDialog = false
                            }) {
                                Text("DESCARGAR")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showUpdateDialog = false }) {
                                Text("LUEGO")
                            }
                        }
                    )
                }
            }
        }
    }
}
