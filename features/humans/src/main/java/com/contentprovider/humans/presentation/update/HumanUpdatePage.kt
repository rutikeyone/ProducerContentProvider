package com.contentprovider.humans.presentation.update

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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.contentprovider.core.presentation.views.AppErrorView
import com.contentprovider.core.presentation.views.PullToRefreshBox
import com.contentprovider.humans.R
import com.contentprovider.humans.presentation.update.viewmodel.HumanUpdateUiAction
import com.contentprovider.humans.presentation.update.viewmodel.HumanUpdateViewModel
import com.contentprovider.humans.presentation.update.viewmodel.HumanUpdateUiState
import com.contentprovider.humans.presentation.update.viewmodel.HumanUpdateUiEvent
import com.contentprovider.humans.presentation.update.views.HumanDataUpdateView

@Composable
fun HumanUpdatePage(
    viewModel: HumanUpdateViewModel,
    onNavigateBack: () -> Unit,
) {
    val focusManager = LocalFocusManager.current

    val uiState = viewModel.uiState.collectAsStateWithLifecycle(HumanUpdateUiState.Pending)
    val refreshingState = viewModel.refreshState.collectAsStateWithLifecycle(false)
    val uiActionState = viewModel.uiActionFlow.collectAsStateWithLifecycle()

    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.obtainEvent(HumanUpdateUiEvent.ObserveDetails)
    }

    LaunchedEffect(key1 = uiActionState.value) {
        uiActionState.value?.get()?.let {
            when (it) {
                HumanUpdateUiAction.HideFocus -> focusManager.clearFocus(true)
                HumanUpdateUiAction.NavigateBack -> onNavigateBack()
            }
        }
    }

    HumanUpdateView(
        humanUpdateUiState = uiState,
        isRefreshingState = refreshingState,
        onEvent = { viewModel.obtainEvent(it) },
        snackBarHostState = snackBarHostState,
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun HumanUpdateView(
    humanUpdateUiState: State<HumanUpdateUiState>,
    isRefreshingState: State<Boolean>,
    onEvent: (HumanUpdateUiEvent) -> Unit,
    snackBarHostState: SnackbarHostState,
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(stringResource(R.string.update))
                },
            )
        },
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = isRefreshingState.value,
            onRefresh = { onEvent(HumanUpdateUiEvent.Restart) },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            when (val state = humanUpdateUiState.value) {
                HumanUpdateUiState.Pure -> {}
                HumanUpdateUiState.Pending -> {}
                HumanUpdateUiState.Empty -> {}
                is HumanUpdateUiState.Data -> HumanDataUpdateView(
                    state = state,
                    onEvent = onEvent,
                )

                is HumanUpdateUiState.Error -> AppErrorView(
                    onTryAgain = { onEvent(HumanUpdateUiEvent.Restart) },
                )
            }
        }
    }
}
