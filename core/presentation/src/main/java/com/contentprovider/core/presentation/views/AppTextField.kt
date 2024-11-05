package com.contentprovider.core.presentation.views

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction

@Composable
fun AppTextField(
    modifier: Modifier = Modifier,
    label: String,
    value: String? = null,
    state: State<String>? = null,
    onValueChanged: (String) -> Unit,
    imeAction: ImeAction = ImeAction.Done,
    readOnly: Boolean = false,
) {
    TextField(
        modifier = modifier,
        value = value ?: state?.value ?: "",
        onValueChange = onValueChanged,
        label = {
            Text(label)
        },
        readOnly = readOnly,
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = imeAction,
        ),
    )
}