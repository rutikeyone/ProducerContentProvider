package com.consumer.content.impl

import com.consumer.content.common_impl.R
import com.contentprovider.core.common.CommonUi
import com.contentprovider.core.common.ErrorHandler
import com.contentprovider.core.common.InvalidAge
import com.contentprovider.core.common.InvalidName
import com.contentprovider.core.common.InvalidSurname
import com.contentprovider.core.common.Resources

class DefaultErrorHandler(
    private val resources: Resources,
    private val commonUi: CommonUi,
) : ErrorHandler {

    override fun handleError(exception: Throwable) {
        val resource = when (exception) {
            is InvalidName -> R.string.invalid_name
            is InvalidSurname -> R.string.invalid_surname
            is InvalidAge -> R.string.invalid_age
            else -> R.string.an_error_has_occurred
        }

        commonUi.toast(resource)
    }

    override fun getUserMessage(exception: Throwable): String {
        val resource = when (exception) {
            is InvalidName -> R.string.invalid_name
            is InvalidSurname -> R.string.invalid_surname
            is InvalidAge -> R.string.invalid_age
            else -> R.string.an_error_has_occurred
        }

        return resources.getString(resource)
    }
}