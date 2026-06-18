package com.sancarlina.app.ui.features.category.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.sancarlina.app.R
import com.sancarlina.app.ui.components.SancarlinaTopBar

@Composable
fun CategoryHeader(
    title: String,
    onBack: () -> Unit,
    onOpenFilters: () -> Unit,
    modifier: Modifier = Modifier
) {
    SancarlinaTopBar(
        title = title,
        modifier = modifier,
        onBack = onBack,
        actions = {
            IconButton(onClick = onOpenFilters) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = stringResource(R.string.cd_filters)
                )
            }
        }
    )
}
