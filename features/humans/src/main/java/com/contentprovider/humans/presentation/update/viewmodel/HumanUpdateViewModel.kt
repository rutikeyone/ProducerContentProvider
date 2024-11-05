package com.contentprovider.humans.presentation.update.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.contentprovider.core.common.Container
import com.contentprovider.core.presentation.Event
import com.contentprovider.core.presentation.flow.restartableStateIn
import com.contentprovider.humans.R
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
) : ViewModel() {

    private val humanDetailsFlow =
        humanRepository.observeHuman(silently, id, requiredObserver).restartableStateIn(
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

    val refreshingState = humanDetailsFlow.map {
        it is Container.Pending
    }

    init {
        observeHumanDetails()
    }

    private fun observeHumanDetails() {
        viewModelScope.launch {
            humanDetailsFlow.collect { container ->
                _uiState.update {
                    val state = mapContainerToUiState(container)
                    state
                }
            }
        }
    }

    fun onEvent(event: HumanUpdateUiEvent) {
        when (event) {
            is HumanUpdateUiEvent.Age -> updateAge(event.value)
            is HumanUpdateUiEvent.Name -> updateName(event.value)
            is HumanUpdateUiEvent.Surname -> updateSurname(event.value)
            HumanUpdateUiEvent.Restart -> restart()
            HumanUpdateUiEvent.Update -> updateHuman()
            HumanUpdateUiEvent.Delete -> deleteHuman()
        }
    }

    private fun updateName(name: String) {
        val state = uiState.value
        if (state !is HumanUpdateUiState.Data) return

        val newState = state.copy(name = name)
        _uiState.tryEmit(newState)
    }

    private fun updateSurname(surname: String) {
        val state = uiState.value
        if (state !is HumanUpdateUiState.Data) return

        val newState = state.copy(surname = surname)
        _uiState.tryEmit(newState)
    }

    private fun updateAge(age: String) {
        val state = uiState.value
        if (state !is HumanUpdateUiState.Data) return

        val newState = state.copy(age = age)
        _uiState.tryEmit(newState)
    }

    private fun restart() {
        humanDetailsFlow.restart()
    }

    private fun updateHuman() {
        viewModelScope.launch {
            val state = uiState.value
            if (state !is HumanUpdateUiState.Data) return@launch

            val name = state.name
            val surname = state.surname
            val age = state.age
            val ageIntOrNull = age.toIntOrNull()

            val validateStatus = state.validateStatue
            if (!validateStatus || ageIntOrNull == null) {
                return@launch
            }

            val human = state.human.copy(
                name = name,
                surname = surname,
                age = ageIntOrNull,
            )

            val updateResult = updateData(human)
            handleResult(updateResult)
        }
    }

    private suspend fun updateData(human: Human): Result<Int> {
        try {
            val id = humanRepository.updateHuman(human)
            return Result.success(id)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    private fun handleResult(result: Result<Int>) {
        val state = uiState.value
        if (state !is HumanUpdateUiState.Data) return

        val isSuccess = result.isSuccess
        val isFailure = result.isFailure

        val id = result.getOrNull()
        val exception = result.exceptionOrNull()

        if (isSuccess && id != null) {
            val uiAction = HumanUpdateUiAction.NavigateBack
            val uiEvent = Event(uiAction)

            _uiActionFlow.tryEmit(uiEvent)
        } else if (isFailure && exception != null) {
            val uiAction = HumanUpdateUiAction.ShowSnackBar(R.string.an_error_has_occurred)
            val uiEvent = Event(uiAction)

            _uiActionFlow.tryEmit(uiEvent)
        }
    }

    private fun deleteHuman() {
        viewModelScope.launch {
            val state = uiState.value
            if (state !is HumanUpdateUiState.Data) return@launch

            val deleteResult = deleteData(state.human)
            handleResult(deleteResult)
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

    @AssistedFactory
    interface Factory {
        fun create(id: Long): HumanUpdateViewModel
    }

    interface FactoryProvider {
        fun provideViewModelFactory(): Factory
    }
}