package com.example.seremtinameno.datamanager.core

import android.app.Application
import android.content.ContextWrapper
import com.crashlytics.android.Crashlytics
import com.example.seremtinameno.datamanager.core.di.ApplicationComponent
import com.example.seremtinameno.datamanager.core.di.ApplicationModule
import com.example.seremtinameno.datamanager.core.di.DaggerApplicationComponent
import com.pixplicity.easyprefs.library.Prefs
import com.squareup.leakcanary.LeakCanary
import io.fabric.sdk.android.Fabric
import org.koin.android.ext.android.startKoin


class AndroidApplication : Application() {

    val appComponent: ApplicationComponent by lazy(mode = LazyThreadSafetyMode.NONE) {
        DaggerApplicationComponent
                .builder()
                .applicationModule(ApplicationModule(this))
                .build()
    }

    override fun onCreate() {
        super.onCreate()
        injectMembers()
        initializeLeakDetection()

        initPrefs()
        initCrashlytics()
    }

    private fun injectMembers() = appComponent.inject(this)

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
