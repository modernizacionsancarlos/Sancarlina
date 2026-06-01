package com.sancarlina.app.ui.features.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.sancarlina.app.ui.theme.SancarlinaAccent
import com.sancarlina.app.ui.theme.SancarlinaBackground
import com.sancarlina.app.ui.theme.SancarlinaPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesContent(onBack: () -> Unit, onNavigateToDetail: (String) -> Unit) {
    // Mock list for now
    val favorites = listOf(
        FavoriteItem("1", "Miel Pura San Carlos", "Apicultura Local", "https://lh3.googleusercontent.com/aida-public/AB6AXuBbZcOe6LGPf9IHIetVYbYlIe851D3RrdefjH21VFJkBXyTwU9dm4YT45ezEmJOHJ-9L_Hk6vP2zM2eXdAtUI64J49VoGaD2EEW17uL1DhFRiZx3hDfMiUv2HG0g-V-OItP2fug0og4qXTJqbJ7mV678SzHfwQwRP4JxLjXQMMtNfheP8h7OZJd0R31v6_VYKzMi5_biHRHMFWMGPfWjrUelbz7C2j-LOJOhc3Fy6ekoBNsZxQS4xlfXEi_kSJe5t5XJ7TwWzW8N50"),
        FavoriteItem("2", "Lácteos La Esperanza", "Quesos de Autor", "https://lh3.googleusercontent.com/aida-public/AB6AXuCRgK4lK09RMkh88UpNMZdpxSTy2F0zWF8qReq_WQE8t8oyqEk1C4nWDNUpTbAof3NIsgZ3vt5dFxlKwBh-k9qfVrgbbK4fwySdd4k9EvkU3WGa5K4R9W2UFB9tD7yHro9ovGR_cVaYLPKTJWwr8th_I4wwDBaJXFWSsSb43pbCcbtgYWwEwx5pBEq99NAGcbDu5SjesQmJwLeV5_tvp5efTW7Dh6FJ_3E4sruaUC3yrHJsKXcrmWPFBhEAlPXT56zFVcQVCyaJdWE"),
        FavoriteItem("3", "Bodega El Terruño", "Vinos Regionales", "https://lh3.googleusercontent.com/aida-public/AB6AXuBzUcWDFE8fISdffVNZRa_9ByGxTrM9hIcw9FBi6XjAzQOIO0X171Ugo7xJx61_CvHlJAgMOTAM_UVQqPfLvAVUCfBTKwGD-7kRmOCdNEVJGIiWyUGuUqtLCdw0Tx21fZkXiytVoOcfIlQF3BgI2KqHKWw3Evq-R_rSMWwFfpG1dhFhsq5pMh-UzZPsNXF8Q5SzGuq38Y-YZyy7qTVYIJzj43bWnCz51M3N-kGZFHQVFgMWdHCBI_iG_XGxIkulYaJ5yppPvTLhfd8")
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "GÓNDOLA SANCARLINA",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = SancarlinaPrimary
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(SancarlinaBackground),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(favorites) { item ->
                FavoriteCard(item) {
                    onNavigateToDetail(item.id)
                }
            }
            
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun FavoriteCard(item: FavoriteItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(88.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = item.brand,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
                
                IconButton(onClick = { /* TODO */ }) {
                    Icon(Icons.Default.Favorite, contentDescription = "Quitar", tint = SancarlinaAccent)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = { /* TODO: WhatsApp */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SancarlinaAccent)
            ) {
                Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("CONTACTO DIRECTO POR WHATSAPP", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

data class FavoriteItem(
    val id: String,
    val name: String,
    val brand: String,
    val imageUrl: String
)
