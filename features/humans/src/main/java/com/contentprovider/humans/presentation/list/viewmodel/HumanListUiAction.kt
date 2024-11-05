package com.contentprovider.humans.presentation.list.viewmodel

import androidx.annotation.StringRes

sealed class HumanListUiAction {
    data class ShowSnackBar(@StringRes val value: Int) : HumanListUiAction()

}