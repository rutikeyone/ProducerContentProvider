package com.contentprovider.humans.presentation.add

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.contentprovider.humans.R
import com.contentprovider.humans.presentation.add.viewmodel.AddHumanViewModel
import com.contentprovider.humans.presentation.add.widgets.AgeTextField
import com.contentprovider.humans.presentation.add.widgets.ElevatedSaveButton
import com.contentprovider.humans.presentation.add.widgets.NameTextField
import com.contentprovider.humans.presentation.add.widgets.SurnameTextField

@Composable
fun AddHumanPage(
    viewModel: AddHumanViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
) {
    val nameUiState = viewModel.name.collectAsStateWithLifecycle("")
    val surnameUiState = viewModel.surname.collectAsStateWithLifecycle("")
    val ageUiState = viewModel.age.collectAsStateWithLifecycle("")
    val isValidateState = viewModel.isValidate.collectAsStateWithLifecycle(false)
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
        onNameChanged = { viewModel.updateName(it) },
        surnameUiState = surnameUiState,
        onSurnameChanged = { viewModel.updateSurname(it) },
        ageUiState = ageUiState,
        onAgeChanged = { viewModel.updateAge(it) },
        isValidate = isValidateState,
        onSavePressed = { viewModel.save() },
        hostState = snackBarHostState,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHumanView(
    nameUiState: State<String>,
    onNameChanged: (String) -> Unit,
    surnameUiState: State<String>,
    onSurnameChanged: (String) -> Unit,
    ageUiState: State<String>,
    onAgeChanged: (String) -> Unit,
    isValidate: State<Boolean>,
    onSavePressed: () -> Unit,
    hostState: SnackbarHostState,
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
            NameTextField(
                nameState = nameUiState,
                onValueChanged = onNameChanged,
            )
            SurnameTextField(
                surnameState = surnameUiState,
                onValueChanged = onSurnameChanged,
            )
            AgeTextField(
                ageState = ageUiState,
                onValueChanged = onAgeChanged,
            )
            ElevatedSaveButton(
                enabled = isValidate,
                onClick = onSavePressed,
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
        onNameChanged = {},
        surnameUiState = surnameUiState,
        onSurnameChanged = {},
        ageUiState = ageUiState,
        onAgeChanged = {},
        isValidate = isValidateState,
        onSavePressed = {},
        hostState = snackBarHostState,
    )
}