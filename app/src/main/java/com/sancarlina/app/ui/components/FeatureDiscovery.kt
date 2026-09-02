package com.sancarlina.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.sancarlina.app.R

data class GuideStep(
    val title: String,
    val description: String,
    val alignment: Alignment = Alignment.Center
)

@Composable
fun QuickGuide(onFinish: () -> Unit) {
    val steps = listOf(
        GuideStep(
            "¡Bienvenido al Inicio!",
            "Aquí encontrarás las mejores ofertas del día y categorías de comercios locales.",
            Alignment.TopCenter
        ),
        GuideStep(
            "Mapa Interactivo",
            "Toca aquí para ver dónde están los negocios y cómo llegar a ellos.",
            Alignment.BottomCenter
        ),
        GuideStep(
            "Sistema de Puntos",
            "Suma puntos con tus compras y canjéalos por premios increíbles.",
            Alignment.BottomCenter
        ),
        GuideStep(
            "Tu Perfil",
            "Gestiona tus datos, favoritos y revisa tus actualizaciones aquí.",
            Alignment.BottomCenter
        )
    )

    var currentStep by remember { mutableIntStateOf(0) }

    Dialog(
        onDismissRequest = { },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.65f))
                .clickable {
                    if (currentStep < steps.size - 1) {
                        currentStep++
                    } else {
                        onFinish()
                    }
                }
        ) {
            val step = steps[currentStep]

            SancarlinaCard(
                modifier = Modifier
                    .align(step.alignment)
                    .padding(32.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = step.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = step.description,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        repeat(steps.size) { index ->
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (index == currentStep) MaterialTheme.colorScheme.primaryFixedDim else Color.LightGray
                                    )
                            )
                            if (index < steps.size - 1) Spacer(modifier = Modifier.width(8.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = if (currentStep == steps.size - 1) {
                            stringResource(R.string.guide_done).uppercase()
                        } else {
                            stringResource(R.string.guide_continue).uppercase()
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
