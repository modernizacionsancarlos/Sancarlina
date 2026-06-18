package com.sancarlina.app.ui.features.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.sancarlina.app.R
import com.sancarlina.app.ui.features.common.components.StitchStatusScreen

@Composable
fun SuccessContent(
    title: String? = null,
    message: String? = null,
    buttonText: String? = null,
    onButtonClick: () -> Unit
) {
    StitchStatusScreen(
        icon = Icons.Default.Check,
        title = title ?: stringResource(R.string.success_default_title),
        message = message ?: stringResource(R.string.success_default_message),
        buttonText = buttonText ?: stringResource(R.string.success_default_button),
        onButtonClick = onButtonClick
    )
}
