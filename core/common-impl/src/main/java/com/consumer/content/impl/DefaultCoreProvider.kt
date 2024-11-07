package com.consumer.content.impl

import com.contentprovider.core.common.CommonUi
import com.contentprovider.core.common.CoreProvider
import com.contentprovider.core.common.ErrorHandler
import com.contentprovider.core.common.Resources

class DefaultCoreProvider(
    override val commonUi: CommonUi = AndroidCommonUi(),
    override val resources: Resources = AndroidResources(),
    override val errorHandler: ErrorHandler = DefaultErrorHandler(resources, commonUi)
) : CoreProvider