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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.contentprovider.core.common.Container
import com.contentprovider.core.presentation.views.PullToRefreshBox
import com.contentprovider.humans.R
import com.contentprovider.humans.domain.entities.Human
import com.contentprovider.humans.presentation.list.viewmodel.HumanListViewModel
import com.contentprovider.humans.presentation.list.views.HumanDataListView
import com.contentprovider.humans.presentation.list.views.HumanEmptyView
import com.contentprovider.humans.presentation.list.views.HumanErrorView


@Composable
fun HumanListPage(
    viewModel: HumanListViewModel = hiltViewModel(),
    navigateToAddHuman: () -> Unit,
    onClickItem: (Human) -> Unit,

    ) {
    val uiState = viewModel.humanListFlow.collectAsStateWithLifecycle(Container.Pure)

    val isRefreshing = viewModel.isRefreshing.collectAsStateWithLifecycle(false)

    HumanListView(
        uiState = uiState,
        isRefreshing = isRefreshing,
        onTryAgain = { viewModel.humanListFlow.restart() },
        navigateToAddHuman = navigateToAddHuman,
        onClickItem = onClickItem,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HumanListView(
    uiState: State<Container<List<Human>>>,
    isRefreshing: State<Boolean>,
    onTryAgain: () -> Unit,
    navigateToAddHuman: () -> Unit,
    onClickItem: (Human) -> Unit,
) {
    Scaffold(topBar = {
        TopAppBar(colors = topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ), title = {
            Text(stringResource(R.string.human_list_view))
        })
    }, floatingActionButton = {
        FloatingActionButton(onClick = navigateToAddHuman) {
            Icon(Icons.Filled.Add, null)
        }
    }) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing.value,
            onRefresh = {
                onTryAgain()
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            when (val state = uiState.value) {
                is Container.Data -> HumanDataListView(
                    data = state.data,
                    onClickItem = onClickItem,
                )

                is Container.Error -> HumanErrorView(onTryAgain)

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

    HumanListView(
        uiState = uiState,
        onTryAgain = {},
        navigateToAddHuman = {},
        isRefreshing = isRefreshing,
        onClickItem = {},
    )
}