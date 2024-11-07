package com.contentprovider.core.common

object Core {

    private lateinit var coreProvider: CoreProvider

    val commonUi: CommonUi get() = coreProvider.commonUi

    val resources: Resources get() = coreProvider.resources

    val errorHandler get() = coreProvider.errorHandler

    fun init(coreProvider: CoreProvider) {
        this.coreProvider = coreProvider
    }

}