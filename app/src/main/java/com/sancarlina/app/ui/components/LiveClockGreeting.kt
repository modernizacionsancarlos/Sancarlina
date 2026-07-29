package com.sancarlina.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sancarlina.app.ui.theme.SancarlinaOnSurfaceVariant
import com.sancarlina.app.ui.theme.SancarlinaPrimary
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun LiveClockGreeting(
    userName: String? = null,
    modifier: Modifier = Modifier
) {
    var currentTime by remember { mutableStateOf("") }
    var greeting by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val timeZone = TimeZone.getTimeZone("America/Argentina/Buenos_Aires")
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale("es", "AR")).apply {
            this.timeZone = timeZone
        }

        while (true) {
            val now = Calendar.getInstance(timeZone)
            val hour = now.get(Calendar.HOUR_OF_DAY)

            currentTime = timeFormat.format(now.time)
            greeting = when (hour) {
                in 7..11 -> "Buenos días ☀️"
                in 12..17 -> "Buenas tardes 🌤️"
                in 18..23 -> "Buenas noches 🌙"
                else -> "Buen turno nocturno 🌙"
            }
            delay(1000L)
        }
    }

    val displayName = userName?.ifBlank { null }
        ?: "Administrador"

    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = greeting,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            if (displayName.isNotBlank()) {
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = displayName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = SancarlinaPrimary
                )
            }
        }
        if (currentTime.isNotBlank()) {
            Text(
                text = "Hora actual: $currentTime",
                style = MaterialTheme.typography.labelMedium,
                color = SancarlinaOnSurfaceVariant
            )
        }
    }
}
