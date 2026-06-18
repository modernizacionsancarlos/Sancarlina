package com.sancarlina.app.ui.features.profile.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.sancarlina.app.ui.components.SancarlinaElevatedCard
import com.sancarlina.app.ui.theme.*

@Composable
fun ProfileHeroCard(
    userName: String,
    userEmail: String,
    pointsBalance: Int,
    profileImageUrl: String?,
    modifier: Modifier = Modifier
) {
    SancarlinaElevatedCard(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProfileAvatar(
                name = userName,
                imageUrl = profileImageUrl
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = userName,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = SancarlinaOnSurface
            )
            Text(
                text = userEmail.ifEmpty { "Iniciá sesión para más beneficios" },
                style = MaterialTheme.typography.bodyMedium,
                color = SancarlinaOnSurfaceVariant
            )
            if (pointsBalance > 0) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    color = SancarlinaPrimaryContainer.copy(alpha = 0.25f),
                    shape = CircleShape
                ) {
                    Text(
                        text = "$pointsBalance puntos",
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelLarge,
                        color = SancarlinaPrimary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileAvatar(name: String, imageUrl: String?) {
    if (!imageUrl.isNullOrBlank()) {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    } else {
        Surface(
            modifier = Modifier.size(96.dp),
            shape = CircleShape,
            color = SancarlinaPrimary.copy(alpha = 0.12f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                val initials = name.split(" ")
                    .filter { it.isNotBlank() }
                    .take(2)
                    .joinToString("") { it.first().uppercaseChar().toString() }
                if (initials.isNotBlank()) {
                    Text(
                        text = initials,
                        style = MaterialTheme.typography.headlineMedium,
                        color = SancarlinaPrimary,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = SancarlinaPrimary
                    )
                }
            }
        }
    }
}
