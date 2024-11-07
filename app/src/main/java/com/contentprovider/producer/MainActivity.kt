package com.contentprovider.producer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.consumer.content.impl.ActivityRequired
import com.contentprovider.core.theme.ProducerContentProviderTheme
import com.contentprovider.humans.presentation.update.viewmodel.HumanUpdateViewModel
import com.contentprovider.producer.glue.navigation.AppNavHost
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.components.ActivityComponent
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity(), HumanUpdateViewModel.FactoryProvider {

    @Inject
    lateinit var activityRequiredSet: Set<@JvmSuppressWildcards ActivityRequired>

    @EntryPoint
    @InstallIn(ActivityComponent::class)
    interface ViewModelFactoryProvider {
        fun humanUpdateViewModelFactory(): HumanUpdateViewModel.Factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            ProducerContentProviderTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    AppNavHost(navController = rememberNavController())
                }
            }
        }

        activityRequiredSet.forEach { it.onCreated(this) }
    }

    override fun onStart() {
        activityRequiredSet.forEach { it.onStarted() }
        super.onStart()
    }

    override fun onStop() {
        activityRequiredSet.forEach { it.onStopped() }
        super.onStop()
    }

    override fun onDestroy() {
        activityRequiredSet.forEach { it.onDestroyed() }
        super.onDestroy()
    }

    override fun provideViewModelFactory(): HumanUpdateViewModel.Factory {
        return EntryPointAccessors.fromActivity(
            this,
            ViewModelFactoryProvider::class.java,
        ).humanUpdateViewModelFactory()
    }

}
