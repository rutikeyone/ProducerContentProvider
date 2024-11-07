package com.contentprovider.core.common

interface CoreProvider {
    val commonUi: CommonUi
    val errorHandler: ErrorHandler
    val resources: Resources
}