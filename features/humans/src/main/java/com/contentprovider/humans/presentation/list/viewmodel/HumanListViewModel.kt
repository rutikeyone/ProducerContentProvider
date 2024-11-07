package com.contentprovider.humans.presentation.list.viewmodel

import com.contentprovider.core.common.Container
import com.contentprovider.core.presentation.BaseViewModel
import com.contentprovider.core.presentation.Event
import com.contentprovider.core.presentation.flow.RestartableStateFlow
import com.contentprovider.core.presentation.flow.restartableStateIn
import com.contentprovider.humans.domain.entities.Human
import com.contentprovider.humans.domain.repositories.HumanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HumanListViewModel @Inject constructor(
    private val humanRepository: HumanRepository,
) : BaseViewModel() {

    val humanListFlow: RestartableStateFlow<Container<List<Human>>> =
        humanRepository.observeAll(true)
            .restartableStateIn(
                viewModelScope,
                SharingStarted.Lazily,
                Container.Pending,
            )

    val isRefreshFlow = humanListFlow.map { it is Container.Pending }

    private val _uiActionFlow: MutableStateFlow<Event<out HumanListUiAction>?> =
        MutableStateFlow(null)
    val uiActionFlow = _uiActionFlow.asStateFlow()

    fun onEvent(uiEvent: HumanListUiEvent) {
        when (uiEvent) {
            is HumanListUiEvent.Delete -> deleteHuman(uiEvent.human)
            HumanListUiEvent.Restart -> humanListFlow.restart()
        }
    }

    private fun deleteHuman(human: Human) {
        viewModelScope.launch {
            val humanId = human.id
            humanRepository.deleteHuman(humanId)
        }
    }

}