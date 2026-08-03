package com.sancarlina.app.ui.features.points.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.ArrowForward
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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import com.sancarlina.app.ui.theme.*

@Composable
fun QrActionCard(
    onScanClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onScanClick),
        color = SancarlinaPrimaryContainer,
        contentColor = SancarlinaOnPrimaryContainer,
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(shape = RoundedCornerShape(14.dp), color = SancarlinaOnPrimaryContainer.copy(alpha = 0.14f)) {
                Icon(
                    imageVector = Icons.Default.QrCodeScanner,
                    contentDescription = stringResource(R.string.cd_open_qr),
                    tint = SancarlinaOnPrimaryContainer,
                    modifier = Modifier.padding(12.dp).size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.points_scan_cta),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = stringResource(R.string.points_scan_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = SancarlinaOnPrimaryContainer.copy(alpha = 0.8f)
                )
            }
            Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(22.dp))
        }
    }
}
