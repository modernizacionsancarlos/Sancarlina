package com.sancarlina.app.ui.features.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WineBar
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sancarlina.app.R
import com.sancarlina.app.ui.theme.SancarlinaCardShape

data class CuratedRouteItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val location: String,
    val tag: String,
    val drawableRes: Int,
    val icon: ImageVector
)

val sampleCuratedRoutes = listOf(
    CuratedRouteItem(
        id = "ruta_altamira",
        title = "Ruta de Altamira",
        subtitle = "Viñedos de extrema altura y bodegas familiares",
        location = "Paraje Altamira",
        tag = "Imperdible",
        drawableRes = R.drawable.ic_cat_vino,
        icon = Icons.Default.WineBar
    ),
    CuratedRouteItem(
        id = "ruta_sabores",
        title = "Sabores del Valle",
        subtitle = "Cocina de fuegos, truchas y empanadas criollas",
        location = "La Consulta",
        tag = "Gastronomía",
        drawableRes = R.drawable.ic_cat_gastronomia,
        icon = Icons.Default.Star
    ),
    CuratedRouteItem(
        id = "ruta_artesanos",
        title = "Cuna de Tradición",
        subtitle = "Cerámica ancestral, hilados al telar y cuero",
        location = "Villa San Carlos",
        tag = "Cultura",
        drawableRes = R.drawable.ic_cat_ceramica,
        icon = Icons.Default.Palette
    ),
    CuratedRouteItem(
        id = "ruta_huayquerias",
        title = "Huayquerías y Cañadones",
        subtitle = "Senderos de arcilla y atardeceres mágicos",
        location = "Pareditas",
        tag = "Aventura",
        drawableRes = R.drawable.ic_cat_emprendedores,
        icon = Icons.Default.Landscape
    )
)

@Composable
fun HomeCuratedRoutes(
    onRouteClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(horizontal = 2.dp, vertical = 4.dp)
    ) {
        items(sampleCuratedRoutes, key = { it.id }) { route ->
            Surface(
                modifier = Modifier
                    .width(260.dp)
                    .height(180.dp)
                    .clip(SancarlinaCardShape)
                    .clickable { onRouteClick(route.title) },
                shape = SancarlinaCardShape,
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                shadowElevation = 4.dp,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    androidx.compose.foundation.Image(
                        painter = painterResource(id = route.drawableRes),
                        contentDescription = route.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Contrast gradient overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.15f),
                                        Color.Black.copy(alpha = 0.45f),
                                        Color.Black.copy(alpha = 0.90f)
                                    )
                                )
                            )
                    )

                    // Tag badge top-right
                    Surface(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = route.tag,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    // Content bottom
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = route.location,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primaryContainer,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Text(
                            text = route.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = route.subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.85f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}
