package com.contentprovider.humans.presentation.list.viewmodel

import com.contentprovider.humans.domain.entities.Human

sealed class HumanListUiEvent {
    data object Restart: HumanListUiEvent()
    data class Delete(val human: Human): HumanListUiEvent()
 }