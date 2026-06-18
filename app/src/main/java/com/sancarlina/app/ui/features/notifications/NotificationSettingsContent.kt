package com.sancarlina.app.ui.features.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sancarlina.app.R
import com.sancarlina.app.ui.components.SancarlinaTopBar
import com.sancarlina.app.ui.features.forms.components.FormToggleRow
import com.sancarlina.app.ui.theme.SancarlinaBackground
import com.sancarlina.app.ui.theme.SancarlinaOnSurfaceVariant

@Composable
fun NotificationSettingsContent(onBack: () -> Unit) {
    var pointsEnabled by remember { mutableStateOf(true) }
    var newsEnabled by remember { mutableStateOf(true) }
    var offersEnabled by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SancarlinaBackground)
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
                color = SancarlinaOnSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            FormToggleRow(
                title = stringResource(R.string.notification_toggle_points),
                subtitle = stringResource(R.string.notification_toggle_points_sub),
                checked = pointsEnabled,
                onCheckedChange = { pointsEnabled = it }
            )
            FormToggleRow(
                title = stringResource(R.string.notification_toggle_news),
                subtitle = stringResource(R.string.notification_toggle_news_sub),
                checked = newsEnabled,
                onCheckedChange = { newsEnabled = it }
            )
            FormToggleRow(
                title = stringResource(R.string.notification_toggle_offers),
                subtitle = stringResource(R.string.notification_toggle_offers_sub),
                checked = offersEnabled,
                onCheckedChange = { offersEnabled = it }
            )
        }
    }
}
