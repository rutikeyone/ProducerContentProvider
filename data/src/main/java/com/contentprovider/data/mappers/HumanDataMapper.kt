package com.contentprovider.data.mappers

import android.content.ContentValues
import com.contentprovider.data.db.HumanContract
import com.contentprovider.data.models.HumanModel

class HumanDataMapper {

    fun toHumanDb(model: HumanModel) : ContentValues {
        val name = model.name
        val surname = model.surname
        val age = model.age

        val values = ContentValues().apply {
            put(HumanContract.Entry.COLUMN_NAME_TITLE, name)
            put(HumanContract.Entry.COLUMN_SURNAME_TITLE, surname)
            put(HumanContract.Entry.COLUMN_AGE_TITLE, age)
        }

        return values;
    }

}