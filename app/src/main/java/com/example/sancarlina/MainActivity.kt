package com.example.sancarlina

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sancarlina.ui.components.MainScaffold
import com.example.sancarlina.ui.theme.SancarlinaAccent
import com.example.sancarlina.ui.theme.SancarlinaPrimary
import com.example.sancarlina.ui.theme.SancarlinaTheme
import com.example.sancarlina.utils.UpdateManager

@Composable
fun UpdateAnouncementModal(
    version: String,
    notes: String,
    onDownload: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        containerColor = Color.White,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(40.dp))
                
                // Rocket Icon Section
                Surface(
                    modifier = Modifier.size(120.dp),
                    shape = CircleShape,
                    color = Color(0xFFEDEFDF),
                    shadowElevation = 2.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.RocketLaunch,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = SancarlinaAccent
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Title & Version
                Surface(
                    color = SancarlinaPrimary,
                    shape = CircleShape
                ) {
                    Text(
                        text = version,
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "¡Sancarlina se renueva!",
                    style = MaterialTheme.typography.headlineMedium,
                    color = SancarlinaPrimary,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                
                Text(
                    text = "Descubre las nuevas funciones diseñadas para mejorar tu experiencia.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Release Notes Box
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFFEDEFDF),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "NOVEDADES",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.Gray,
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        notes.split("\n").forEach { note ->
                            if (note.isNotBlank()) {
                                Row(
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        Icons.Default.Stars,
                                        contentDescription = null,
                                        tint = SancarlinaPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = note.trim(),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Actions
                Button(
                    onClick = onDownload,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SancarlinaAccent),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Text("DESCARGAR E INSTALAR AHORA", fontWeight = FontWeight.Bold)
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                TextButton(onClick = onDismiss) {
                    Text("Recordarme más tarde", color = Color.Gray)
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    )
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val updateManager = UpdateManager(this)
        
        enableEdgeToEdge()
        
        setContent {
            var showUpdateDialog by remember { mutableStateOf(false) }
            var apkUrl by remember { mutableStateOf("") }
            var releaseNotes by remember { mutableStateOf("") }
            var latestVersionName by remember { mutableStateOf("") }

            LaunchedEffect(Unit) {
                updateManager.checkForUpdates { url, notes ->
                    apkUrl = url
                    releaseNotes = notes
                    latestVersionName = "2.4.2" 
                    showUpdateDialog = true
                }
            }

            SancarlinaTheme {
                MainScaffold()

                if (showUpdateDialog) {
                    UpdateAnouncementModal(
                        version = latestVersionName,
                        notes = releaseNotes,
                        onDownload = {
                            updateManager.downloadAndInstall(apkUrl)
                            showUpdateDialog = false
                        },
                        onDismiss = { showUpdateDialog = false }
                    )
                }
            }
        }
    }
}
