package com.contentprovider.humans.presentation.list

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import com.contentprovider.core.common.Container
import com.contentprovider.core.presentation.views.PullToRefreshBox
import com.contentprovider.humans.R
import com.contentprovider.humans.domain.entities.Human
import com.contentprovider.humans.presentation.list.viewmodel.HumanListViewModel
import com.contentprovider.humans.presentation.list.views.HumanDataListView
import com.contentprovider.humans.presentation.list.views.HumanEmptyView
import com.contentprovider.core.presentation.views.AppErrorView
import com.contentprovider.humans.presentation.list.viewmodel.HumanListUiAction
import com.contentprovider.humans.presentation.list.viewmodel.HumanListUiEvent
import com.contentprovider.humans.presentation.update.viewmodel.HumanUpdateUiAction


@Composable
fun HumanListPage(
    viewModel: HumanListViewModel = hiltViewModel(),
    navigateToAddHuman: () -> Unit,
    onClickItem: (Human) -> Unit,
) {
    val uiState = viewModel.humanListFlow.collectAsStateWithLifecycle(Container.Pure)
    val uiActionState = viewModel.uiActionFlow.collectAsStateWithLifecycle()
    val isRefreshing = viewModel.isRefreshing.collectAsStateWithLifecycle(false)

    val snackBarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current

    LaunchedEffect(key1 = uiActionState.value) {
        val showSnackBarEvent = uiActionState.value?.get()

        showSnackBarEvent?.let {
            when (it) {
                is HumanListUiAction.ShowSnackBar -> {
                    val resource = context.getString(it.value)
                    snackBarHostState.showSnackbar(resource)
                }
            }
        }
    }

    HumanListView(
        uiState = uiState,
        isRefreshing = isRefreshing,
        onEvent = { viewModel.onEvent(it) },
        navigateToAddHuman = navigateToAddHuman,
        onClickItem = onClickItem,
        snackBarHostState = snackBarHostState,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalWearMaterialApi::class)
@Composable
fun HumanListView(
    uiState: State<Container<List<Human>>>,
    isRefreshing: State<Boolean>,
    onEvent: (HumanListUiEvent) -> Unit,
    navigateToAddHuman: () -> Unit,
    onClickItem: (Human) -> Unit,
    snackBarHostState: SnackbarHostState,
) {
    Scaffold(snackbarHost = { SnackbarHost(snackBarHostState) }, topBar = {
        TopAppBar(
            colors = topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.primary,
            ),
            title = {
                Text(stringResource(R.string.human_list_view))
            },
        )
    }, floatingActionButton = {
        FloatingActionButton(onClick = navigateToAddHuman) {
            Icon(Icons.Filled.Add, null)
        }
    }) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing.value,
            onRefresh = {
                onEvent(HumanListUiEvent.Restart)
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            when (val state = uiState.value) {
                is Container.Data -> HumanDataListView(
                    data = state.data,
                    onClickItem = onClickItem,
                    onDeleteItem = { onEvent(HumanListUiEvent.Delete(it)) }
                )

                is Container.Error -> AppErrorView(
                    onTryAgain = { onEvent(HumanListUiEvent.Restart) }
                )

                Container.Empty -> HumanEmptyView()

                Container.Pending -> {}

                Container.Pure -> {}
            }
        }
    }
}


@Composable
@Preview(showBackground = true)
fun HumanListPreview() {
    val humans = listOf(
        Human(name = "Test", surname = "Test", age = 1)
    )

    val pendingContainer = Container.Pending
    val errorContainer = Container.Error(error = Exception())
    val emptyContainer = Container.Empty
    val dataContainer = Container.Data(humans)

    val uiState = remember {
        mutableStateOf(dataContainer)
    }

    val isRefreshing = remember {
        mutableStateOf(true)
    }

    val snackBarHostState = remember { SnackbarHostState() }

    HumanListView(
        uiState = uiState,
        onEvent = {},
        navigateToAddHuman = {},
        isRefreshing = isRefreshing,
        onClickItem = {},
        snackBarHostState = snackBarHostState,
    )
}