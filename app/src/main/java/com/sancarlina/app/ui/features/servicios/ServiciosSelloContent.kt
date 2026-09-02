package com.sancarlina.app.ui.features.servicios

import androidx.compose.material3.MaterialTheme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterVintage
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.WineBar
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.sancarlina.app.R
import com.sancarlina.app.ui.components.SancarlinaCard
import com.sancarlina.app.ui.components.SancarlinaTopBar
import com.sancarlina.app.ui.theme.*

@Composable
fun ServiciosSelloContent(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        SancarlinaTopBar(
            title = stringResource(R.string.servicios_title),
            onBack = onBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Box(modifier = Modifier.fillMaxWidth().height(220.dp)) {
                AsyncImage(
                    model = "https://lh3.googleusercontent.com/aida-public/AB6AXuDBlnBzECUfV3Q8L9JVdnEfGXXRXrm9ZfJJ0m6tqX_9A7EchP_mYE96WOuNQsAK2rDez5ls2ZSOBgbO_O0Q-6C8z2mPntKysTbXVeoEAWu5DOP0ALbMWzGu0HhWe34w0RAPmCyOD9lsSuyWoHUOd6FY_Q3uuFxqXkSrmx7q9kczsPQcXY4f4gCsMlepsiSSQILCTM50_OL5UBRHHhVQhXNLGqBWD0GcjVj0skE_S3r06yEUjpBUgKDMA4b2McArh82DvsEZb-1EjWs",
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)))
                Column(
                    modifier = Modifier.align(Alignment.Center).padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        stringResource(R.string.servicios_hero_title),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                    Text(
                        stringResource(R.string.servicios_hero_subtitle),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }

            Column(modifier = Modifier.padding(24.dp)) {
                SancarlinaCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(48.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.WorkspacePremium, null, tint = MaterialTheme.colorScheme.secondary)
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            stringResource(R.string.servicios_guarantee_title),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        stringResource(R.string.servicios_guarantee_body),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 24.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    stringResource(R.string.servicios_categories),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ServiceBadge("Vinos", Icons.Default.WineBar, Modifier.weight(1f))
                    ServiceBadge("Miel", Icons.Default.FilterVintage, Modifier.weight(1f))
                    ServiceBadge("Artesanías", Icons.Default.Palette, Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(64.dp))
            }
        }
    }
}

@Composable
fun ServiceBadge(name: String, icon: ImageVector, modifier: Modifier = Modifier) {
    SancarlinaCard(modifier = modifier) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = name,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
