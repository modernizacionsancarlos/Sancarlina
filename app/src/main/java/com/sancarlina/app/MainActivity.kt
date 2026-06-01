package com.sancarlina.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.sancarlina.app.ui.components.MainScaffold
import com.sancarlina.app.ui.theme.SancarlinaAccent
import com.sancarlina.app.ui.theme.SancarlinaPrimary
import com.sancarlina.app.ui.theme.SancarlinaTheme
import com.sancarlina.app.utils.UpdateManager
import com.sancarlina.app.R
import kotlinx.coroutines.launch

@Composable
fun DownloadOverlay(progress: Float) {
    Dialog(
        onDismissRequest = { },
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            shape = RoundedCornerShape(24.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    progress = { progress },
                    color = SancarlinaAccent,
                    modifier = Modifier.size(64.dp),
                    strokeWidth = 6.dp
                )
                Spacer(Modifier.height(24.dp))
                Text(
                    "Descargando actualización...",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Por favor, no cierres la aplicación.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Spacer(Modifier.height(16.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(8.dp).background(Color(0xFFF0F2E1), CircleShape),
                    color = SancarlinaAccent,
                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                )
                Text(
                    "${(progress * 100).toInt()}%",
                    modifier = Modifier.padding(top = 8.dp),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = SancarlinaPrimary
                )
            }
        }
    }
}

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
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)).padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth().animateContentSize(),
                shape = RoundedCornerShape(28.dp),
                color = Color.White
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // CONTENIDO SCROLLABLE (Novedades)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 450.dp)
                            .verticalScroll(scrollState)
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Logo Section
                    Image(
                        painter = androidx.compose.ui.res.painterResource(id = R.drawable.app_logo),
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        contentScale = androidx.compose.ui.layout.ContentScale.Fit
                    )
                        
                        Spacer(Modifier.height(16.dp))
                        
                        Surface(color = SancarlinaPrimary, shape = CircleShape) {
                            Text(version, color = Color.White, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp), fontWeight = FontWeight.Bold)
                        }

                        Text("GÓNDOLA SANCARLINA", style = MaterialTheme.typography.headlineSmall, color = SancarlinaPrimary, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 12.dp))
                        
                        Spacer(Modifier.height(16.dp))

                        Surface(modifier = Modifier.fillMaxWidth(), color = Color(0xFFF9F9F6), shape = RoundedCornerShape(16.dp), border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E4D3))) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("NOVEDADES", style = MaterialTheme.typography.labelMedium, color = SancarlinaPrimary, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(8.dp))
                                notes.split("\n").forEach { note ->
                                    if (note.isNotBlank()) {
                                        Row(modifier = Modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.Top) {
                                            Icon(Icons.Default.Stars, null, tint = SancarlinaAccent, modifier = Modifier.size(14.dp).padding(top = 2.dp))
                                            Spacer(Modifier.width(8.dp))
                                            Text(note.trim().removePrefix("-").trim(), style = MaterialTheme.typography.bodyMedium)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // BOTONES FIJOS
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(horizontal = 24.dp, vertical = 16.dp)
                    ) {
                        Button(
                            onClick = onDownload,
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SancarlinaAccent),
                            shape = RoundedCornerShape(28.dp)
                        ) {
                            Text("DESCARGAR E INSTALAR", fontWeight = FontWeight.Bold)
                        }
                        
                        TextButton(onClick = onDismiss, modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 4.dp)) {
                            Text("Recordarme más tarde", color = Color.Gray)
                        }
                    }
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
            val scope = rememberCoroutineScope()
            var showUpdateDialog by remember { mutableStateOf(false) }
            var isDownloading by remember { mutableStateOf(false) }
            var downloadProgress by remember { mutableStateOf(0f) }
            var apkUrl by remember { mutableStateOf("") }
            var releaseNotes by remember { mutableStateOf("") }
            var latestVersionName by remember { mutableStateOf("") }

            val permissionLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { }

            LaunchedEffect(Unit) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }

                updateManager.checkForUpdates { url, notes ->
                    apkUrl = url
                    releaseNotes = notes
                    latestVersionName = "v3.9.1" 
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
                            showUpdateDialog = false
                            isDownloading = true
                            scope.launch {
                                updateManager.downloadAndInstallWithProgress(
                                    apkUrl = apkUrl,
                                    onProgress = { downloadProgress = it },
                                    onComplete = { isDownloading = false },
                                    onError = { 
                                        isDownloading = false
                                        Toast.makeText(this@MainActivity, "Error: $it", Toast.LENGTH_LONG).show()
                                    }
                                )
                            }
                        },
                        onDismiss = { showUpdateDialog = false }
                    )
                }

                if (isDownloading) {
                    DownloadOverlay(progress = downloadProgress)
                }
            }
        }
    }
}
