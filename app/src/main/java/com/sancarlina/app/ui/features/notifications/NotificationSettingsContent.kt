package com.sancarlina.app.ui.features.notifications

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.core.content.ContextCompat
import com.sancarlina.app.R
import com.sancarlina.app.SancarlinaApp
import com.sancarlina.app.data.repository.PushPreferencesRepository
import com.sancarlina.app.ui.components.SancarlinaTopBar
import com.sancarlina.app.ui.features.forms.components.FormToggleRow
import kotlinx.coroutines.launch

@Composable
fun NotificationSettingsContent(onBack: () -> Unit) {
    val context = LocalContext.current
    val repository = remember(context) {
        (context.applicationContext as SancarlinaApp).container.pushPreferencesRepository
    }
    val scope = rememberCoroutineScope()
    fun hasNotificationPermission(): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
    var notificationPermissionGranted by remember {
        mutableStateOf(hasNotificationPermission())
    }
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> notificationPermissionGranted = granted }
    val initial = remember(repository) { repository.current() }
    var pointsEnabled by remember { mutableStateOf(initial.points) }
    var newsEnabled by remember { mutableStateOf(initial.news) }
    var offersEnabled by remember { mutableStateOf(initial.offers) }
    var eventsEnabled by remember { mutableStateOf(initial.events) }

    fun update(key: String, enabled: Boolean) {
        scope.launch { runCatching { repository.setEnabled(key, enabled) } }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        SancarlinaTopBar(
            title = stringResource(R.string.notification_settings_title),
            onBack = onBack
        )

        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                stringResource(R.string.notification_settings_intro),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (!notificationPermissionGranted) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stringResource(R.string.notification_permission_title),
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = stringResource(R.string.notification_permission_message),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                        Button(
                            onClick = {
                                notificationPermissionLauncher.launch(
                                    Manifest.permission.POST_NOTIFICATIONS
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text(stringResource(R.string.notification_permission_cta)) }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            FormToggleRow(
                title = stringResource(R.string.notification_toggle_points),
                subtitle = stringResource(R.string.notification_toggle_points_sub),
                checked = pointsEnabled,
                onCheckedChange = { pointsEnabled = it; update(PushPreferencesRepository.KEY_POINTS, it) }
            )
            FormToggleRow(
                title = stringResource(R.string.notification_toggle_news),
                subtitle = stringResource(R.string.notification_toggle_news_sub),
                checked = newsEnabled,
                onCheckedChange = { newsEnabled = it; update(PushPreferencesRepository.KEY_NEWS, it) }
            )
            FormToggleRow(
                title = stringResource(R.string.notification_toggle_offers),
                subtitle = stringResource(R.string.notification_toggle_offers_sub),
                checked = offersEnabled,
                onCheckedChange = { offersEnabled = it; update(PushPreferencesRepository.KEY_OFFERS, it) }
            )
            FormToggleRow(
                title = "Eventos y agenda",
                subtitle = "Actividades y propuestas destacadas de San Carlos",
                checked = eventsEnabled,
                onCheckedChange = { eventsEnabled = it; update(PushPreferencesRepository.KEY_EVENTS, it) }
            )
        }
    }
}
