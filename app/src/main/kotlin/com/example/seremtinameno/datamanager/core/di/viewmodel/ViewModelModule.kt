package com.example.seremtinameno.datamanager.core.di.viewmodel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.example.seremtinameno.datamanager.features.datausage.DataUsageViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {
    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

//    @Binds
//    @IntoMap
//    @ViewModelKey(MoviesViewModel::class)
//    abstract fun bindsMoviesViewModel(moviesViewModel: MoviesViewModel): ViewModel

//    @Binds
//    @IntoMap
//    @ViewModelKey(MovieDetailsViewModel::class)
//    abstract fun bindsMovieDetailsViewModel(movieDetailsViewModel: MovieDetailsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DataUsageViewModel::class)
    abstract fun bindsDataUsage(dataUsage: DataUsageViewModel): ViewModel
}