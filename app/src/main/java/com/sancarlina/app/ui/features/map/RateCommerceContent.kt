package com.sancarlina.app.ui.features.map

import androidx.compose.material3.MaterialTheme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sancarlina.app.ui.components.SancarlinaTextField
import com.sancarlina.app.ui.components.SancarlinaTopBar
import com.sancarlina.app.ui.theme.*
import com.sancarlina.app.viewmodel.RateCommerceViewModel
import kotlinx.coroutines.delay

@Composable
fun RateCommerceContent(
    commerceId: String,
    viewModel: RateCommerceViewModel,
    onBack: () -> Unit
) {
    var rating by remember { mutableIntStateOf(0) }
    var comment by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.submitted) {
        if (uiState.submitted) {
            delay(1100)
            onBack()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        SancarlinaTopBar(title = "Calificar comercio", onBack = onBack)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(22.dp)
        ) {
            if (uiState.submitted) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(72.dp)
                )
                Text(
                    text = "¡Gracias por compartir tu experiencia!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "La reseña quedó en revisión para cuidar la calidad de la comunidad.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                return@Column
            }

            Text(
                text = "¿Cómo fue tu visita?",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                repeat(5) { index ->
                    val starIndex = index + 1
                    IconButton(onClick = { rating = starIndex }, modifier = Modifier.size(52.dp)) {
                        Icon(
                            imageVector = if (starIndex <= rating) Icons.Default.Star else Icons.Default.StarOutline,
                            contentDescription = "$starIndex estrellas",
                            tint = if (starIndex <= rating) Color(0xFFF2B32A) else MaterialTheme.colorScheme.outlineVariant,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
            }

            Text(
                text = if (rating == 0) "Elegí una puntuación" else "$rating de 5 estrellas",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Contanos tu experiencia",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
                SancarlinaTextField(
                    value = comment,
                    onValueChange = { if (it.length <= 1000) comment = it },
                    placeholder = "¿Qué te gustó? ¿Qué debería saber otro visitante?",
                    singleLine = false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                )
                Text(
                    text = "${comment.length}/1000",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 4.dp)
                )
            }

            uiState.error?.let { message ->
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = SancarlinaCardShape,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            }

            Button(
                onClick = { viewModel.submit(commerceId, rating, comment) },
                enabled = rating > 0 && !uiState.isSubmitting,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                shape = SancarlinaButtonShape
            ) {
                if (uiState.isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                } else {
                    Text("Enviar reseña", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
