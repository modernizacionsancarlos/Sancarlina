package com.sancarlina.app.ui.features.emprendimiento

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sancarlina.app.R
import com.sancarlina.app.ui.components.SancarlinaElevatedCard
import com.sancarlina.app.ui.components.SancarlinaPrimaryButton
import com.sancarlina.app.ui.components.SancarlinaTextField
import com.sancarlina.app.ui.components.SancarlinaTopBar
import com.sancarlina.app.ui.theme.SancarlinaBackground
import com.sancarlina.app.ui.theme.SancarlinaOnSurfaceVariant

@Composable
fun EmprendimientoContent(onBack: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SancarlinaBackground)
    ) {
        SancarlinaTopBar(
            title = stringResource(R.string.emprendimiento_title),
            onBack = onBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                stringResource(R.string.emprendimiento_intro),
                style = MaterialTheme.typography.bodyMedium,
                color = SancarlinaOnSurfaceVariant
            )

            SancarlinaElevatedCard {
                SancarlinaTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = stringResource(R.string.emprendimiento_name_label),
                    placeholder = stringResource(R.string.emprendimiento_name_hint)
                )

                Spacer(modifier = Modifier.height(16.dp))

                SancarlinaTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = stringResource(R.string.emprendimiento_category_label),
                    placeholder = stringResource(R.string.emprendimiento_category_hint)
                )

                Spacer(modifier = Modifier.height(16.dp))

                SancarlinaTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = stringResource(R.string.emprendimiento_description_label),
                    placeholder = stringResource(R.string.emprendimiento_description_hint),
                    singleLine = false
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            SancarlinaPrimaryButton(
                text = stringResource(R.string.emprendimiento_submit),
                onClick = onBack
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
