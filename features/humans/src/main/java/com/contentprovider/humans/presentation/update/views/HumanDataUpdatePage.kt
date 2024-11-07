package com.contentprovider.humans.presentation.update.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.contentprovider.core.presentation.views.AppElevatedButton
import com.contentprovider.core.presentation.views.AppTextField
import com.contentprovider.humans.R
import com.contentprovider.humans.presentation.update.viewmodel.HumanUpdateUiState
import com.contentprovider.humans.presentation.update.viewmodel.HumanUpdateUiEvent

@Composable
fun HumanDataUpdateView(
    state: HumanUpdateUiState.Data,
    onEvent: (HumanUpdateUiEvent) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppTextField(
            modifier = Modifier
                .width(240.dp)
                .padding(top = 16.dp),
            label = stringResource(id = R.string.name),
            value = state.name,
            onValueChanged = { onEvent(HumanUpdateUiEvent.UpdateName(it)) },
            imeAction = ImeAction.Next,
        )
        AppTextField(
            modifier = Modifier
                .width(240.dp)
                .padding(top = 16.dp),
            label = stringResource(id = R.string.surname),
            value = state.surname,
            onValueChanged = { onEvent(HumanUpdateUiEvent.UpdateSurname(it)) },
            imeAction = ImeAction.Next,
        )
        AppTextField(
            modifier = Modifier
                .width(240.dp)
                .padding(top = 16.dp),
            label = stringResource(id = R.string.age),
            value = state.age,
            onValueChanged = { onEvent(HumanUpdateUiEvent.UpdateAge(it)) },
        )
        AppElevatedButton(
            modifier = Modifier
                .width(240.dp)
                .padding(top = 16.dp),
            label = stringResource(id = R.string.update),
            enabled = state.validateStatue,
            onClick = { onEvent(HumanUpdateUiEvent.Update) }
        )
        AppElevatedButton(
            modifier = Modifier
                .width(240.dp)
                .padding(top = 8.dp),
            label = stringResource(R.string.delete),
            onClick = { onEvent(HumanUpdateUiEvent.Delete) }
        )
    }
}