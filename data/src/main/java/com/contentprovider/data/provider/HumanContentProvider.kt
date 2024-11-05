package com.contentprovider.data.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import android.provider.BaseColumns
import android.text.TextUtils
import com.contentprovider.data.db.HumanContract
import com.contentprovider.data.db.HumanDbHelper
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent


class HumanContentProvider : ContentProvider() {

    companion object {

        private const val AUTHORITY: String = "com.producer.human.provider"
        private const val PATH_CONTACTS: String = "humans"

        val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/$PATH_CONTACTS")

        private const val DIR_CODE = 1
        private const val ID_CODE = 2

        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, "humans", DIR_CODE)
            addURI(AUTHORITY, "humans/#", ID_CODE)
        }

        private val HUMANS_PROJECTION_MAP: HashMap<String, String>? = null

    }

    private lateinit var dbHelper: HumanDbHelper

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface HumanContentProviderEntryPoint {
        fun humanDbHelper(): HumanDbHelper
    }


    override fun onCreate(): Boolean {
        val appContext = context?.applicationContext ?: throw IllegalStateException()
        val hiltEntryPoint =
            EntryPointAccessors.fromApplication(
                appContext,
                HumanContentProviderEntryPoint::class.java
            )

        dbHelper = hiltEntryPoint.humanDbHelper()
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        val qb = SQLiteQueryBuilder().apply {
            tables = HumanContract.Entry.TABLE_NAME
        }

        when (uriMatcher.match(uri)) {
            DIR_CODE -> qb.projectionMap = HUMANS_PROJECTION_MAP
            ID_CODE -> qb.appendWhere(BaseColumns._ID + "=" + uri.pathSegments[1])
        }

        val cursor = dbHelper.query(
            uri,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )

        cursor?.setNotificationUri(context?.contentResolver, uri)
        return cursor
    }

    override fun getType(uri: Uri): String {
        return when (uriMatcher.match(uri)) {
            DIR_CODE -> "vnd.android.cursor.dir/vnd.example.humans"
            ID_CODE -> "vnd.android.cursor.item/vnd.example.humans"
            else -> throw IllegalArgumentException("Unsupported URI: $uri")

        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val id = dbHelper.insert(
            HumanContract.Entry.TABLE_NAME,
            null,
            values,
        )

        val path = Uri.withAppendedPath(CONTENT_URI, id.toString())
        context?.contentResolver?.notifyChange(path, null)

        return path
    }

    override fun delete(
        uri: Uri,
        selection: String?,
        selectionArgs: Array<out String>?,
    ): Int {
        val rowID = when (uriMatcher.match(uri)) {
            DIR_CODE -> dbHelper.delete(
                HumanContract.Entry.TABLE_NAME,
                selection,
                selectionArgs
            )

            ID_CODE -> {
                return dbHelper.delete(
                    HumanContract.Entry.TABLE_NAME,
                    "${BaseColumns._ID} = ?${if (!TextUtils.isEmpty(selection)) " AND ($selection)" else ""}",
                    selectionArgs,
                )
            }

            else -> throw IllegalArgumentException("Unsupported URI: $uri")
        }

        context?.contentResolver?.notifyChange(uri, null);
        return rowID
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int {


        val rowID = when (uriMatcher.match(uri)) {
            DIR_CODE -> dbHelper.update(
                HumanContract.Entry.TABLE_NAME,
                values,
                selection,
                selectionArgs
            )

            ID_CODE -> {
                return dbHelper.update(
                    HumanContract.Entry.TABLE_NAME,
                    values,
                    "${BaseColumns._ID} = ?${if (!TextUtils.isEmpty(selection)) " AND ($selection)" else ""}",
                    selectionArgs?.plus(
                        uri.pathSegments[1],
                    )
                )
            }

            else -> throw IllegalArgumentException("Unsupported URI: $uri")
        }

        context?.contentResolver?.notifyChange(uri, null);
        return rowID
    }
}