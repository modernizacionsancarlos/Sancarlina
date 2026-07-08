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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sancarlina.app.R
import com.sancarlina.app.data.models.FormSchema
import com.sancarlina.app.data.models.displayImageUrl
import com.sancarlina.app.ui.components.SancarlinaCard
import com.sancarlina.app.ui.components.SancarlinaPrimaryButton
import com.sancarlina.app.ui.features.map.components.CommerceInfoCard
import com.sancarlina.app.ui.features.map.components.CommerceProfileHero
import com.sancarlina.app.ui.theme.*
import com.sancarlina.app.viewmodel.CommerceProfileViewModel
import com.sancarlina.app.utils.Logger

@Composable
fun CommerceProfileContent(
    commerceId: String,
    viewModel: CommerceProfileViewModel,
    onBack: () -> Unit,
    onNavigateToProduct: (String) -> Unit,
    onNavigateToReviews: (String) -> Unit = {},
    onNavigateToRate: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(commerceId) {
        viewModel.loadCommerce(commerceId)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SancarlinaBackground)
    ) {
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = SancarlinaPrimary)
                }
            }
            uiState.error != null || uiState.tenant == null -> {
                CommerceProfileErrorState(
                    message = uiState.error ?: stringResource(R.string.commerce_load_error),
                    onBack = onBack
                )
            }
            else -> {
                val tenant = uiState.tenant!!
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    CommerceProfileHero(imageUrl = tenant.displayImageUrl())

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(y = (-24).dp)
                            .padding(horizontal = 16.dp)
                    ) {
                        CommerceInfoCard(
                            tenant = tenant,
                            onRatingClick = { onNavigateToReviews(tenant.id) }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        SancarlinaPrimaryButton(
                            text = "Calificar comercio",
                            onClick = { onNavigateToRate(tenant.id) },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        CommerceProductsSection(onNavigateToProduct = onNavigateToProduct)

                        if (uiState.forms.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = stringResource(R.string.commerce_forms_title),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Medium,
                                color = SancarlinaOnSurface,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            uiState.forms.forEach { form ->
                                FormItem(form = form) {
                                    val url = form.submitUrl
                                        ?: "https://gondolasancarlina.web.app/formulario/${form.id}"
                                    openUrl(context, url)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(88.dp))
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .statusBarsPadding()
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.25f), CircleShape)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    stringResource(R.string.cd_back),
                    tint = Color.White
                )
            }

            IconButton(
                onClick = { viewModel.toggleFavorite() },
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.25f), CircleShape)
            ) {
                Icon(
                    imageVector = if (uiState.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = stringResource(R.string.cd_favorite),
                    tint = if (uiState.isFavorite) Color.Red else Color.White
                )
            }
        }
    }
}

@Composable
private fun CommerceProductsSection(@Suppress("UNUSED_PARAMETER") onNavigateToProduct: (String) -> Unit) {
    SancarlinaCard {
        Text(
            text = stringResource(R.string.commerce_products_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Medium,
            color = SancarlinaOnSurface
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Inventory2,
                contentDescription = null,
                tint = SancarlinaOutline,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = stringResource(R.string.commerce_products_empty),
                style = MaterialTheme.typography.bodyMedium,
                color = SancarlinaOnSurfaceVariant
            )
        }
    }
}

@Composable
private fun CommerceProfileErrorState(message: String, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        SancarlinaCard {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.Store,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = SancarlinaSecondary.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SancarlinaOnSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun FormItem(form: FormSchema, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = SancarlinaSurfaceContainerLow,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.AutoMirrored.Filled.Assignment,
                contentDescription = null,
                tint = SancarlinaSecondary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = form.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = SancarlinaOnSurface,
                modifier = Modifier.weight(1f)
            )
            Icon(
                Icons.AutoMirrored.Filled.OpenInNew,
                contentDescription = null,
                tint = SancarlinaOutline,
                modifier = Modifier.size(18.dp)
            )
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
