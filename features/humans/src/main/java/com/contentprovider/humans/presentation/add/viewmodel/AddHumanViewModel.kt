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
        when (event) {
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

            humanRepository.insert(
                newName = name,
                newSurname = surname,
                newAge = age,
            )
            handleSaveResult()
        }
    }

    private fun handleSaveResult() {
        val uiState = _uiState.value

        val successUiState = uiState.copy(
            showSnackBarEvent = Event(R.string.data_has_been_successfully_added),
            navigateBackEvent = Event(Unit)
        )
        _uiState.tryEmit(successUiState)
    }

    private fun validate(name: String, surname: String, age: String): Boolean {
        val ageIntOrNull = age.toIntOrNull()
        val nameIsNotEmpty = name.isNotEmpty()
        val surnameIsNotEmpty = surname.isNotEmpty()
        val ageNotNull = ageIntOrNull != null

        return nameIsNotEmpty && surnameIsNotEmpty && ageNotNull
    }

}
