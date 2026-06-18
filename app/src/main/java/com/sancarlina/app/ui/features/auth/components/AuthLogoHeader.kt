package com.sancarlina.app.ui.features.auth.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sancarlina.app.R
import com.sancarlina.app.ui.theme.SancarlinaPrimary
import com.sancarlina.app.ui.theme.SancarlinaSurfaceContainerLow

@Composable
fun AuthLogoHeader(
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = SancarlinaSurfaceContainerLow
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp)
        ) {
            if (onBack != null) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.align(Alignment.CenterStart).padding(start = 4.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        stringResource(R.string.cd_back),
                        tint = SancarlinaPrimary
                    )
                }
            }
            Image(
                painter = painterResource(id = R.drawable.ic_sancarlina_logo),
                contentDescription = stringResource(R.string.app_name),
                modifier = Modifier
                    .height(44.dp)
                    .align(Alignment.Center)
            )
        }
    }
}
