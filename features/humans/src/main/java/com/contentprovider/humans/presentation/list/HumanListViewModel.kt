package com.contentprovider.humans.presentation.list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.contentprovider.core.common.Container
import com.contentprovider.core.presentation.flow.RestartableStateFlow
import com.contentprovider.core.presentation.flow.restartableStateIn
import com.contentprovider.humans.domain.entities.Human
import com.contentprovider.humans.domain.repositories.HumanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.cache
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HumanListViewModel @Inject constructor(
    private val humanRepository: HumanRepository,
) : ViewModel() {

    val humanListFlow: RestartableStateFlow<Container<List<Human>>> =
        humanRepository.observeAll(true)
            .restartableStateIn(
                viewModelScope,
                SharingStarted.Lazily,
                Container.Pending,
            )

    val isRefreshing = humanListFlow.map {
        it is Container.Pending
    }
}