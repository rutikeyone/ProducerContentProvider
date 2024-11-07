package com.contentprovider.core.common

import kotlinx.coroutines.runBlocking

sealed class Container<out T> {

    fun <R> map(mapper: ((T) -> R)? = null): Container<R> {
        return runBlocking {
            val suspendMapper: (suspend (T) -> R)? = if (mapper == null) {
                null
            } else {
                {
                    mapper(it)
                }
            }
            suspendMap(suspendMapper)
        }
    }

    abstract suspend fun <R> suspendMap(mapper: (suspend (T) -> R)? = null): Container<R>

    abstract fun unwrap(): T

    abstract fun getOrNull(): T?

    data object Pure : Container<Nothing>() {
        override suspend fun <R> suspendMap(mapper: (suspend (Nothing) -> R)?): Container<R> {
            return this
        }

        override fun unwrap(): Nothing {
            throw IllegalStateException("Container is Pure")
        }

        override fun getOrNull(): Nothing? {
            return null
        }
    }

    data object Empty : Container<Nothing>() {
        override suspend fun <R> suspendMap(mapper: (suspend (Nothing) -> R)?): Container<R> {
            return this
        }

        override fun unwrap(): Nothing {
            throw IllegalStateException("Container is Empty")
        }

        override fun getOrNull(): Nothing? {
            return null
        }
    }

    data object Pending : Container<Nothing>() {
        override suspend fun <R> suspendMap(mapper: (suspend (Nothing) -> R)?): Container<R> {
            return this
        }

        override fun unwrap(): Nothing {
            throw IllegalStateException("Container is Pending")
        }

        override fun getOrNull(): Nothing? {
            return null
        }
    }

    data class Data<T>(val data: T) : Container<T>() {
        override suspend fun <R> suspendMap(mapper: (suspend (T) -> R)?): Container<R> {
            if (mapper == null) {
                throw IllegalStateException("Can't map Container.Success without mapper")
            }

            return try {
                Data(mapper(data))
            } catch (e: Exception) {
                Error(e)
            }
        }

        override fun unwrap(): T {
            return data
        }

        override fun getOrNull(): T? {
            return data
        }
    }

    data class Error(val error: Exception) : Container<Nothing>() {
        override suspend fun <R> suspendMap(mapper: (suspend (Nothing) -> R)?): Container<R> {
            return this
        }

        override fun unwrap(): Nothing {
            throw error
        }

        override fun getOrNull(): Nothing? {
            return null
        }
    }
}