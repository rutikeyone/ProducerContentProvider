package com.contentprovider.core.presentation.views

import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier

@Composable
fun AppElevatedButton(
    modifier: Modifier,
    label: String,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    ElevatedButton(
        modifier = modifier,
        enabled = enabled,
        onClick = onClick,
    ) {
        Text(label)
    }
}