package com.example.seremtinameno.datamanager.features.datausage

import android.app.usage.NetworkStats
import android.arch.lifecycle.MutableLiveData
import com.example.seremtinameno.datamanager.core.platform.BaseViewModel
import com.example.seremtinameno.datamanager.features.datausage.GetDataUsage
import javax.inject.Inject

class DataUsageViewModel
@Inject constructor(private val getDataUsage: GetDataUsage): BaseViewModel() {

    var dataUsage: MutableLiveData<HashMap<String, NetworkStats>> = MutableLiveData()

    fun loadDataUsage(params: GetDataUsage.Params) = getDataUsage(params) { it.either(::handleFailure, ::handleData) }

    private fun handleData(data: HashMap<String, NetworkStats>) {
        dataUsage.value = data
    }
}