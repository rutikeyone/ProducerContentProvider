package com.contentprovider.core.presentation

class Event<T>(
    value: T
) {
    private var _value: T? = value
    fun get(): T? = _value.apply { _value = null }
}