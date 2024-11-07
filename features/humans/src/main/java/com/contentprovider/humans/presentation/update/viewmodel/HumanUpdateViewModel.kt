package com.contentprovider.humans.presentation.update.viewmodel

import com.contentprovider.core.common.Container
import com.contentprovider.core.presentation.BaseViewModel
import com.contentprovider.core.presentation.Event
import com.contentprovider.core.presentation.flow.restartableStateIn
import com.contentprovider.humans.domain.entities.Human
import com.contentprovider.humans.domain.repositories.HumanRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val silently = false
private const val requiredObserver = false

class HumanUpdateViewModel @AssistedInject constructor(
    @Assisted private val id: Long,
    private val humanRepository: HumanRepository,
) : BaseViewModel() {

    private val humanDetailsFlow =
        humanRepository.observeHuman(silently, id, requiredObserver)
            .restartableStateIn(
                viewModelScope,
                SharingStarted.Lazily,
                Container.Pending,
            )

    private val _uiState: MutableStateFlow<HumanUpdateUiState> =
        MutableStateFlow(HumanUpdateUiState.Pending)
    val uiState = _uiState.asStateFlow()

    private val _uiActionFlow: MutableStateFlow<Event<out HumanUpdateUiAction>?> =
        MutableStateFlow(null)
    val uiActionFlow = _uiActionFlow.asStateFlow()

    val refreshState = humanDetailsFlow.map { it is Container.Pending }

    private fun observeDetails() {
        viewModelScope.launch {
            humanDetailsFlow.collect { container ->
                _uiState.update {
                    val state = mapContainerToUiState(container)
                    state
                }
            }
        }
    }

    fun obtainEvent(humanUpdateUiEvent: HumanUpdateUiEvent) {
        when (humanUpdateUiEvent) {
            is HumanUpdateUiEvent.UpdateAge -> updateAge(humanUpdateUiEvent.value)
            is HumanUpdateUiEvent.UpdateName -> updateName(humanUpdateUiEvent.value)
            is HumanUpdateUiEvent.UpdateSurname -> updateSurname(humanUpdateUiEvent.value)
            HumanUpdateUiEvent.ObserveDetails -> observeDetails()
            HumanUpdateUiEvent.Restart -> restart()
            HumanUpdateUiEvent.Update -> updateHuman()
            HumanUpdateUiEvent.Delete -> deleteHuman()
        }
    }

    private fun updateName(name: String) {
        uiState.value.getOrNull()?.let {
            val newState = it.copy(name = name)
            _uiState.tryEmit(newState)
        }
    }

    private fun updateSurname(surname: String) {
        uiState.value.getOrNull()?.let {
            val newState = it.copy(surname = surname)
            _uiState.tryEmit(newState)
        }
    }

    private fun updateAge(age: String) {
        uiState.value.getOrNull()?.let {
            val newState = it.copy(age = age)
            _uiState.tryEmit(newState)
        }
    }

    private fun restart() {
        humanDetailsFlow.restart()
    }

    private fun updateHuman() {
        val state = uiState.value.getOrNull() ?: return

        viewModelScope.launch {
            hideFocus()

            val id = humanRepository.updateHuman(
                initialHuman = state.human,
                newName = state.name,
                newSurname = state.surname,
                newAge = state.age,
            )
            handleResult(id)
        }
    }

    private fun handleResult(id: Int) {
        uiState.value.getOrNull().let {
            val uiAction = HumanUpdateUiAction.navigateBack()
            val uiEvent = Event(uiAction)

            _uiActionFlow.tryEmit(uiEvent)
        }
    }

    private fun deleteHuman() {
        val state = uiState.value.getOrNull() ?: return

        viewModelScope.launch {
            val id = state.human.id
            val deletedId = humanRepository.deleteHuman(id)

            handleResult(deletedId)
        }
    }

    private fun mapContainerToUiState(container: Container<Human>): HumanUpdateUiState {
        return when (container) {
            Container.Pure -> HumanUpdateUiState.Pure
            Container.Empty -> HumanUpdateUiState.Empty
            Container.Pending -> HumanUpdateUiState.Pending
            is Container.Data -> HumanUpdateUiState.Data(
                human = container.data,
                name = container.data.name,
                surname = container.data.surname,
                age = container.data.age.toString(),
            )

            is Container.Error -> HumanUpdateUiState.Error(container.error)
        }
    }

    private fun hideFocus() {
        val uiAction = HumanUpdateUiAction.hideFocus()
        val uiEvent = Event(uiAction)

        _uiActionFlow.tryEmit(uiEvent)
    }

    @AssistedFactory
    interface Factory {
        fun create(id: Long): HumanUpdateViewModel
    }

    interface FactoryProvider {
        fun provideViewModelFactory(): Factory
    }
}