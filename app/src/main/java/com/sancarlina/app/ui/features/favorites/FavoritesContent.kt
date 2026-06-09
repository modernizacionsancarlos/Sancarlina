package com.sancarlina.app.ui.features.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.sancarlina.app.ui.theme.*
import com.sancarlina.app.viewmodel.CommerceMarker
import com.sancarlina.app.ui.features.category.CommerceCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesContent(
    onBack: () -> Unit,
    onNavigateToDetail: (String) -> Unit
) {
    // Mocking favorites for now based on stitch/favoritos
    val favorites = remember {
        listOf(
            CommerceMarker(
                id = "1",
                name = "Bodega La Celia",
                locationName = "Eugenio Bustos",
                category = "Bodegas",
                imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDqRRXzgOsXEGjd7hU4sp0ogAWj6ziCETn5orGxGaxDrds4rajh2-Oq1xI_zo1E6Qbw39ZiwJRXQaViytdfnpj-iK57JI5Ka57vnmIGZ-5c-mS0RysfSO68JxDxhTxpuUzsW-j1a8Huvco3qhtfIncJdyjjH3EDvM5RY2ssw5MAaPOedn84EqcOryHb0VYn46dXKzbqDPuf8uOqHKEJoKMqmcZ0D18ZaDlFHbVRT2v-e9a-KR2pSkHDBfgKF9ril7fFuLfhytDtyPw",
                rating = 4.9f,
                distance = "A 1.2 km"
            ),
            CommerceMarker(
                id = "2",
                name = "Miel de la Región",
                locationName = "San Carlos",
                category = "Productores",
                imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCnRCqegozoJa59a1jH6WxldD6GuLGJoHEXDiukl1ZfA9XQf53RyNzXCcQfHi01RTKmvQp17R1VKsyJKgPyLaDxyo6OvGxkAkVHNSxVynC8PjwKR4qU2NMNKkJY6FuNLJ0HSyKk3vghFKFG1m8ygnWOaG5D-WiyLDH_HcHsV3jQah9G34mumg-2f9K-twlGyY53M_5SexZT5Li5kJSmczTlY690bL61FWFREj6vYpMgd6L0x2i3zycYwzZj-vV1b4DyFyEmF8BDXz8",
                rating = 5.0f,
                distance = "A 0.5 km"
            )
        )
    }

    Box(modifier = Modifier.fillMaxSize().background(SancarlinaSurface)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // App Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = SancarlinaSurfaceContainerLow
            ) {
                Row(
                    modifier = Modifier.statusBarsPadding().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = SancarlinaPrimary)
                    }
                    Text(
                        text = "Mis Favoritos",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = SancarlinaOnSurface
                    )
                }
            }

            if (favorites.isEmpty()) {
                EmptyFavorites(onExplore = onBack)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(favorites) { commerce ->
                        CommerceCard(commerce = commerce) {
                            onNavigateToDetail(commerce.id)
                        }
                    }
                    item { Spacer(modifier = Modifier.height(100.dp)) }
                }
            }
        }
    }
}

@Composable
fun EmptyFavorites(onExplore: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(120.dp),
            color = SancarlinaSurfaceContainer,
            shape = RoundedCornerShape(32.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Default.Favorite,
                    null,
                    modifier = Modifier.size(64.dp),
                    tint = SancarlinaSecondary.copy(alpha = 0.4f)
                )
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            "Aún no tienes favoritos",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = SancarlinaOnSurface
        )
        Text(
            "Guarda los comercios que más te gusten para tenerlos siempre a mano.",
            style = MaterialTheme.typography.bodyLarge,
            color = SancarlinaOnSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(top = 12.dp)
        )
        Spacer(modifier = Modifier.height(48.dp))
        Button(
            onClick = onExplore,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SancarlinaPrimary),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text("EXPLORAR COMERCIOS", fontWeight = FontWeight.Bold)
        }
    }
}
