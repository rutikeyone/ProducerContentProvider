package com.contentprovider.humans.domain.repositories

import android.net.Uri
import com.contentprovider.core.common.Container
import com.contentprovider.humans.domain.entities.Human
import kotlinx.coroutines.flow.Flow

interface HumanRepository {

    fun observeAll(silently: Boolean = false): Flow<Container<List<Human>>>

    suspend fun insert(model: Human): Uri

}