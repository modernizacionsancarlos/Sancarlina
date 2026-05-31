package com.example.sancarlina.ui.features.map

import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.sancarlina.data.model.FormSchema
import com.example.sancarlina.ui.theme.SancarlinaAccent
import com.example.sancarlina.ui.theme.SancarlinaBackground
import com.example.sancarlina.ui.theme.SancarlinaPrimary

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

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("GÓNDOLA SANCARLINA", color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = SancarlinaPrimary)
            )
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = SancarlinaPrimary)
            }
        } else {
            val tenant = uiState.tenant
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(SancarlinaBackground)
                    .verticalScroll(rememberScrollState())
            ) {
                // Hero Section
                Box(modifier = Modifier.fillMaxWidth().height(240.dp)) {
                    AsyncImage(
                        model = tenant?.imageUrl?.ifEmpty { tenant.coverUrl } ?: "",
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)))
                }

                // Info Card
                Surface(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .offset(y = (-40).dp),
                    color = Color.White,
                    shape = RoundedCornerShape(24.dp),
                    shadowElevation = 4.dp
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    tenant?.name ?: "Cargando...",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = SancarlinaPrimary
                                )
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                                    Icon(Icons.Default.Store, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text(tenant?.industry ?: "", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                }
                            }
                            Surface(
                                color = if (tenant?.status == "active") SancarlinaPrimary.copy(alpha = 0.1f) else Color.Gray.copy(alpha = 0.1f),
                                shape = CircleShape
                            ) {
                                Text(
                                    if (tenant?.status == "active") "ACTIVO" else "INACTIVO",
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (tenant?.status == "active") SancarlinaPrimary else Color.Gray,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(Modifier.height(16.dp))
                        Text(
                            tenant?.description ?: "Sin descripción disponible.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.DarkGray,
                            lineHeight = 20.sp
                        )
                        
                        // Forms Section (NEW)
                        if (uiState.forms.isNotEmpty()) {
                            Spacer(Modifier.height(24.dp))
                            Divider(color = Color.LightGray.copy(alpha = 0.5f))
                            Spacer(Modifier.height(16.dp))
                            Text(
                                "Trámites y Formularios",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = SancarlinaPrimary
                            )
                            Spacer(Modifier.height(8.dp))
                            
                            uiState.forms.forEach { form ->
                                FormItem(form = form) {
                                    val url = form.submitUrl ?: "https://gondolasancarlina.web.app/formulario/${form.id}"
                                    val intent = CustomTabsIntent.Builder().build()
                                    intent.launchUrl(context, Uri.parse(url))
                                }
                                Spacer(Modifier.height(8.dp))
                            }
                        }
                    }
                }

                // Placeholder for other sections (Products etc)
                Spacer(Modifier.height(100.dp))
            }
        }
    }
}

@Composable
fun FormItem(form: FormSchema, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F6)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E4D3))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Assignment,
                contentDescription = null,
                tint = SancarlinaAccent,
                modifier = Modifier.size(32.dp)
            )
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(form.title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                if (form.description.isNotBlank()) {
                    Text(form.description, style = MaterialTheme.typography.bodySmall, color = Color.Gray, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
            Icon(Icons.Default.ChevronRight, null, tint = Color.Gray)
        }
    }
}

@Composable
fun Tag(text: String) {
    Surface(
        color = SancarlinaBackground,
        shape = CircleShape,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
    ) {
        Text(text, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), style = MaterialTheme.typography.labelSmall)
    }
}

data class ProductMini(val id: String, val name: String, val category: String, val price: String, val imageUrl: String)
