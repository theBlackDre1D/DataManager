package com.example.seremtinameno.datamanager.core.di

import android.arch.lifecycle.ViewModelProvider
import com.example.seremtinameno.datamanager.core.di.viewmodel.ViewModelFactory
import com.example.seremtinameno.datamanager.core.permissions.PermissionProvider
import com.example.seremtinameno.datamanager.features.datausage.DataUsageViewModel
import com.example.seremtinameno.datamanager.features.datausage.GetDataUsage
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module


val applicationModules = module(override=true) {
    single<PermissionProvider> { get() }
    single { DataUsageViewModel( get() ) }
    factory<ViewModelProvider.Factory> { ViewModelFactory( get() ) }
    single { GetDataUsage( get() )}
}
