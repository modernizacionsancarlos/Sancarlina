package com.sancarlina.app.ui.features.home

import androidx.compose.material3.MaterialTheme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.sancarlina.app.ui.theme.*
import com.sancarlina.app.viewmodel.BannerItem
import com.sancarlina.app.viewmodel.NewsDetailViewModel

@Composable
fun NewsDetailContent(
    newsId: String,
    viewModel: NewsDetailViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val newsItem by viewModel.newsItem.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(newsId) {
        viewModel.loadNewsDetails(newsId)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            newsItem?.let { item ->
                NewsDetailBody(item = item, onBack = onBack)
            } ?: Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Novedad no encontrada",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onBack,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Volver")
                    }
                }
            }
        }
    }
}

@Composable
private fun NewsDetailBody(
    item: BannerItem,
    onBack: () -> Unit
) {
    val scrollState = rememberScrollState()

    val displayTag = item.tag.ifEmpty { "Viticultura" }
    val displayDate = item.date.ifEmpty { "Reciente" }
    val displayAuthorName = item.authorName.ifEmpty { "Valeria Montes" }
    val displayAuthorRole = item.authorRole.ifEmpty { "Enóloga & Editora Principal" }

    // Dynamic fallback article content if Firestore only has title/subtitle
    val articleBody = item.content.ifEmpty {
        """
        Durante décadas, el prestigio de nuestros viñedos se sustentó en un puñado de variedades internacionales. Sin embargo, en los rincones más antiguos de las laderas orientales, un grupo de productores independientes está desenterrando un legado vitivinícola que el mercado moderno había decidido ignorar.

        El trabajo de rescate comenzó hace cinco años, casi por accidente. Caminando entre hileras centenarias destinadas al arranque, agrónomos notaron comportamientos inusuales en ciertas plantas: resistencia a la sequía extrema y una adaptación fenológica perfecta a los veranos cada vez más tórridos. No eran Cabernet ni Merlot; eran variedades patrimoniales adaptadas a la geografía local a través de mutaciones naturales durante más de un siglo.

        "No estamos inventando nada nuevo. Simplemente estamos escuchando lo que la tierra, a través de estas vides resilientes, ha estado intentando decirnos durante los últimos cien años."
        — Roberto Allende, Viticultor

        La transición no ha sido sencilla. Elaborar vinos con uvas de perfiles organolépticos desconocidos requiere desaprender técnicas industriales y volver a metodologías más intuitivas, casi artesanales. El uso de ánforas de arcilla y cubas de cemento crudo ha reemplazado a las relucientes barricas de roble nuevo en muchas de estas bodegas pioneras, buscando expresar la desnudez del terruño sin maquillajes maderables.

        Un futuro con raíces profundas

        Más allá de la novedad romántica, este rescate tiene implicaciones cruciales para la sostenibilidad de la industria. Frente a la innegable realidad del cambio climático, estas cepas olvidadas ofrecen un modelo de resiliencia genética inestimable. Requieren menos riego, resisten mejor las olas de calor prolongadas y, sorprendentemente, logran retener una acidez vibrante que las variedades comerciales a menudo pierden bajo el sol implacable.
        """.trimIndent()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Hero Header Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(440.dp)
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            ) {
                if (item.imageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = item.imageUrl,
                        contentDescription = item.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                // Shadow Gradient Overlays for title and status bar readability
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.4f),
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.85f)
                                ),
                                startY = 0f,
                                endY = Float.POSITIVE_INFINITY
                            )
                        )
                )

                // Content overlaid at the bottom of the image
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(horizontal = 16.dp, vertical = 32.dp)
                        .padding(bottom = 12.dp) // space for overlap container
                ) {
                    // Category Tag
                    Surface(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Text(
                            text = displayTag.uppercase(),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Date & Reading Time row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.surfaceContainerLow,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = displayDate,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.surfaceContainerLow
                        )
                        Text(
                            text = "•",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.surfaceContainerLow
                        )
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.surfaceContainerLow,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = item.readingTime,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.surfaceContainerLow
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Title
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        lineHeight = 38.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Author Row
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Author Avatar
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryFixed)
                        ) {
                            if (item.authorImageUrl.isNotEmpty()) {
                                AsyncImage(
                                    model = item.authorImageUrl,
                                    contentDescription = displayAuthorName,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                // Monogram or placeholder
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = displayAuthorName.take(1).uppercase(),
                                        color = MaterialTheme.colorScheme.onPrimaryFixed,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = buildAnnotatedString {
                                    append("Por ")
                                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primaryFixed)) {
                                        append(displayAuthorName)
                                    }
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White
                            )
                            Text(
                                text = displayAuthorRole,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }

            // Article Content Canvas (with rounded corners overlaying the hero image)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-16).dp),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                color = MaterialTheme.colorScheme.background,
                tonalElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 24.dp)
                ) {
                    val paragraphs = articleBody.split("\n\n", "\n").filter { it.isNotBlank() }

                    paragraphs.forEachIndexed { index, paragraph ->
                        when {
                            // Secondary Heading style (for lines that don't end in punctuation and are short)
                            paragraph.length < 50 && !paragraph.endsWith(".") && !paragraph.contains("\"") && !paragraph.startsWith("—") -> {
                                Spacer(modifier = Modifier.height(24.dp))
                                Text(
                                    text = paragraph,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                            // Pull Quote block style
                            paragraph.startsWith("\"") || (paragraph.contains("—") && paragraph.length < 200) -> {
                                Spacer(modifier = Modifier.height(16.dp))
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            color = MaterialTheme.colorScheme.secondaryFixed.copy(alpha = 0.2f),
                                            shape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp)
                                        )
                                        .padding(vertical = 16.dp, horizontal = 20.dp)
                                        .drawBorderLeft(3.dp, MaterialTheme.colorScheme.secondary)
                                ) {
                                    val quoteText = if (paragraph.startsWith("\"")) paragraph else {
                                        // If it's a quote with author, format beautifully
                                        val parts = paragraph.split("—")
                                        parts.firstOrNull()?.trim() ?: paragraph
                                    }
                                    val authorText = if (paragraph.startsWith("\"")) {
                                        val nextPara = paragraphs.getOrNull(index + 1)
                                        if (nextPara != null && nextPara.startsWith("—")) nextPara else null
                                    } else {
                                        val parts = paragraph.split("—")
                                        if (parts.size > 1) "— " + parts[1].trim() else null
                                    }

                                    Text(
                                        text = quoteText,
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontStyle = FontStyle.Italic,
                                            lineHeight = 28.sp
                                        ),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    authorText?.let {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = it,
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.secondary
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                            // Skip standalone author lines since they are drawn as part of the pull quote block above
                            paragraph.startsWith("—") -> {
                                // Do nothing, handled in pull quote block
                            }
                            // Normal paragraph with styled Dropcap for the very first paragraph
                            index == 0 -> {
                                val annotatedString = buildAnnotatedString {
                                    // Make first letter larger and colored
                                    val firstLetter = paragraph.take(1)
                                    val remaining = paragraph.drop(1)
                                    withStyle(
                                        style = SpanStyle(
                                            fontSize = 36.sp,
                                            fontWeight = FontWeight.Black,
                                            color = MaterialTheme.colorScheme.secondary
                                        )
                                    ) {
                                        append(firstLetter)
                                    }
                                    append(remaining)
                                }

                                Text(
                                    text = annotatedString,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        lineHeight = 26.sp,
                                        letterSpacing = 0.25.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            // Regular body paragraph
                            else -> {
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = paragraph,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        lineHeight = 26.sp,
                                        letterSpacing = 0.25.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(64.dp))
                }
            }
        }

        // Translucent Overlaid TopBar for Back navigation (Overlay Style)
        Row(
            modifier = Modifier
                .statusBarsPadding()
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        color = Color.Black.copy(alpha = 0.35f),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            IconButton(
                onClick = { /* Save article functionality placeholder */ },
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        color = Color.Black.copy(alpha = 0.35f),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.BookmarkBorder,
                    contentDescription = "Guardar",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

// Simple modifier helper to draw a left border without extra Canvas setup
private fun Modifier.drawBorderLeft(width: androidx.compose.ui.unit.Dp, color: Color) = this.drawWithContent {
    drawContent()
    val strokeWidth = width.toPx()
    drawLine(
        color = color,
        start = androidx.compose.ui.geometry.Offset(0f + strokeWidth / 2, 0f),
        end = androidx.compose.ui.geometry.Offset(0f + strokeWidth / 2, size.height),
        strokeWidth = strokeWidth
    )
}

