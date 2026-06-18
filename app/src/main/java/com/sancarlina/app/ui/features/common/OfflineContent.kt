package com.sancarlina.app.ui.features.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.sancarlina.app.R
import com.sancarlina.app.ui.features.common.components.StitchStatusScreen

@Composable
fun OfflineContent(onRetry: () -> Unit) {
    StitchStatusScreen(
        icon = Icons.Default.WifiOff,
        title = stringResource(R.string.offline_title),
        message = stringResource(R.string.offline_message),
        buttonText = stringResource(R.string.offline_retry),
        onButtonClick = onRetry,
        buttonTestTag = "offline_retry"
    )
}
