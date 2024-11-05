package com.contentprovider.humans.domain.entities

data class Human(
    val id: Long = -1,
    val name: String,
    val surname: String,
    val age: Int,
)