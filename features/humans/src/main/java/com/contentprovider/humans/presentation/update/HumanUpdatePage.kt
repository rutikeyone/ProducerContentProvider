package com.contentprovider.humans.presentation.update

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.contentprovider.core.common.Container
import com.contentprovider.core.presentation.views.PullToRefreshBox
import com.contentprovider.humans.R
import com.contentprovider.humans.domain.entities.Human
import com.contentprovider.humans.presentation.update.viewmodel.HumanUpdateViewModel
import com.contentprovider.humans.presentation.update.viewmodel.UiState

@Composable
fun HumanUpdatePage(
    viewModel: HumanUpdateViewModel,
) {
    val uiState =
        viewModel.humanDetailsFlow
            .collectAsStateWithLifecycle(initialValue = UiState.Pending)

    val isRefreshingState = viewModel.isRefreshingState
        .collectAsStateWithLifecycle(false)

    HumanUpdateView(
        uiState = uiState,
        isRefreshingState = isRefreshingState,
        onRefresh = {},
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun HumanUpdateView(
    uiState: State<UiState>,
    isRefreshingState: State<Boolean>,
    onRefresh: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(colors = topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.primary,
            ), title = {
                Text(stringResource(R.string.update))
            })
        },
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = isRefreshingState.value,
            onRefresh = {},
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            Text(uiState.value.toString())
//            when (uiState.value) {
//                UiState.Pure -> {}
//                UiState.Pending -> {}
//                UiState.Empty -> {}
//                is UiState.Data -> {}
//                is UiState.Error -> {}
//            }
        }
    }
}