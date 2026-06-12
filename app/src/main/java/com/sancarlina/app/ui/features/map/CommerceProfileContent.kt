package com.sancarlina.app.ui.features.map

import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.sancarlina.app.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.sancarlina.app.data.models.FormSchema
import com.sancarlina.app.data.models.displayImageUrl
import com.sancarlina.app.ui.theme.*
import com.sancarlina.app.viewmodel.CommerceProfileViewModel
import com.sancarlina.app.utils.Logger

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommerceProfileContent(
    commerceId: String,
    viewModel: CommerceProfileViewModel,
    onBack: () -> Unit,
    onNavigateToProduct: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(commerceId) {
        viewModel.loadCommerce(commerceId)
    }

    Box(modifier = Modifier.fillMaxSize().background(SancarlinaSurface)) {
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = SancarlinaPrimary)
            }
        } else {
            val tenant = uiState.tenant
            val displayUrl = tenant?.displayImageUrl() ?: ""

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Hero Image
                Box(modifier = Modifier.fillMaxWidth().height(300.dp)) {
                    AsyncImage(
                        model = displayUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    // Gradient
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                androidx.compose.ui.graphics.Brush.verticalGradient(
                                    colors = listOf(Color.Black.copy(alpha = 0.4f), Color.Transparent, Color.Black.copy(alpha = 0.6f)),
                                    startY = 0f,
                                    endY = 1000f
                                )
                            )
                    )
                }

                // Profile Info Overlay Card
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = (-32).dp),
                    color = SancarlinaSurfaceContainerLowest,
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                    shadowElevation = 8.dp
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = tenant?.name ?: "",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = SancarlinaOnSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Category, null, tint = SancarlinaOutline, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = tenant?.industry ?: "",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = SancarlinaOnSurfaceVariant
                                    )
                                }
                            }
                            
                            // Favorite Toggle
                            IconButton(onClick = { /* Favorite */ }) {
                                Icon(
                                    Icons.Default.FavoriteBorder,
                                    stringResource(R.string.cd_favorite),
                                    tint = SancarlinaSecondary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Location & Contact Quick Info
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            QuickInfoChip(Icons.Default.LocationOn, "Eugenio Bustos")
                            QuickInfoChip(Icons.Default.Star, "4.9 (120)")
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Text(
                            text = "Acerca de",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = SancarlinaOnSurface
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = tenant?.description ?: "Descubre la pasión por lo nuestro en cada rincón de este comercio local.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = SancarlinaOnSurfaceVariant,
                            lineHeight = 24.sp
                        )

                        // Services / Products Preview
                        Spacer(modifier = Modifier.height(32.dp))
                        Text(
                            text = "Nuestros Productos",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = SancarlinaOnSurface
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Placeholder for products grid
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            ProductMiniCard("Miel Pura", "$4500", Modifier.weight(1f))
                            ProductMiniCard("Aceite Oliva", "$8200", Modifier.weight(1f))
                        }

                        // Forms / CTAs
                        if (uiState.forms.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(32.dp))
                            Text(
                                text = "Gestiones Disponibles",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = SancarlinaOnSurface
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            uiState.forms.forEach { form ->
                                FormItem(form = form) {
                                    val url = form.submitUrl ?: "https://gondolasancarlina.web.app/formulario/${form.id}"
                                    openUrl(context, url)
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(64.dp))
                    }
                }
            }
        }

        // Floating Back Button
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .statusBarsPadding()
                .padding(8.dp)
                .background(Color.Black.copy(alpha = 0.3f), CircleShape)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.cd_back), tint = Color.White)
        }
    }
}

@Composable
fun QuickInfoChip(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Surface(
        color = SancarlinaSurfaceContainerLow,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = SancarlinaPrimary, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = text, style = MaterialTheme.typography.labelLarge, color = SancarlinaOnSurface)
        }
    }
}

@Composable
fun ProductMiniCard(name: String, price: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.height(140.dp),
        color = SancarlinaSurfaceContainer,
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.Bottom) {
            Text(text = name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            Text(text = price, style = MaterialTheme.typography.labelLarge, color = SancarlinaPrimary)
        }
    }
}

@Composable
fun FormItem(form: FormSchema, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        color = SancarlinaSurfaceContainerLow,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.AutoMirrored.Filled.Assignment, null, tint = SancarlinaSecondary, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = form.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = SancarlinaOnSurface,
                modifier = Modifier.weight(1f)
            )
            Icon(Icons.AutoMirrored.Filled.OpenInNew, null, tint = SancarlinaOutline, modifier = Modifier.size(18.dp))
        }
    }
}

private fun openUrl(context: android.content.Context, url: String) {
    try {
        val colorParams = CustomTabColorSchemeParams.Builder()
            .setToolbarColor(android.graphics.Color.parseColor("#476500"))
            .build()
        val intent = CustomTabsIntent.Builder()
            .setDefaultColorSchemeParams(colorParams)
            .build()
        intent.launchUrl(context, Uri.parse(url))
    } catch (e: Exception) {
        Logger.e("Error opening URL: ${e.message}")
    }
}
