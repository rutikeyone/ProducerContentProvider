package com.contentprovider.humans.presentation.list.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.contentprovider.core.common.Container
import com.contentprovider.core.presentation.Event
import com.contentprovider.core.presentation.flow.RestartableStateFlow
import com.contentprovider.core.presentation.flow.restartableStateIn
import com.contentprovider.humans.R
import com.contentprovider.humans.domain.entities.Human
import com.contentprovider.humans.domain.repositories.HumanRepository
import com.contentprovider.humans.presentation.update.viewmodel.HumanUpdateUiAction
import com.contentprovider.humans.presentation.update.viewmodel.HumanUpdateUiState
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

    private val _uiActionFlow: MutableStateFlow<Event<out HumanListUiAction>?> = MutableStateFlow(null)
    val uiActionFlow = _uiActionFlow.asStateFlow()

    fun onEvent(uiEvent: HumanListUiEvent) {
        when (uiEvent) {
            is HumanListUiEvent.Delete -> {
                deleteHuman(uiEvent.human)
            }

            HumanListUiEvent.Restart -> humanListFlow.restart()
        }
    }

    private fun deleteHuman(human: Human) {
        viewModelScope.launch {
            val deleteResult = deleteData(human)
            handleResult(deleteResult)
        }
    }

    private fun handleResult(result: Result<Int>) {
        val isFailure = result.isFailure
        val exception = result.exceptionOrNull()

        if (isFailure && exception != null) {
            val uiAction = HumanListUiAction.ShowSnackBar(R.string.an_error_has_occurred)
            val uiEvent = Event(uiAction)

            _uiActionFlow.tryEmit(uiEvent)
        }
    }

    private suspend fun deleteData(human: Human): Result<Int> {
        try {
            val id = humanRepository.deleteHuman(human.id)
            return Result.success(id)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }


}