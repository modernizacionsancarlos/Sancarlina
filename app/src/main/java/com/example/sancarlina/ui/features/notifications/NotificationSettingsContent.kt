package com.example.sancarlina.ui.features.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sancarlina.ui.theme.SancarlinaAccent
import com.example.sancarlina.ui.theme.SancarlinaBackground
import com.example.sancarlina.ui.theme.SancarlinaPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsContent(onBack: () -> Unit) {
    var offersEnabled by remember { mutableStateOf(true) }
    var pointsEnabled by remember { mutableStateOf(true) }
    var messagesEnabled by remember { mutableStateOf(true) }
    var eventsEnabled by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "AJUSTES DE ALERTAS",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = SancarlinaPrimary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(SancarlinaBackground)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 2.dp
            ) {
                Column {
                    ToggleSettingItem(
                        title = "Nuevas Ofertas y Descuentos",
                        checked = offersEnabled,
                        onCheckedChange = { offersEnabled = it }
                    )
                    HorizontalDivider(color = SancarlinaBackground, thickness = 1.dp)
                    ToggleSettingItem(
                        title = "Acreditación de Puntos",
                        checked = pointsEnabled,
                        onCheckedChange = { pointsEnabled = it }
                    )
                    HorizontalDivider(color = SancarlinaBackground, thickness = 1.dp)
                    ToggleSettingItem(
                        title = "Mensajes de Productores",
                        checked = messagesEnabled,
                        onCheckedChange = { messagesEnabled = it }
                    )
                    HorizontalDivider(color = SancarlinaBackground, thickness = 1.dp)
                    ToggleSettingItem(
                        title = "Eventos y Ferias Municipales",
                        checked = eventsEnabled,
                        onCheckedChange = { eventsEnabled = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            TextButton(
                onClick = {
                    offersEnabled = true
                    pointsEnabled = true
                    messagesEnabled = true
                    eventsEnabled = false
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Restablecer valores iniciales", color = SancarlinaAccent, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ToggleSettingItem(title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = SancarlinaPrimary,
                uncheckedThumbColor = Color.Gray,
                uncheckedTrackColor = Color.LightGray
            )
        )
    }
}
