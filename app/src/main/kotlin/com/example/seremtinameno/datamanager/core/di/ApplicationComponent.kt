package com.example.seremtinameno.datamanager.core.di

import com.example.seremtinameno.datamanager.core.AndroidApplication
import com.example.seremtinameno.datamanager.features.datausage.MainActivity
import com.example.seremtinameno.datamanager.core.di.viewmodel.ViewModelModule
import com.example.seremtinameno.datamanager.core.platform.BaseActivity
import com.example.seremtinameno.datamanager.features.settings.SettingsActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class, ViewModelModule::class])
interface ApplicationComponent {
    fun inject(application: AndroidApplication)
//    fun inject(routeActivity: RouteActivity)
    fun inject(activity: MainActivity)
    fun inject(activity: SettingsActivity)
    fun inject(activity: BaseActivity)

//    fun inject(moviesFragment: MoviesFragment)
//    fun inject(movieDetailsFragment: MovieDetailsFragment)
}
