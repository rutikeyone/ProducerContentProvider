package com.contentprovider.data.di

import android.content.Context
import com.contentprovider.data.HumanDataRepository
import com.contentprovider.data.db.HumanDbHelper
import com.contentprovider.data.mappers.HumanDataMapper
import com.contentprovider.data.repositories.HumanDataRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier

@Qualifier
annotation class IODispatcher

@Module
@InstallIn(SingletonComponent::class)
interface HumanDataModule {

    @Binds
    fun bindHumanDataRepository(humanDataRepository: HumanDataRepositoryImpl): HumanDataRepository

    companion object {

        @Provides
        fun provideHumanDbHelper(@ApplicationContext context: Context): HumanDbHelper {
            return HumanDbHelper(context)
        }

        @Provides
        @IODispatcher
        fun provideIODispatcher(): CoroutineDispatcher {
            return Dispatchers.IO
        }

        @Provides
        fun provideHumanMapper(): HumanDataMapper {
            return HumanDataMapper()
        }

    }

}