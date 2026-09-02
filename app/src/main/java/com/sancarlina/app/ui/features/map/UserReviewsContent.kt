package com.sancarlina.app.ui.features.map

import androidx.compose.material3.MaterialTheme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale
import coil.compose.AsyncImage
import com.sancarlina.app.R
import com.sancarlina.app.ui.components.*
import com.sancarlina.app.ui.theme.*
import com.sancarlina.app.viewmodel.ReviewsViewModel
import com.sancarlina.app.viewmodel.UserReview

@Composable
fun UserReviewsContent(
    commerceId: String,
    viewModel: ReviewsViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedFilter by remember { mutableStateOf("recent") }
    val visibleReviews = remember(uiState.reviews, selectedFilter) {
        when (selectedFilter) {
            "top" -> uiState.reviews.sortedByDescending { it.rating }
            else -> uiState.reviews
        }
    }

    LaunchedEffect(commerceId) {
        viewModel.loadReviews(commerceId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        SancarlinaTopBar(
            title = stringResource(R.string.reviews_title),
            onBack = onBack
        )

        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
            uiState.error != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = uiState.error!!, color = MaterialTheme.colorScheme.error)
                }
            }
            else -> {
                val reviewsCount = uiState.reviews.size
                val rating = if (reviewsCount > 0) {
                    uiState.reviews.map { it.rating }.average()
                } else {
                    0.0
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Summary Section Card
                    item {
                        SancarlinaCard {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(24.dp)
                            ) {
                                // Score circle block
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.width(100.dp)
                                ) {
                                    Text(
                                        text = if (reviewsCount > 0) String.format(Locale.getDefault(), "%.1f", rating) else "—",
                                        fontSize = 44.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        lineHeight = 48.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    RatingStars(rating = rating)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = stringResource(R.string.reviews_summary_based_on, reviewsCount),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center
                                    )
                                }

                                // Distribution bars
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    (5 downTo 1).forEach { stars ->
                                        val percentage = if (reviewsCount == 0) 0f else {
                                            uiState.reviews.count { it.rating == stars }.toFloat() / reviewsCount
                                        }
                                        RatingDistributionRow(stars = stars, percentage = percentage)
                                    }
                                }
                            }
                        }
                    }

                    // Filter Chips Section
                    item {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            SancarlinaFilterChip(
                                label = stringResource(id = R.string.reviews_filter_recent),
                                selected = selectedFilter == "recent",
                                onClick = { selectedFilter = "recent" }
                            )
                            SancarlinaFilterChip(
                                label = stringResource(id = R.string.reviews_filter_top),
                                selected = selectedFilter == "top",
                                onClick = { selectedFilter = "top" }
                            )
                        }
                    }

                    // Reviews List
                    if (uiState.reviews.isEmpty()) {
                        item {
                            SancarlinaCard(modifier = Modifier.fillMaxWidth()) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 32.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Surface(
                                        modifier = Modifier.size(64.dp),
                                        shape = CircleShape,
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.RateReview,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(32.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = stringResource(R.string.reviews_empty_title),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = stringResource(R.string.reviews_empty_message),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(horizontal = 24.dp)
                                    )
                                }
                            }
                        }
                    } else {
                        items(visibleReviews, key = { it.id }) { review ->
                            ReviewItemCard(review = review)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RatingStars(rating: Double) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(1.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val filledStars = rating.toInt()
        val hasHalf = (rating - filledStars) >= 0.25

        repeat(5) { index ->
            val icon = when {
                index < filledStars -> Icons.Default.Star
                index == filledStars && hasHalf -> Icons.AutoMirrored.Filled.StarHalf
                else -> Icons.Default.StarOutline
            }
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFFF59E0B),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun RatingDistributionRow(stars: Int, percentage: Float) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stars.toString(),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(8.dp)
        )
        LinearProgressIndicator(
            progress = { percentage },
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)
        )
    }
}

@Composable
fun ReviewItemCard(review: UserReview) {
    SancarlinaCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar
                if (review.userAvatarUrl.isNotBlank()) {
                    AsyncImage(
                        model = review.userAvatarUrl,
                        contentDescription = review.userName,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop,
                        fallback = painterResource(id = android.R.drawable.ic_menu_gallery),
                        error = painterResource(id = android.R.drawable.ic_menu_gallery)
                    )
                } else {
                    Surface(
                        modifier = Modifier.size(40.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = review.userInitials.ifBlank { "U" },
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = review.userName,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (review.verifiedVisit) {
                            Spacer(modifier = Modifier.width(5.dp))
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "Visita verificada",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(17.dp)
                            )
                        }
                    }
                    Text(
                        text = review.timeAgo,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                RatingStars(rating = review.rating.toDouble())
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = review.text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.15f
            )
        }
    }
}
