package com.sancarlina.app.ui.features.points.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sancarlina.app.R
import com.sancarlina.app.ui.components.SancarlinaPrimaryButton
import com.sancarlina.app.ui.theme.SancarlinaOnSurfaceVariant

@Composable
fun QrActionCard(
    onScanClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.QrCodeScanner,
            contentDescription = stringResource(R.string.cd_open_qr),
            tint = SancarlinaOnSurfaceVariant,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.points_scan_hint),
            style = MaterialTheme.typography.bodyMedium,
            color = SancarlinaOnSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        SancarlinaPrimaryButton(
            text = stringResource(R.string.points_scan_cta),
            onClick = onScanClick
        )
    }
}
