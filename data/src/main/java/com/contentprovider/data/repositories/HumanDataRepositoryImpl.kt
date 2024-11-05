package com.contentprovider.data.repositories

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

                val data = readAllWithResult()
                trySend(data)
            }
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

    override suspend fun readAll(): List<HumanModel> {
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

    private suspend fun readAllWithResult(): Container<List<HumanModel>> {
        return with(ioDispatcher) {
            try {
                delay(1000)

                val data = readAll()

                if (data.isEmpty()) {
                    return@with Container.Empty
                }

                return@with Container.Data(data)
            } catch (e: Exception) {
                return@with Container.Error(e)
            }
        }
    }

}