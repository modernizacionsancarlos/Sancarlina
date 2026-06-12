package com.sancarlina.app.ui.features.support

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.sancarlina.app.R
import androidx.compose.ui.unit.dp
import com.sancarlina.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportContent(
    onBack: () -> Unit,
    onOpenDrawer: () -> Unit = {},
    onNavigateToLegal: () -> Unit = {},
    onOpenPrivacyPolicy: () -> Unit = {}
) {
    Box(modifier = Modifier.fillMaxSize().background(SancarlinaSurface)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = SancarlinaSurfaceContainerLow
            ) {
                Row(
                    modifier = Modifier.statusBarsPadding().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.cd_back), tint = SancarlinaPrimary)
                    }
                    Text(
                        text = "Ayuda y Soporte",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = SancarlinaOnSurface
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                Text(
                    "Estamos aquí para ayudarte. Selecciona el canal de comunicación que prefieras.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SancarlinaOnSurfaceVariant
                )

                Spacer(modifier = Modifier.height(32.dp))

                SupportCard(
                    Icons.Default.QuestionAnswer,
                    "Preguntas Frecuentes",
                    "Resuelve tus dudas rápidamente con nuestra guía.",
                    SancarlinaPrimary
                )

                Spacer(modifier = Modifier.height(16.dp))

                SupportCard(
                    Icons.AutoMirrored.Filled.Chat,
                    "Soporte en Vivo",
                    "Habla directamente con un operador municipal.",
                    SancarlinaSecondary
                )

                Spacer(modifier = Modifier.height(16.dp))

                SupportCard(
                    Icons.Default.Mail,
                    "Correo Electrónico",
                    "Envíanos tus sugerencias o reclamos detallados.",
                    SancarlinaTertiary
                )

                Spacer(modifier = Modifier.height(48.dp))

                Text(
                    "Información Institucional",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = SancarlinaOnSurface
                )

                Spacer(modifier = Modifier.height(16.dp))

                LegalMenuItem("Términos y Condiciones", onClick = onNavigateToLegal)
                LegalMenuItem("Política de Privacidad", onClick = onOpenPrivacyPolicy)
                LegalMenuItem("Acerca de GondolApp", onClick = onNavigateToLegal)
            }
        }
    }
}

@Composable
fun SupportCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    color: androidx.compose.ui.graphics.Color
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { },
        color = SancarlinaSurfaceContainerLowest,
        shape = RoundedCornerShape(24.dp),
        shadowElevation = 2.dp
    ) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(56.dp),
                color = color.copy(alpha = 0.1f),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = color)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = SancarlinaOutline)
            }
            Icon(Icons.Default.ChevronRight, null, tint = SancarlinaOutlineVariant)
        }
    }
}

@Composable
fun LegalMenuItem(text: String, onClick: () -> Unit = {}) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier.padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = text, style = MaterialTheme.typography.bodyMedium, color = SancarlinaOnSurface)
            Icon(Icons.AutoMirrored.Filled.OpenInNew, null, tint = SancarlinaOutlineVariant, modifier = Modifier.size(16.dp))
        }
    }
    HorizontalDivider(color = SancarlinaOutlineVariant.copy(alpha = 0.3f))
}
