package com.contentprovider.humans.presentation.add.viewmodel

sealed class AddHumanUiEvent {
    data class UpdateName(val value: String) : AddHumanUiEvent()
    data class UpdateSurname(val value: String) : AddHumanUiEvent()
    data class UpdateAge(val value: String) : AddHumanUiEvent()
    data object Save : AddHumanUiEvent()
}