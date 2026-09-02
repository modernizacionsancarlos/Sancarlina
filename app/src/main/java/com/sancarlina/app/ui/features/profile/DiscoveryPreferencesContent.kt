package com.sancarlina.app.ui.features.profile

import androidx.compose.material3.MaterialTheme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sancarlina.app.SancarlinaApp
import com.sancarlina.app.ui.components.SancarlinaTopBar
import com.sancarlina.app.ui.theme.*
import kotlinx.coroutines.launch

private data class InterestOption(val id: String, val label: String, val icon: ImageVector)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoveryPreferencesContent(onBack: () -> Unit) {
    val context = LocalContext.current
    val repository = remember(context) {
        (context.applicationContext as SancarlinaApp).container.discoveryPreferencesRepository
    }
    val saved by repository.interests.collectAsState()
    var selected by remember(saved) { mutableStateOf(saved) }
    var savedMessage by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val options = remember {
        listOf(
            InterestOption("vino", "Vinos y bodegas", Icons.Default.WineBar),
            InterestOption("gastronomía", "Gastronomía", Icons.Default.Restaurant),
            InterestOption("naturaleza", "Naturaleza", Icons.Default.Landscape),
            InterestOption("aventura", "Aventura", Icons.Default.Hiking),
            InterestOption("cultura", "Cultura e historia", Icons.Default.Museum),
            InterestOption("familia", "Planes en familia", Icons.Default.FamilyRestroom)
        )
    }

    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        SancarlinaTopBar(title = "Mis intereses", onBack = onBack)
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("¿Qué te gustaría descubrir?", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
            Text(
                "Elegí tus temas favoritos para ver primero los lugares y experiencias más relevantes para vos.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(4.dp))
            options.forEach { option ->
                val checked = option.id in selected
                Surface(
                    onClick = {
                        selected = if (checked) selected - option.id else selected + option.id
                        savedMessage = false
                    },
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp),
                    color = if (checked) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerLow,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (checked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                    )
                ) {
                    Row(
                        Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Icon(option.icon, null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(14.dp))
                        Text(option.label, Modifier.weight(1f), fontWeight = FontWeight.SemiBold)
                        Checkbox(checked = checked, onCheckedChange = null)
                    }
                }
            }
            if (savedMessage) {
                Text("Preferencias guardadas", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
            }
            Button(
                onClick = {
                    scope.launch {
                        runCatching { repository.saveInterests(selected) }
                        savedMessage = true
                    }
                },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp),
                enabled = selected != saved
            ) {
                Icon(Icons.Default.Check, null)
                Spacer(Modifier.width(8.dp))
                Text("Guardar preferencias", fontWeight = FontWeight.Bold)
            }
        }
    }
}
