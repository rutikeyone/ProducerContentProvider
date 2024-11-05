package com.contentprovider.data.db

import android.provider.BaseColumns

object HumanContract {

    object Entry : BaseColumns {
        const val TABLE_NAME = "humans"
        const val COLUMN_NAME_TITLE = "name"
        const val COLUMN_SURNAME_TITLE = "surname"
        const val COLUMN_AGE_TITLE = "age"
    }

}