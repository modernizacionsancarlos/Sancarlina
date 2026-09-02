package com.sancarlina.app.ui.features.map.components

import androidx.compose.material3.MaterialTheme

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sancarlina.app.data.models.Tenant
import com.sancarlina.app.ui.components.SancarlinaCard

private data class CommerceAction(
    val label: String,
    val icon: ImageVector,
    val trackingName: String,
    val onClick: () -> Unit
)

@Composable
fun CommerceActionPanel(
    tenant: Tenant,
    isInRoute: Boolean,
    onTrack: (String) -> Unit,
    onToggleRoute: () -> Unit
) {
    val context = LocalContext.current
    fun open(uri: String) {
        runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(uri))) }
    }

    val actions = buildList {
        if (tenant.latitude != null && tenant.longitude != null) {
            add(
                CommerceAction("Cómo llegar", Icons.Default.Directions, "directions") {
                    open(
                        Uri.parse("https://www.google.com/maps/dir/").buildUpon()
                            .appendQueryParameter("api", "1")
                            .appendQueryParameter("destination", "${tenant.latitude},${tenant.longitude}")
                            .appendQueryParameter("travelmode", "driving")
                            .build()
                            .toString()
                    )
                }
            )
        }
        if (tenant.contactPhone.isNotBlank()) {
            add(CommerceAction("Llamar", Icons.Default.Call, "call") { open("tel:${tenant.contactPhone}") })
        }
        val whatsappDigits = tenant.whatsapp.filter(Char::isDigit)
        if (whatsappDigits.isNotBlank()) {
            add(CommerceAction("WhatsApp", Icons.AutoMirrored.Filled.Chat, "whatsapp") { open("https://wa.me/$whatsappDigits") })
        }
        if (tenant.website.isNotBlank()) {
            val url = if (tenant.website.startsWith("http")) tenant.website else "https://${tenant.website}"
            add(CommerceAction("Sitio web", Icons.Default.Language, "website") { open(url) })
        }
        if (tenant.contactEmail.isNotBlank()) {
            add(CommerceAction("Email", Icons.Default.Email, "email") { open("mailto:${tenant.contactEmail}") })
        }
        add(
            CommerceAction("Compartir", Icons.Default.Share, "share") {
                context.startActivity(
                    Intent.createChooser(
                        Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "${tenant.name}\nhttps://gondolapp.web.app/comercio/${tenant.id}"
                            )
                        },
                        "Compartir comercio"
                    )
                )
            }
        )
        add(
            CommerceAction(
                if (isInRoute) "En tu ruta" else "Sumar a ruta",
                if (isInRoute) Icons.Default.CheckCircle else Icons.Default.Route,
                "add_to_route",
                onToggleRoute
            )
        )
    }

    SancarlinaCard(modifier = Modifier.fillMaxWidth()) {
        Text("Acciones rápidas", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        actions.chunked(3).forEach { rowActions ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowActions.forEach { action ->
                    OutlinedButton(
                        onClick = {
                            onTrack(action.trackingName)
                            action.onClick()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 62.dp),
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 8.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(action.icon, action.label, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(21.dp))
                            Spacer(Modifier.height(4.dp))
                            Text(action.label, style = MaterialTheme.typography.labelSmall, maxLines = 1)
                        }
                    }
                }
                repeat(3 - rowActions.size) { Spacer(Modifier.weight(1f)) }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}
