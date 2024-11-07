package com.contentprovider.humans.domain.repositories

import android.net.Uri
import com.contentprovider.core.common.Container
import com.contentprovider.humans.domain.entities.Human
import kotlinx.coroutines.flow.Flow

interface HumanRepository {

    fun observeAll(silently: Boolean = false): Flow<Container<List<Human>>>

    suspend fun insert(
        newName: String,
        newSurname: String,
        newAge: String,
    ): Uri

    suspend fun getHuman(id: Long): Human

    fun observeHuman(
        silently: Boolean,
        id: Long,
        requiredObserver: Boolean = true,
    ): Flow<Container<Human>>

    suspend fun updateHuman(
        initialHuman: Human,
        newName: String,
        newSurname: String,
        newAge: String,
    ): Int

    suspend fun deleteHuman(id: Long): Int

}