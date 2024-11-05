package com.contentprovider.humans.presentation.add.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.contentprovider.core.presentation.Event
import com.contentprovider.humans.R
import com.contentprovider.humans.domain.entities.Human
import com.contentprovider.humans.domain.repositories.HumanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddHumanViewModel @Inject constructor(
    private val humanRepository: HumanRepository,
) : ViewModel() {

    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()

    private val _surname = MutableStateFlow("")
    val surname = _surname.asStateFlow()

    private val _age = MutableStateFlow("")
    val age = _age.asStateFlow()

    private val _uiState = MutableStateFlow(AddHumanUiState())
    val uiState = _uiState.asStateFlow()

    val validateState = combine(_name, _surname, _age) { name, surname, age ->
        return@combine validate(name, surname, age)
    }

    fun onEvent(event: AddHumanUiEvent) {
        when(event) {
            is AddHumanUiEvent.UpdateAge -> updateAge(event.value)
            is AddHumanUiEvent.UpdateName -> updateName(event.value)
            is AddHumanUiEvent.UpdateSurname -> updateSurname(event.value)
            AddHumanUiEvent.Save -> saveHuman()
        }
    }

    private fun updateName(value: String) {
        _name.tryEmit(value)
    }

    private fun updateSurname(value: String) {
        _surname.tryEmit(value)
    }

    private fun updateAge(value: String) {
        _age.tryEmit(value)
    }

    private fun saveHuman() {
        viewModelScope.launch {
            val name = _name.value;
            val surname = _surname.value;
            val age = _age.value
            val ageIntOrNull = age.toIntOrNull()

            val validateStatus = validate(name, surname, age)
            if (!validateStatus || ageIntOrNull == null) {
                return@launch
            }

            val human = Human(
                name = name,
                surname = surname,
                age = ageIntOrNull,
            )

            val insertResult = saveData(human)
            handleSaveResult(insertResult)
        }
    }

    private suspend fun saveData(human: Human): Result<Uri> {
        try {
            val uri = humanRepository.insert(human)
            return Result.success(uri)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    private fun handleSaveResult(result: Result<Uri>) {
        val isSuccess = result.isSuccess
        val isFailure = result.isFailure

        val uri = result.getOrNull()
        val exception = result.exceptionOrNull()

        val uiState = _uiState.value

        if (isSuccess && uri != null) {
            val successUiState = uiState.copy(
                showSnackBarEvent = Event(R.string.data_has_been_successfully_added),
                navigateBackEvent = Event(Unit)
            )

            _uiState.tryEmit(successUiState)

        } else if (isFailure && exception != null) {
            val failureUiState = uiState.copy(
                showSnackBarEvent = Event(R.string.an_error_has_occurred),
            )

            _uiState.value = failureUiState
        }
    }

    private fun validate(name: String, surname: String, age: String): Boolean {
        val ageInt = age.toIntOrNull()

        return name.isNotEmpty() && surname.isNotEmpty() && ageInt != null
    }

}
