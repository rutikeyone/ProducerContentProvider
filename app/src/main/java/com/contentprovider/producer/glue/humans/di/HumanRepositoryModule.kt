package com.contentprovider.producer.glue.humans.di

import com.contentprovider.humans.domain.repositories.HumanRepository
import com.contentprovider.producer.glue.humans.repositories.AdapterHumanRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface HumanRepositoryModule {

    @Binds
    fun bindHumanRepository(repository: AdapterHumanRepository): HumanRepository

}