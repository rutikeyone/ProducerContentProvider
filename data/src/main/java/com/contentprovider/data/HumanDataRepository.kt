package com.contentprovider.data

import android.net.Uri
import com.contentprovider.core.common.Container
import com.contentprovider.data.models.HumanModel
import kotlinx.coroutines.flow.Flow

interface HumanDataRepository {

    suspend fun insertHuman(
        newName: String,
        newSurname: String,
        newAge: String,
    ): Uri

    suspend fun updateHuman(
        initialHuman: HumanModel,
        newName: String,
        newSurname: String,
        newAge: String,
    ): Int

    suspend fun deleteHuman(id: Long): Int

    suspend fun getHumans(): List<HumanModel>

    suspend fun getHuman(id: Long): HumanModel

    fun observeHumans(silently: Boolean = false): Flow<Container<List<HumanModel>>>

    fun observeHuman(
        silently: Boolean, id: Long,
        requiredObserver: Boolean = true,
    ): Flow<Container<HumanModel>>
}