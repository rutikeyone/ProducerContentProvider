package com.contentprovider.humans.presentation.update.viewmodel

import androidx.annotation.StringRes

sealed class HumanUpdateUiAction {
    data object HideFocus : HumanUpdateUiAction()
    data object NavigateBack : HumanUpdateUiAction()
    data class ShowSnackBar(@StringRes val value: Int) : HumanUpdateUiAction()
}