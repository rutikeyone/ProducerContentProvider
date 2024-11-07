package com.contentprovider.humans.presentation.update.viewmodel

sealed class HumanUpdateUiEvent {

    data class UpdateName(val value: String): HumanUpdateUiEvent()

    data class UpdateSurname(val value: String): HumanUpdateUiEvent()

    data class UpdateAge(val value: String): HumanUpdateUiEvent()

    data object Restart: HumanUpdateUiEvent()

    data object ObserveDetails: HumanUpdateUiEvent()

    data object Update: HumanUpdateUiEvent()

    data object Delete: HumanUpdateUiEvent()
}