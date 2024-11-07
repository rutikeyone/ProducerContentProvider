package com.contentprovider.humans.presentation.update.viewmodel

sealed class HumanUpdateUiAction {
    data object HideFocus : HumanUpdateUiAction()
    data object NavigateBack : HumanUpdateUiAction()

    companion object {
        fun hideFocus() = HideFocus
        fun navigateBack() = NavigateBack
    }
}
