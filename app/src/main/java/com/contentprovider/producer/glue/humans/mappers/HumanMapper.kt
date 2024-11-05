package com.contentprovider.producer.glue.humans.mappers

import com.contentprovider.data.models.HumanModel
import com.contentprovider.humans.domain.entities.Human
import javax.inject.Inject

class HumanMapper @Inject constructor() {

    fun toHumanModel(entity: Human): HumanModel {
        return HumanModel(
            id = entity.id,
            name = entity.name,
            surname = entity.surname,
            age = entity.age,
        )
    }

    fun toHuman(model: HumanModel): Human {
        return Human(
            id = model.id,
            name = model.name,
            surname = model.surname,
            age = model.age,
        )
    }

}