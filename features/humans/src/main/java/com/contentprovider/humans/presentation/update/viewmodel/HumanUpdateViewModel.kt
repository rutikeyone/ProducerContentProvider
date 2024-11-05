package com.contentprovider.humans.presentation.update.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.contentprovider.core.common.Container
import com.contentprovider.core.presentation.flow.restartableStateIn
import com.contentprovider.humans.domain.entities.Human
import com.contentprovider.humans.domain.repositories.HumanRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map

class HumanUpdateViewModel @AssistedInject constructor(
    @Assisted private val id: Long,
    private val humanRepository: HumanRepository,
) : ViewModel() {

    val humanDetailsFlow = humanRepository.observeById(
        silently = false,
        id = id,
    ).restartableStateIn(
        viewModelScope,
        SharingStarted.Lazily,
        Container.Pending,
    ).map(::mapContainerToUiState)

    private fun mapContainerToUiState(container: Container<Human>): UiState {
        return when (container) {
            Container.Pure -> UiState.Pure
            Container.Empty -> UiState.Empty
            Container.Pending -> UiState.Pending
            is Container.Data -> UiState.Data(container.data)
            is Container.Error -> UiState.Error(container.error)
        }
    }

    val isRefreshingState = humanDetailsFlow.map {
        it is UiState.Pending
    }

    @AssistedFactory
    interface Factory {
        fun create(id: Long): HumanUpdateViewModel
    }

    interface FactoryProvider {
        fun provideHumanUpdateViewModelFactory(): Factory
    }
}