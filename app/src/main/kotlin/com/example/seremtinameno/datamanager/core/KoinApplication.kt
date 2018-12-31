package com.example.seremtinameno.datamanager.core

import android.app.Application
import android.content.ContextWrapper
import com.crashlytics.android.Crashlytics
import com.example.seremtinameno.datamanager.core.di.applicationModules
import com.pixplicity.easyprefs.library.Prefs
import com.squareup.leakcanary.LeakCanary
import io.fabric.sdk.android.Fabric
import org.koin.android.ext.android.startKoin

class KoinApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin(this, listOf(applicationModules))

        initializeLeakDetection()

        initPrefs()
        initCrashlytics()
    }

    private fun initializeLeakDetection() {
//        if (BuildConfig.DEBUG)
        LeakCanary.install(this)
    }

    private fun initPrefs() {
        Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(packageName)
                .setUseDefaultSharedPreference(true)
                .build()
    }


    private fun initCrashlytics() {
        Fabric.with(this, Crashlytics())
    }
}