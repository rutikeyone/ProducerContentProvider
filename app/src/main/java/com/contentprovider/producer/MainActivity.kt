package com.contentprovider.producer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.contentprovider.producer.glue.navigation.AppNavHost
import com.contentprovider.core.theme.ProducerContentProviderTheme
import com.contentprovider.humans.presentation.update.viewmodel.HumanUpdateViewModel
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.components.ActivityComponent

@AndroidEntryPoint
class MainActivity : ComponentActivity(), HumanUpdateViewModel.FactoryProvider {

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
    }

    override fun provideHumanUpdateViewModelFactory(): HumanUpdateViewModel.Factory {
        return EntryPointAccessors.fromActivity(
            this, ViewModelFactoryProvider::class.java
        ).humanUpdateViewModelFactory()
    }

}
