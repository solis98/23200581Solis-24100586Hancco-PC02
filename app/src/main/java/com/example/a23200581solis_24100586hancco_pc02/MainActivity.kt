package com.example.a23200581solis_24100586hancco_pc02

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.a23200581solis_24100586hancco_pc02.di.AppContainer
import com.example.a23200581solis_24100586hancco_pc02.ui.images.ImagesApp
import com.example.a23200581solis_24100586hancco_pc02.ui.images.ImagesViewModel

class MainActivity : ComponentActivity() {

    private val container by lazy {
        AppContainer(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {

                val factory = remember {
                    ImagesViewModel.Factory(
                        container.apodRepository
                    )
                }

                val imagesViewModel: ImagesViewModel = viewModel(
                    factory = factory
                )

                ImagesApp(
                    viewModel = imagesViewModel
                )
            }
        }
    }
}