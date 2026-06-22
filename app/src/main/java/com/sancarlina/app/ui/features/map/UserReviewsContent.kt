package com.sancarlina.app.ui.features.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material.icons.filled.StarOutline
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
    var selectedFilter by remember { mutableStateOf("all") } // all, recent, top, photos

    LaunchedEffect(commerceId) {
        viewModel.loadReviews(commerceId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SancarlinaBackground)
    ) {
        SancarlinaTopBar(
            title = stringResource(R.string.reviews_title),
            onBack = onBack
        )

        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = SancarlinaPrimary)
                }
            }
            uiState.error != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = uiState.error!!, color = SancarlinaError)
                }
            }
            else -> {
                val tenant = uiState.tenant
                val rating = tenant?.rating ?: 4.8
                val reviewsCount = tenant?.reviewsCount ?: 124

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
                                        text = String.format("%.1f", rating),
                                        fontSize = 44.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SancarlinaPrimary,
                                        lineHeight = 48.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    RatingStars(rating = rating)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = stringResource(R.string.reviews_summary_based_on, reviewsCount),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = SancarlinaOnSurfaceVariant,
                                        textAlign = TextAlign.Center
                                    )
                                }

                                // Distribution bars
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    RatingDistributionRow(stars = 5, percentage = 0.85f)
                                    RatingDistributionRow(stars = 4, percentage = 0.10f)
                                    RatingDistributionRow(stars = 3, percentage = 0.03f)
                                    RatingDistributionRow(stars = 2, percentage = 0.01f)
                                    RatingDistributionRow(stars = 1, percentage = 0.01f)
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
                                selected = selectedFilter == "all" || selectedFilter == "recent",
                                onClick = { selectedFilter = "recent" }
                            )
                            SancarlinaFilterChip(
                                label = stringResource(id = R.string.reviews_filter_top),
                                selected = selectedFilter == "top",
                                onClick = { selectedFilter = "top" }
                            )
                            SancarlinaFilterChip(
                                label = stringResource(id = R.string.reviews_filter_photos),
                                selected = selectedFilter == "photos",
                                onClick = { selectedFilter = "photos" }
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
                                        color = SancarlinaPrimary.copy(alpha = 0.08f)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.RateReview,
                                                contentDescription = null,
                                                tint = SancarlinaPrimary,
                                                modifier = Modifier.size(32.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = stringResource(R.string.reviews_empty_title),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = SancarlinaOnSurface
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = stringResource(R.string.reviews_empty_message),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = SancarlinaOnSurfaceVariant,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(horizontal = 24.dp)
                                    )
                                }
                            }
                        }
                    } else {
                        items(uiState.reviews) { review ->
                            ReviewItemCard(review = review)
                        }

                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            SancarlinaPrimaryButton(
                                text = stringResource(R.string.reviews_load_more),
                                onClick = { /* Paginar en backend si existiese */ },
                                modifier = Modifier.fillMaxWidth()
                            )
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
                index == filledStars && hasHalf -> Icons.Default.StarHalf
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
            color = SancarlinaOnSurfaceVariant,
            modifier = Modifier.width(8.dp)
        )
        LinearProgressIndicator(
            progress = percentage,
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = SancarlinaPrimary,
            trackColor = SancarlinaOutlineVariant.copy(alpha = 0.25f)
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
                        color = SancarlinaPrimaryContainer
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = review.userInitials.ifBlank { "U" },
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = SancarlinaOnPrimaryContainer
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = review.userName,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = SancarlinaOnSurface
                    )
                    Text(
                        text = review.timeAgo,
                        style = MaterialTheme.typography.bodySmall,
                        color = SancarlinaOnSurfaceVariant
                    )
                }

                RatingStars(rating = review.rating.toDouble())
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = review.text,
                style = MaterialTheme.typography.bodyMedium,
                color = SancarlinaOnSurface,
                lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.15f
            )
        }
    }
}
