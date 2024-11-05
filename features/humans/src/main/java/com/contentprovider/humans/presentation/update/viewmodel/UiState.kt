package com.contentprovider.humans.presentation.update.viewmodel

import com.contentprovider.humans.domain.entities.Human

sealed class UiState {
    data object Pure : UiState()

    data object Pending : UiState()

    data object Empty : UiState()

    data class Data(
        val human: Human,
    ) : UiState()

    data class Error(val exception: Exception) : UiState()
}