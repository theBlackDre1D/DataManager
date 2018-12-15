package com.example.seremtinameno.datamanager.core.di

import com.example.seremtinameno.datamanager.core.permissions.PermissionProvider
import com.example.seremtinameno.datamanager.features.datausage.DataUsageViewModel
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module


val applicationModules = module(override=true) {
//    single { DataUsageViewModel(get()) }
    factory { PermissionProvider() }
}
