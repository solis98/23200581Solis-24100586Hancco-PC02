package com.example.a23200581solis_24100586hancco_pc02.di

import android.content.Context
import com.example.a23200581solis_24100586hancco_pc02.BuildConfig
import com.example.a23200581solis_24100586hancco_pc02.data.local.apod.AppDatabase
import com.example.a23200581solis_24100586hancco_pc02.data.remote.RetrofitProvider
import com.example.a23200581solis_24100586hancco_pc02.data.repository.ApodRepository

class AppContainer(context: Context) {

    private val database = AppDatabase.getInstance(context)

    val apodRepository: ApodRepository by lazy {
        ApodRepository(
            api = RetrofitProvider.apodApi,
            dao = database.apodDao(),
            apiKey = BuildConfig.NASA_API_KEY
        )
    }
}