package com.contentprovider.humans.presentation.add

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.contentprovider.humans.R
import com.contentprovider.humans.presentation.add.viewmodel.AddHumanViewModel
import com.contentprovider.core.presentation.views.AppTextField
import com.contentprovider.core.presentation.views.AppElevatedButton
import com.contentprovider.humans.presentation.add.viewmodel.AddHumanUiEvent

@Composable
fun AddHumanPage(
    viewModel: AddHumanViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
) {
    val nameUiState = viewModel.name.collectAsStateWithLifecycle("")
    val surnameUiState = viewModel.surname.collectAsStateWithLifecycle("")
    val ageUiState = viewModel.age.collectAsStateWithLifecycle("")
    val validateState = viewModel.validateState.collectAsStateWithLifecycle(false)
    val uiStateValue = viewModel.uiState.collectAsStateWithLifecycle().value

    val snackBarHostState = remember { SnackbarHostState() }

    val content = LocalContext.current

    LaunchedEffect(key1 = uiStateValue.showSnackBarEvent) {
        val showSnackBarEvent = uiStateValue.showSnackBarEvent?.get()

        showSnackBarEvent?.let {
            val resource = content.getString(it)
            snackBarHostState.showSnackbar(resource)
        }
    }

    LaunchedEffect(key1 = uiStateValue.navigateBackEvent) {
        val navigateBackEvent = uiStateValue.navigateBackEvent

        navigateBackEvent?.let {
            onNavigateBack()
        }
    }

    AddHumanView(
        nameUiState = nameUiState,
        surnameUiState = surnameUiState,
        ageUiState = ageUiState,
        validateState = validateState,
        hostState = snackBarHostState,
        onEvent = { viewModel.onEvent(it) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHumanView(
    nameUiState: State<String>,
    surnameUiState: State<String>,
    ageUiState: State<String>,
    validateState: State<Boolean>,
    hostState: SnackbarHostState,
    onEvent: (AddHumanUiEvent) -> Unit,
) {

    Scaffold(
        snackbarHost = { SnackbarHost(hostState) },
        topBar = {
            TopAppBar(colors = topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.primary,
            ), title = {
                Text(stringResource(R.string.add_human))
            })
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AppTextField(
                modifier = Modifier
                    .width(240.dp),
                label = stringResource(id = R.string.name),
                state = nameUiState,
                onValueChanged = { onEvent(AddHumanUiEvent.UpdateName(it)) },
                imeAction = ImeAction.Next,
            )
            AppTextField(
                modifier = Modifier
                    .width(240.dp)
                    .padding(top = 16.dp),
                label = stringResource(id = R.string.surname),
                state = surnameUiState,
                onValueChanged = { onEvent(AddHumanUiEvent.UpdateSurname(it)) },
                imeAction = ImeAction.Next,
            )
            AppTextField(
                modifier = Modifier
                    .width(240.dp)
                    .padding(top = 16.dp),
                label = stringResource(id = R.string.age),
                state = ageUiState,
                onValueChanged = { onEvent(AddHumanUiEvent.UpdateAge(it)) },
            )
            AppElevatedButton(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .width(240.dp),
                enabled = validateState.value,
                onClick = { onEvent(AddHumanUiEvent.Save) },
                label = stringResource(id = R.string.save)
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun AddHumanPreview() {
    val nameUiState = remember {
        mutableStateOf("")
    }

    val surnameUiState = remember {
        mutableStateOf("")
    }

    val ageUiState = remember {
        mutableStateOf("")
    }

    val isValidateState = remember {
        mutableStateOf(false)
    }

    val snackBarHostState = remember {
        SnackbarHostState()
    }

    AddHumanView(
        nameUiState = nameUiState,
        surnameUiState = surnameUiState,
        ageUiState = ageUiState,
        validateState = isValidateState,
        hostState = snackBarHostState,
        onEvent = {}
    )
}