package com.contentprovider.data.repositories

import android.content.ContentUris
import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.provider.BaseColumns
import com.contentprovider.core.common.Container
import com.contentprovider.data.HumanDataRepository
import com.contentprovider.data.db.HumanContract
import com.contentprovider.data.di.IODispatcher
import com.contentprovider.data.mappers.HumanDataMapper
import com.contentprovider.data.models.HumanModel
import com.contentprovider.data.provider.HumanContentProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

data class RequiredUpdate(
    val firstTime: Boolean,
    val mustUpdate: Boolean,
)

class HumanDataRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
    private val humanDataMapper: HumanDataMapper,
) : HumanDataRepository {

    override fun observeAll(silently: Boolean) = callbackFlow {
        requiredReadAll().collect { requiredUpdate ->
            if (requiredUpdate.mustUpdate) {
                if (!silently) {
                    trySend(Container.Pending)
                }

                val data = getAllWithResult()
                trySend(data)
            }
        }
    }

    override fun observe(silently: Boolean, id: Long): Flow<Container<HumanModel>> = callbackFlow {
        requiredRead(id).collect {
            if (!silently) {
                trySend(Container.Pending)
            }

            val data = getWithResult(id)
            trySend(data)
        }
    }

    override suspend fun insert(model: HumanModel): Uri {
        return with(ioDispatcher) {
            val values = humanDataMapper.toHumanDb(model)

            return@with context.contentResolver.insert(
                HumanContentProvider.CONTENT_URI,
                values,
            ) ?: throw IllegalStateException("The value is null when the element is inserted")
        }
    }

    override suspend fun update(model: HumanModel): Int {
        return with(ioDispatcher) {

            val id = model.id.toString()

            val values = humanDataMapper.toHumanDb(model)

            val selection = "${BaseColumns._ID}=?"
            val selectionArgs = arrayOf(id)

            return@with context.contentResolver.update(
                HumanContentProvider.CONTENT_URI,
                values,
                selection,
                selectionArgs,
            )
        }
    }

    override suspend fun delete(model: HumanModel): Int {
        return with(ioDispatcher) {
            val id = model.id.toString()

            val selection = "${BaseColumns._ID}=?"
            val selectionArgs = arrayOf(id)

            return@with context.contentResolver.delete(
                HumanContentProvider.CONTENT_URI,
                selection,
                selectionArgs,
            )
        }
    }

    override suspend fun getAll(): List<HumanModel> {
        return with(ioDispatcher) {

            val columns = arrayOf(
                BaseColumns._ID,
                HumanContract.Entry.COLUMN_NAME_TITLE,
                HumanContract.Entry.COLUMN_SURNAME_TITLE,
                HumanContract.Entry.COLUMN_AGE_TITLE,
            )

            val cursor = context.contentResolver.query(
                HumanContentProvider.CONTENT_URI,
                columns,
                null,
                null,
                null,
                null,
            )

            if (cursor == null) {
                throw IllegalArgumentException("The cursor should not have a null value")
            }

            val idColumn = cursor.getColumnIndexOrThrow(BaseColumns._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(HumanContract.Entry.COLUMN_NAME_TITLE)
            val surnameColumn =
                cursor.getColumnIndexOrThrow(HumanContract.Entry.COLUMN_SURNAME_TITLE)
            val ageColumn = cursor.getColumnIndexOrThrow(HumanContract.Entry.COLUMN_AGE_TITLE)

            val result = mutableListOf<HumanModel>()

            cursor.use {
                while (cursor.moveToNext()) {
                    val idValue = cursor.getLong(idColumn)
                    val name = cursor.getString(nameColumn)
                    val surname = cursor.getString(surnameColumn)
                    val age = cursor.getInt(ageColumn)

                    val human = HumanModel(idValue, name, surname, age)
                    result.add(human)
                }
            }


            return@with result;
        }
    }

    override suspend fun getById(id: Long): HumanModel {
        return with(ioDispatcher) {

            val columns = arrayOf(
                BaseColumns._ID,
                HumanContract.Entry.COLUMN_NAME_TITLE,
                HumanContract.Entry.COLUMN_SURNAME_TITLE,
                HumanContract.Entry.COLUMN_AGE_TITLE,
            )

            val uri = ContentUris.withAppendedId(HumanContentProvider.CONTENT_URI, id)

            val cursor = context.contentResolver.query(
                uri,
                columns,
                null,
                null,
                null,
                null,
            )

            if (cursor == null) {
                throw IllegalArgumentException("The cursor should not have a null value")
            }

            cursor.moveToFirst()

            val idColumn = cursor.getColumnIndexOrThrow(BaseColumns._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(HumanContract.Entry.COLUMN_NAME_TITLE)
            val surnameColumn =
                cursor.getColumnIndexOrThrow(HumanContract.Entry.COLUMN_SURNAME_TITLE)
            val ageColumn = cursor.getColumnIndexOrThrow(HumanContract.Entry.COLUMN_AGE_TITLE)

            val idValue = cursor.getLong(idColumn)
            val name = cursor.getString(nameColumn)
            val surname = cursor.getString(surnameColumn)
            val age = cursor.getInt(ageColumn)

            cursor.close()

            val human = HumanModel(idValue, name, surname, age)

            return@with human
        }
    }

    private fun requiredReadAll(): Flow<RequiredUpdate> = callbackFlow {
        val initialRequiredUpdate = RequiredUpdate(
            firstTime = true,
            mustUpdate = true,
        )

        trySend(initialRequiredUpdate)

        val observer = object : ContentObserver(null) {
            override fun onChange(selfChange: Boolean) {
                val requiredUpdate = RequiredUpdate(
                    firstTime = false,
                    mustUpdate = true,
                )
                trySend(requiredUpdate)
            }
        }

        context.contentResolver.registerContentObserver(
            HumanContentProvider.CONTENT_URI,
            true,
            observer,
        )

        awaitClose {
            context.contentResolver.unregisterContentObserver(observer)
        }
    }

    private fun requiredRead(id: Long): Flow<Boolean> = callbackFlow {
        val initialValue = true

        trySend(initialValue)

        val observer = object : ContentObserver(null) {
            override fun onChange(selfChange: Boolean) {
                trySend(true)
            }
        }

        val uri = ContentUris.withAppendedId(HumanContentProvider.CONTENT_URI, id)

        context.contentResolver.registerContentObserver(
            uri,
            true,
            observer,
        )

        awaitClose {
            context.contentResolver.unregisterContentObserver(observer)
        }
    }

    private suspend fun getAllWithResult(): Container<List<HumanModel>> {
        try {
            delay(1000)
            val data = getAll()

            if (data.isEmpty()) {
                return Container.Empty
            }

            return Container.Data(data)
        } catch (e: Exception) {
            return Container.Error(e)
        }
    }

    private suspend fun getWithResult(id: Long): Container<HumanModel> {
        try {
            delay(1000)
            val data = getById(id)
            return Container.Data(data)
        } catch (e: Exception) {
            return Container.Error(e)
        }
    }
}