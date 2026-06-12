package com.sancarlina.app.ui.features.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sancarlina.app.R
import com.sancarlina.app.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashContent(onTimeout: () -> Unit) {
    // Pulse animation for the isologo
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    LaunchedEffect(Unit) {
        delay(2500)
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SancarlinaSurface),
        contentAlignment = Alignment.Center
    ) {
        // Top Isologo
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_gondolapp_symbol),
                contentDescription = "GondolApp Isologo",
                modifier = Modifier
                    .fillMaxWidth(0.8f) // Takes 80% of screen width
                    .aspectRatio(1f)    // Keeps it square/proportional
                    .scale(pulseScale)
                    .alpha(pulseAlpha),
                contentScale = ContentScale.Fit
            )
            
            Spacer(modifier = Modifier.height(48.dp))

            // Center Content: Loading Indicator
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    color = SancarlinaPrimary,
                    strokeWidth = 4.dp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "CARGANDO...",
                    color = SancarlinaOnSurfaceVariant.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 2.sp
                )
            }
        }

        // Bottom Brand Signature
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 64.dp)
                .width(200.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_sancarlina_logo),
                contentDescription = "GondolApp Logotipo",
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.Fit,
                alpha = 0.9f
            )
        }
    }
}
