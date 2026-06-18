package com.sancarlina.app.ui.features.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sancarlina.app.ui.components.SancarlinaCard
import com.sancarlina.app.ui.components.SancarlinaPrimaryButton
import com.sancarlina.app.ui.theme.SancarlinaBackground
import com.sancarlina.app.ui.theme.SancarlinaOnSurface
import com.sancarlina.app.ui.theme.SancarlinaOnSurfaceVariant
import com.sancarlina.app.ui.theme.SancarlinaPrimary

@Composable
fun StitchStatusScreen(
    icon: ImageVector,
    title: String,
    message: String,
    buttonText: String,
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconTint: Color = SancarlinaPrimary,
    buttonTestTag: String? = null
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SancarlinaBackground),
        contentAlignment = Alignment.Center
    ) {
        SancarlinaCard(modifier = Modifier.padding(24.dp)) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    modifier = Modifier.size(96.dp),
                    shape = CircleShape,
                    color = iconTint.copy(alpha = 0.12f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = iconTint
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = SancarlinaOnSurface,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyLarge,
                    color = SancarlinaOnSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                SancarlinaPrimaryButton(
                    text = buttonText,
                    onClick = onButtonClick,
                    modifier = buttonTestTag?.let { Modifier.testTag(it) } ?: Modifier
                )
            }
        }
    }
}
