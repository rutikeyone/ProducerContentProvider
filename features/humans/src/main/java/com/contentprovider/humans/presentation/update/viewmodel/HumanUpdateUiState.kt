package com.contentprovider.humans.presentation.update.viewmodel

import com.contentprovider.humans.domain.entities.Human

sealed class HumanUpdateUiState {
    data object Pure : HumanUpdateUiState()

    data object Pending : HumanUpdateUiState()

    data object Empty : HumanUpdateUiState()

    data class Data(
        val human: Human,
        val name: String = "",
        val surname: String = "",
        val age: String = "",
    ) : HumanUpdateUiState() {

        val validateStatue: Boolean
            get() {
                val ageLongOrNull = age.toLongOrNull()
                val hasDifferent =
                    name != human.name || surname != human.surname || age.toIntOrNull() != human.age;

                return name.isNotEmpty() && surname.isNotEmpty() && ageLongOrNull != null && hasDifferent
            }

    }

    data class Error(val exception: Exception) : HumanUpdateUiState()
}