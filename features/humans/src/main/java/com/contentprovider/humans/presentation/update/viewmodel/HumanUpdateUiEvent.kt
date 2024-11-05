package com.contentprovider.humans.presentation.update.viewmodel

sealed class HumanUpdateUiEvent {
    data class Name(val value: String) : HumanUpdateUiEvent()
    data class Surname(val value: String) : HumanUpdateUiEvent()
    data class Age(val value: String) : HumanUpdateUiEvent()
    data object Restart : HumanUpdateUiEvent()
    data object Update: HumanUpdateUiEvent()
    data object Delete: HumanUpdateUiEvent()
}