package com.contentprovider.data.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri
import android.provider.BaseColumns
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class HumanDbHelper @Inject constructor(
    @ApplicationContext private val context: Context,
) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {

        private const val SQL_CREATE_ENTRIES = "CREATE TABLE ${HumanContract.Entry.TABLE_NAME} (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "${HumanContract.Entry.COLUMN_NAME_TITLE} TEXT NOT NULL, " +
                "${HumanContract.Entry.COLUMN_SURNAME_TITLE} TEXT NOT NULL, " +
                "${HumanContract.Entry.COLUMN_AGE_TITLE} INTEGER NOT NULL)"

        private const val SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS ${HumanContract.Entry.TABLE_NAME}"

        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "human.db"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    fun insert(
        tableName: String,
        nullColumnHack: String?,
        values: ContentValues?,
    ): Long {
        return writableDatabase.insert(
            tableName,
            nullColumnHack,
            values
        )
    }

    fun update(
        tableName: String,
        values: ContentValues?,
        whereClause: String?,
        whereArgs: Array<String>?,
    ): Int {
        return writableDatabase.update(
            tableName,
            values,
            whereClause,
            whereArgs
        )
    }

    fun delete(
        tableName: String,
        whereClause: String?,
        whereArgs: Array<out String>?
    ): Int {
        return writableDatabase.delete(
            tableName,
            whereClause,
            whereArgs
        )
    }

    fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        return readableDatabase.query(
            HumanContract.Entry.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            sortOrder,
        )
    }
}