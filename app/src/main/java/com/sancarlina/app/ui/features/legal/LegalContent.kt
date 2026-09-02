package com.sancarlina.app.ui.features.legal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sancarlina.app.BuildConfig
import com.sancarlina.app.R
import com.sancarlina.app.ui.components.SancarlinaCard
import com.sancarlina.app.ui.components.SancarlinaTopBar
import com.sancarlina.app.ui.theme.*
import com.sancarlina.app.utils.BrowserUtils

@Composable
fun LegalContent(onBack: () -> Unit) {
    val context = LocalContext.current
    val privacyUrl = stringResource(R.string.privacy_policy_url)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        SancarlinaTopBar(
            title = stringResource(R.string.legal_title),
            onBack = onBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            SancarlinaCard {
                Text(
                    text = stringResource(R.string.legal_terms_heading),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Bienvenido a GondolApp, la plataforma oficial de la Municipalidad de San Carlos para el fomento del comercio local.\n\n" +
                        "Al utilizar esta aplicación, usted acepta los siguientes términos:\n\n" +
                        "1. Veracidad de la información: Los datos proporcionados por los comercios son responsabilidad de sus titulares.\n\n" +
                        "2. Sistema de Puntos: Los beneficios obtenidos a través del sistema de fidelización están sujetos a disponibilidad en cada comercio adherido.\n\n" +
                        "3. Uso Responsable: Queda prohibido el uso de la plataforma para fines ilícitos o que atenten contra la integridad de terceros.\n\n" +
                        "Política de Privacidad\n\n" +
                        "Sus datos personales están protegidos bajo la Ley de Protección de Datos Personales. La Municipalidad solo utilizará su información para mejorar la experiencia de usuario y el envío de notificaciones oficiales previamente autorizadas.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 24.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = { BrowserUtils.openCustomTab(context, privacyUrl) },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    stringResource(R.string.legal_open_privacy_online),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Versión ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}
