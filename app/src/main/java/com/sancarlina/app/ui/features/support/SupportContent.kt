package com.sancarlina.app.ui.features.support

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sancarlina.app.R
import com.sancarlina.app.ui.components.SancarlinaCard
import com.sancarlina.app.ui.components.SancarlinaTopBar
import com.sancarlina.app.ui.theme.*

@Composable
fun SupportContent(
    onBack: () -> Unit,
    onOpenDrawer: () -> Unit = {},
    onNavigateToLegal: () -> Unit = {},
    onOpenPrivacyPolicy: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SancarlinaBackground)
    ) {
        SancarlinaTopBar(
            title = stringResource(R.string.support_title),
            onBack = onBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Text(
                stringResource(R.string.support_intro),
                style = MaterialTheme.typography.bodyMedium,
                color = SancarlinaOnSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            SupportCard(
                icon = Icons.Default.QuestionAnswer,
                title = stringResource(R.string.support_faq_title),
                subtitle = stringResource(R.string.support_faq_subtitle),
                color = SancarlinaPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            SupportCard(
                icon = Icons.AutoMirrored.Filled.Chat,
                title = stringResource(R.string.support_live_title),
                subtitle = stringResource(R.string.support_live_subtitle),
                color = SancarlinaSecondary
            )

            Spacer(modifier = Modifier.height(12.dp))

            SupportCard(
                icon = Icons.Default.Mail,
                title = stringResource(R.string.support_email_title),
                subtitle = stringResource(R.string.support_email_subtitle),
                color = SancarlinaTertiary
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                stringResource(R.string.support_institutional),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = SancarlinaOnSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            SancarlinaCard {
                LegalMenuItem(
                    text = stringResource(R.string.support_terms),
                    onClick = onNavigateToLegal
                )
                LegalMenuItem(
                    text = stringResource(R.string.support_privacy),
                    onClick = onOpenPrivacyPolicy
                )
                LegalMenuItem(
                    text = stringResource(R.string.support_about),
                    onClick = onNavigateToLegal,
                    showDivider = false
                )
            }
        }
    }
}

@Composable
fun SupportCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    color: Color
) {
    SancarlinaCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(52.dp),
                color = color.copy(alpha = 0.12f),
                shape = MaterialTheme.shapes.medium
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = color)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = SancarlinaOnSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = SancarlinaOutline
                )
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = SancarlinaOutlineVariant)
        }
    }
}

@Composable
fun LegalMenuItem(
    text: String,
    onClick: () -> Unit = {},
    showDivider: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = SancarlinaOnSurface
        )
        Icon(
            Icons.AutoMirrored.Filled.OpenInNew,
            contentDescription = null,
            tint = SancarlinaOutlineVariant,
            modifier = Modifier.size(16.dp)
        )
    }
    if (showDivider) {
        HorizontalDivider(color = SancarlinaOutlineVariant.copy(alpha = 0.3f))
    }
}
