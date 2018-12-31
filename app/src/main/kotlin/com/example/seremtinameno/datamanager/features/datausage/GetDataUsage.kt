package com.example.seremtinameno.datamanager.features.datausage

import android.app.usage.NetworkStats
import android.content.Context
import com.example.seremtinameno.datamanager.core.interactor.UseCase
import javax.inject.Inject

class GetDataUsage
//@Inject constructor
(private val dataRepository: DataRepository
) : UseCase<HashMap<String, NetworkStats>, GetDataUsage.Params>()
{
    override suspend fun run(params: Params) =
            dataRepository.getData(params.context)

    data class Params(val context: Context)
}