package com.contentprovider.core.common

open class AppException(): RuntimeException()

class InvalidName(): AppException()

class InvalidSurname(): AppException()

class InvalidAge(): AppException()

class ActivityNotCreatedException(): AppException()