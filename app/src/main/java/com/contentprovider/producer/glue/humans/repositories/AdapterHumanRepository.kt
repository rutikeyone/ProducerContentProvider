package com.contentprovider.producer.glue.humans.repositories

import android.net.Uri
import com.contentprovider.core.common.Container
import com.contentprovider.data.HumanDataRepository
import com.contentprovider.humans.domain.entities.Human
import com.contentprovider.humans.domain.repositories.HumanRepository
import com.contentprovider.producer.glue.humans.mappers.HumanMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AdapterHumanRepository @Inject constructor(
    private val humanMapper: HumanMapper,
    private val humanDataRepository: HumanDataRepository,
) : HumanRepository {

    override fun observeAll(silently: Boolean): Flow<Container<List<Human>>> {
        return humanDataRepository.observeHumans(silently).map { data ->
            return@map data.map { item ->
                item.map { humanMapper.toHuman(it) }
            }
        }
    }

    override suspend fun insert(
        newName: String,
        newSurname: String,
        newAge: String,
    ): Uri {
        return humanDataRepository.insertHuman(
            newName = newName,
            newSurname = newSurname,
            newAge = newAge,
        )
    }

    override suspend fun getHuman(id: Long): Human {
        val humanModel = humanDataRepository.getHuman(id)
        return humanMapper.toHuman(humanModel)
    }

    override fun observeHuman(
        silently: Boolean,
        id: Long,
        requiredObserver: Boolean,
    ): Flow<Container<Human>> {
        return humanDataRepository.observeHuman(silently, id, requiredObserver).map { container ->
            container.map { model -> humanMapper.toHuman(model) }
        }
    }

    override suspend fun updateHuman(
        initialHuman: Human,
        newName: String,
        newSurname: String,
        newAge: String,
    ): Int {
        val humanModel = humanMapper.toHumanModel(initialHuman)

        return humanDataRepository.updateHuman(
            initialHuman = humanModel,
            newName = newName,
            newSurname = newSurname,
            newAge = newAge,
        )
    }

    override suspend fun deleteHuman(id: Long): Int {
        return humanDataRepository.deleteHuman(id)
    }

}