package com.sancarlina.app.ui.features.support

import androidx.compose.material3.MaterialTheme

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
    onOpenPrivacyPolicy: () -> Unit = {},
    onNavigateToInstitutional: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
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
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            SupportCard(
                icon = Icons.Default.QuestionAnswer,
                title = stringResource(R.string.support_faq_title),
                subtitle = stringResource(R.string.support_faq_subtitle),
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(12.dp))

            SupportCard(
                icon = Icons.AutoMirrored.Filled.Chat,
                title = stringResource(R.string.support_live_title),
                subtitle = stringResource(R.string.support_live_subtitle),
                color = MaterialTheme.colorScheme.secondary
            )

            Spacer(modifier = Modifier.height(12.dp))

            SupportCard(
                icon = Icons.Default.Mail,
                title = stringResource(R.string.support_email_title),
                subtitle = stringResource(R.string.support_email_subtitle),
                color = MaterialTheme.colorScheme.tertiary
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                stringResource(R.string.support_institutional),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
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
                    onClick = onNavigateToInstitutional,
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
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.outlineVariant)
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
            color = MaterialTheme.colorScheme.onSurface
        )
        Icon(
            Icons.AutoMirrored.Filled.OpenInNew,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outlineVariant,
            modifier = Modifier.size(16.dp)
        )
    }
    if (showDivider) {
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
    }
}
