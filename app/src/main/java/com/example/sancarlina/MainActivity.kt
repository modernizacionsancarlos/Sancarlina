package com.example.sancarlina

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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
    val scrollState = rememberScrollState()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        // Full screen background with padding
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(),
                shape = RoundedCornerShape(28.dp),
                color = Color.White,
                tonalElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 550.dp) // Ensures it doesn't overflow screen but allows scroll
                        .verticalScroll(scrollState)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Rocket Icon Section
                    Surface(
                        modifier = Modifier.size(100.dp),
                        shape = CircleShape,
                        color = Color(0xFFF0F2E1),
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.RocketLaunch,
                                contentDescription = null,
                                modifier = Modifier.size(52.dp),
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
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(
                        text = "¡Sancarlina se renueva!",
                        style = MaterialTheme.typography.headlineSmall,
                        color = SancarlinaPrimary,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    
                    Text(
                        text = "Nuevas funciones diseñadas para mejorar tu experiencia.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Release Notes Box
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFFF9F9F6),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E4D3))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "NOVEDADES",
                                style = MaterialTheme.typography.labelMedium,
                                color = SancarlinaPrimary,
                                letterSpacing = 1.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            notes.split("\n").forEach { note ->
                                if (note.isNotBlank()) {
                                    Row(
                                        modifier = Modifier.padding(vertical = 6.dp),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Icon(
                                            Icons.Default.Stars,
                                            contentDescription = null,
                                            tint = SancarlinaAccent,
                                            modifier = Modifier.size(16.dp).padding(top = 2.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = note.trim().removePrefix("-").trim(),
                                            style = MaterialTheme.typography.bodyMedium,
                                            lineHeight = 20.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Actions
                    Button(
                        onClick = onDownload,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SancarlinaAccent),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Text("DESCARGAR E INSTALAR", fontWeight = FontWeight.Bold)
                    }
                    
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text("Recordarme más tarde", color = Color.Gray)
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
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
                    latestVersionName = "v3.6.1"
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
