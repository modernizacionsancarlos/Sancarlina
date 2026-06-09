package com.sancarlina.app.ui.features.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sancarlina.app.ui.theme.*

@Composable
fun EmptyStateContent(
    title: String = "No encontramos resultados",
    description: String = "Prueba cambiando los filtros o revisa la ortografía de tu búsqueda.",
    icon: ImageVector = Icons.Default.SearchOff,
    primaryButtonText: String? = "VOLVER",
    onPrimaryClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SancarlinaSurface)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Surface(
                modifier = Modifier.size(140.dp),
                color = SancarlinaSurfaceContainer,
                shape = RoundedCornerShape(32.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = SancarlinaOutlineVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = SancarlinaOnSurface,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodyLarge,
                color = SancarlinaOnSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )

            if (primaryButtonText != null) {
                Spacer(modifier = Modifier.height(48.dp))
                Button(
                    onClick = onPrimaryClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SancarlinaPrimary),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Text(
                        text = primaryButtonText,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
