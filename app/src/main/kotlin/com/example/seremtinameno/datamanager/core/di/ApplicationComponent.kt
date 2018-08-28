/**
 * Copyright (C) 2018 Fernando Cejas Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.seremtinameno.datamanager.core.di

import com.example.seremtinameno.datamanager.AndroidApplication
import com.example.seremtinameno.datamanager.features.datausage.MainActivity
import com.example.seremtinameno.datamanager.core.di.viewmodel.ViewModelModule
import com.example.seremtinameno.datamanager.features.datausage.daily.TestActivity
//import com.fernandocejas.sample.features.movies.MovieDetailsFragment
//import com.fernandocejas.sample.features.movies.MoviesFragment
//import com.fernandocejas.sample.core.navigation.RouteActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class, ViewModelModule::class])
interface ApplicationComponent {
    fun inject(application: AndroidApplication)
//    fun inject(routeActivity: RouteActivity)
    fun inject(activity: MainActivity)
    fun inject(activity: TestActivity)

//    fun inject(moviesFragment: MoviesFragment)
//    fun inject(movieDetailsFragment: MovieDetailsFragment)
}
