package com.sancarlina.app.ui.features.product

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sancarlina.app.R
import com.sancarlina.app.ui.components.SancarlinaCard
import com.sancarlina.app.ui.components.SancarlinaPrimaryButton
import com.sancarlina.app.ui.features.product.components.ProductActionBar
import com.sancarlina.app.ui.features.product.components.ProductHeroSection
import com.sancarlina.app.ui.features.product.components.ProductInfoSection
import com.sancarlina.app.ui.theme.*

@Composable
fun ProductDetailContent(
    productId: String,
    viewModel: ProductDetailViewModel = viewModel(),
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(productId) {
        viewModel.loadProduct(productId)
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
            uiState.notFound -> {
                ProductNotFoundState(onBack = onBack)
            }
            uiState.product != null -> {
                ProductDetailBody(
                    product = uiState.product!!,
                    onBack = onBack
                )
            }
        }
    }
}

@Composable
internal fun ProductDetailBody(
    product: ProductDetail,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var isFav by remember { mutableStateOf(product.isFavorite) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            ProductHeroSection(
                imageUrl = product.imageUrl,
                onBack = onBack,
                isFavorite = isFav,
                onToggleFavorite = { isFav = !isFav }
            )

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-24).dp),
                color = SancarlinaSurfaceContainerLowest,
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                shadowElevation = 6.dp
            ) {
                ProductInfoSection(
                    product = product,
                    modifier = Modifier.padding(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(100.dp))
        }

        ProductActionBar(
            onConsultClick = {
                if (product.phone.isNotBlank()) {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse("https://wa.me/${product.phone}")
                    }
                    context.startActivity(intent)
                }
            },
            enabled = product.phone.isNotBlank(),
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun ProductNotFoundState(onBack: () -> Unit) {
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
                    Icons.Default.Inventory2,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = SancarlinaSecondary.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.product_not_found_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = SancarlinaOnSurface,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(R.string.product_not_found_message),
                    style = MaterialTheme.typography.bodyMedium,
                    color = SancarlinaOnSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
                )
                SancarlinaPrimaryButton(
                    text = stringResource(R.string.cd_back),
                    onClick = onBack,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProductDetailPreview() {
    SancarlinaTheme {
        ProductDetailBody(
            product = ProductDetail(
                id = "preview",
                name = "Malbec Reserva 2022",
                location = "Mendoza, Argentina",
                description = "Vino de altura con notas a frutos rojos.",
                price = "$15.500",
                imageUrl = "",
                galleryImages = emptyList(),
                tags = listOf("Tinto", "Reserva"),
                phone = "5492610000000"
            ),
            onBack = {}
        )
    }
}
