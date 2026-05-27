package com.example.sancarlina.ui.features.map

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.sancarlina.ui.theme.SancarlinaAccent
import com.example.sancarlina.ui.theme.SancarlinaBackground
import com.example.sancarlina.ui.theme.SancarlinaPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommerceProfileContent(
    commerceId: String,
    onBack: () -> Unit,
    onNavigateToProduct: (String) -> Unit
) {
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
                    model = "https://lh3.googleusercontent.com/aida-public/AB6AXuAZ2EmueAlUx5o1WTJsL6du3n5Bx1b6T2qkHGGaV2v6gFxNjbIt6WDD0F5PAEGM-zqbJRfcQ5s4AIjGIVZcJBa2nv-3mS_zbbbqoBpsBsM69ZTG5nUOTGImKIRfX3XytI3jorv2ovZyPRZvrGj0NR54m8A-v3WlJeYDehv9Dv6Q02b_J7tYEAD4NsqDOCqZ18QjNK9lR_FYhrfh_z8UGyUQp41LZHMyiZ7vJ5whZZAZPGMy2jPRID2kIPyMYqnHEqqPERpj3f1068k",
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
                                "Bodega Familiar",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = SancarlinaPrimary
                            )
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                                Icon(Icons.Default.LocationOn, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Ruta 40, Km 15, San Carlos", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            }
                        }
                        Surface(
                            color = SancarlinaPrimary.copy(alpha = 0.1f),
                            shape = CircleShape
                        ) {
                            Text(
                                "ABIERTO",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = SancarlinaPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    Text(
                        "Tradición vitivinícola desde 1920. Elaboramos vinos de autor y conservas artesanales con los mejores frutos de nuestra tierra.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.DarkGray,
                        lineHeight = 20.sp
                    )
                    
                    Spacer(Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Tag("Vinos de Autor")
                        Tag("Conservas")
                    }
                }
            }

            // Products Section
            Column(modifier = Modifier.padding(horizontal = 20.dp).padding(bottom = 100.dp)) {
                Text(
                    "Nuestros Productos",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = SancarlinaPrimary
                )
                Spacer(Modifier.height(16.dp))
                
                // Simple Grid manually since we are inside a vertical scroll
                val products = listOf(
                    ProductMini("1", "Malbec Reserva", "Vino Tinto", "$8.500", "https://lh3.googleusercontent.com/aida-public/AB6AXuBMTkp_qerwqnEsD9g4aTpgioe9NZ8Z5F42RvWlhK45oIxQkld6oybLPaBeKgBBtwP9FfhsCtbdboZlF85zuJj6nDRtPZUaBX0mbhQEiAjxUZXSWnwI-RzMjP8TBAWQ39gu-dGdjYUPJjV7x5Oa5zaXWEEwpQzu1k0uhGfvoCDhp7VrsxwvxxJBh7FbkpMKZpDgFDFOLn5KW-CmeVF0hSuZKgoh57QwpVzn3Iyf7qjF8LPXrVsNKKi8xBGL7tDWsf2VDLo-K3mpF7I"),
                    ProductMini("2", "Mermelada de Durazno", "Conservas", "$2.800", "https://lh3.googleusercontent.com/aida-public/AB6AXuASvc3wuOI4xoyTjdDUwtXVrmr3pLXoyJ941kjIJknoCnUJB8CWpJb171hwu6EbLyfINNYd70puwjwhs0bzN0PerwBKX1_pVbWZnP9uTjb-WKilIm_vk2pB_H-oyEP_ryx-8LtscOJz10R7L_xFyJ9R42VtY8egSACKtnlw7GSUTGpKvgPo0BtiEbbhM0BI-OMGkXJCEQ0vdea9kU9EuAydSTGL0vHlrFqEO6ap8ftWa5EciAJSGQ5jhXTMyVY-GUZS8awC-f9pfMU")
                )

                products.chunked(2).forEach { row ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        row.forEach { product ->
                            ProductCard(product, modifier = Modifier.weight(1f)) { onNavigateToProduct(product.id) }
                        }
                        if (row.size == 1) Spacer(Modifier.weight(1f))
                    }
                    Spacer(Modifier.height(16.dp))
                }
            }
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

@Composable
fun ProductCard(product: ProductMini, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().height(120.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(12.dp)) {
                Text(product.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(product.category, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                Spacer(Modifier.height(8.dp))
                Text(product.price, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Black, color = SancarlinaAccent)
            }
        }
    }
}

data class ProductMini(val id: String, val name: String, val category: String, val price: String, val imageUrl: String)
