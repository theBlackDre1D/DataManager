package com.example.seremtinameno.datamanager.features.datausage

import android.annotation.SuppressLint
import android.app.usage.NetworkStats
import android.app.usage.NetworkStatsManager
import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.support.annotation.RequiresApi
import android.telephony.TelephonyManager
import com.example.seremtinameno.datamanager.MainActivity
import com.example.seremtinameno.datamanager.core.exception.Failure
import com.example.seremtinameno.datamanager.core.functional.Either
import com.example.seremtinameno.datamanager.core.permissions.PermissionProvider
import com.pixplicity.easyprefs.library.Prefs
import javax.inject.Inject

interface DataRepository {
    fun getData(applicationContext: Context, startTime: Long, endTime: Long, connectionType: Int): Either<Failure, NetworkStats.Bucket>

    class Data
    @Inject constructor(private val permissions: PermissionProvider): DataRepository {
        @RequiresApi(Build.VERSION_CODES.M)
        override fun getData(applicationContext: Context, startTime: Long, endTime: Long, connectionType: Int): Either<Failure, NetworkStats.Bucket> {
            return when(permissions.checkPermissionReadPhoneState()) {
                true -> Either.Right(obtainData(applicationContext, startTime, endTime, connectionType))
                false -> Either.Left(Failure.PermissionError())
            }
        }

        @RequiresApi(Build.VERSION_CODES.M)
        @SuppressLint("MissingPermission", "HardwareIds")
        private fun obtainData(applicationContext: Context, startTime: Long, endTime: Long, connectionType: Int): NetworkStats.Bucket {
            val networkStatsManager = applicationContext.getSystemService(Context.NETWORK_STATS_SERVICE) as NetworkStatsManager
            var subscriberID: String? = null

            if (subscriberID == null) {
                val manager = applicationContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                subscriberID = manager.subscriberId

                Prefs.putString(MainActivity.SUBSCRIBER_ID, subscriberID)
            } else {
                subscriberID = Prefs.getString(MainActivity.SUBSCRIBER_ID, null)
            }

            return networkStatsManager.querySummaryForDevice(connectionType, subscriberID, startTime, endTime)
        }
    }
}