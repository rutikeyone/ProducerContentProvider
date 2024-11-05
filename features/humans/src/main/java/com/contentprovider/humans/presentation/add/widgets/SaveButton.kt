package com.contentprovider.humans.presentation.add.widgets

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.contentprovider.humans.R

@Composable
fun ElevatedSaveButton(
    enabled: State<Boolean>,
    onClick: () -> Unit,
) {
    ElevatedButton(
        modifier = Modifier
            .padding(top = 12.dp)
            .width(240.dp),
        enabled = enabled.value,
        onClick = onClick,
    ) {
        Text(stringResource(R.string.save))
    }
}