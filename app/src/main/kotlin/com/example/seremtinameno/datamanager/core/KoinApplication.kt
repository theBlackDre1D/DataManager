package com.example.seremtinameno.datamanager.core

import android.app.Application
import com.example.seremtinameno.datamanager.core.di.applicationModules
import org.koin.android.ext.android.startKoin

class KoinApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin(this, listOf(applicationModules))

    }
}