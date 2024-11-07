package com.contentprovider.humans.presentation.update.viewmodel

import com.contentprovider.humans.domain.entities.Human

sealed class HumanUpdateUiState {

    open fun getOrNull(): Data? {
        return null
    }

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
                val nameDifferent = name != human.name
                val surnameDifferent = surname != human.surname
                val ageDifferent = age.toIntOrNull() != human.age

                val hasDifferent =
                    nameDifferent || surnameDifferent || ageDifferent

                val hasData = name.isNotEmpty() && surname.isNotEmpty() && ageLongOrNull != null

                return hasData && hasDifferent
            }

        override fun getOrNull(): Data {
            return this
        }

    }

    data class Error(val exception: Exception) : HumanUpdateUiState()
}