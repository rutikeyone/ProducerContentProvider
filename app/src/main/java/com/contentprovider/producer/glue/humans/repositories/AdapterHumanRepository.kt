package com.contentprovider.producer.glue.humans.repositories

import android.net.Uri
import com.contentprovider.core.common.Container
import com.contentprovider.data.HumanDataRepository
import com.contentprovider.humans.domain.entities.Human
import com.contentprovider.humans.domain.repositories.HumanRepository
import com.contentprovider.producer.glue.humans.mappers.HumanMapper
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AdapterHumanRepository @Inject constructor(
    private val humanMapper: HumanMapper,
    private val humanDataRepository: HumanDataRepository,
) : HumanRepository {

    override fun observeAll(silently: Boolean): Flow<Container<List<Human>>> {
        return humanDataRepository.observeAll(silently).map { data ->
            return@map data.map { item ->
                item.map { humanMapper.toHuman(it) }
            }
        }
    }

    override suspend fun insert(model: Human): Uri {
        val humanModel = humanMapper.toHumanModel(model)
        return humanDataRepository.insert(humanModel)
    }

}