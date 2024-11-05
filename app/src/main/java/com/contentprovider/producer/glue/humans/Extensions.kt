package com.contentprovider.producer.glue.humans

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.contentprovider.humans.presentation.update.viewmodel.HumanUpdateViewModel
import com.contentprovider.producer.MainActivity

@Composable
fun requiredHumanUpdateViewModelFactory(): HumanUpdateViewModel.Factory {
    val context = LocalContext.current

    return (context as MainActivity).provideViewModelFactory()
}