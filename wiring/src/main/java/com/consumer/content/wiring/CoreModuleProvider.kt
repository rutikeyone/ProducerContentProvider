package com.consumer.content.wiring

import com.consumer.content.impl.ActivityRequired
import com.consumer.content.impl.DefaultCoreProvider
import com.contentprovider.core.common.CommonUi
import com.contentprovider.core.common.Core
import com.contentprovider.core.common.CoreProvider
import com.contentprovider.core.common.ErrorHandler
import com.contentprovider.core.common.Resources
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.ElementsIntoSet

@Module
@InstallIn(SingletonComponent::class)
interface CoreModuleProvider {

    companion object {

        @Provides
        fun provideCoreProvider(): CoreProvider {
            return DefaultCoreProvider()
        }

        @Provides
        @ElementsIntoSet
        fun provideActivityRequiredSet(
            commonUi: CommonUi,
            resources: Resources,
        ): Set<ActivityRequired> {
            val set = hashSetOf<ActivityRequired>()
            if (commonUi is ActivityRequired) set.add(commonUi)
            if (resources is ActivityRequired) set.add(resources)
            return set
        }

        @Provides
        fun provideCommonUi(): CommonUi {
            return Core.commonUi
        }

        @Provides
        fun provideResources(): Resources {
            return Core.resources
        }

        @Provides
        fun provideErrorHandler(): ErrorHandler {
            return Core.errorHandler
        }

    }

}