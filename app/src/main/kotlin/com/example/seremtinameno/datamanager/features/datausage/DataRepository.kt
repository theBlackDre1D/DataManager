package com.example.seremtinameno.datamanager.features.datausage

import android.annotation.SuppressLint
import android.app.usage.NetworkStats
import android.app.usage.NetworkStatsManager
import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.support.annotation.RequiresApi
import android.telephony.TelephonyManager
import com.example.seremtinameno.datamanager.core.exception.Failure
import com.example.seremtinameno.datamanager.core.functional.Either
import com.example.seremtinameno.datamanager.core.permissions.PermissionProvider
import com.pixplicity.easyprefs.library.Prefs
import java.text.DateFormat
import java.util.*
import javax.inject.Inject

interface DataRepository {
    fun getData(applicationContext: Context): Either<Failure, HashMap<String, NetworkStats>>
//    fun getNetworkStats(applicationContext: Context, startTime: Long, endTime: Long, networkType: Int): Either<Failure, HashMap<String, NetworkStats>>

    class Data // DataRepositoryImplementation
    @Inject constructor(private val permissions: PermissionProvider): DataRepository {
        @RequiresApi(Build.VERSION_CODES.M)
        override fun getData(applicationContext: Context): Either<Failure, HashMap<String, NetworkStats>> {
            return when (permissions.checkPermissionReadPhoneState()) {
                true -> Either.Right(obtainMonthlyData(applicationContext))
                false -> Either.Left(Failure.PermissionError())
            }
        }

        @RequiresApi(Build.VERSION_CODES.M)
        @SuppressLint("MissingPermission", "HardwareIds")
        private fun obtainMonthlyData(applicationContext: Context): HashMap<String, NetworkStats> {
            val networkStatsManager = applicationContext.getSystemService(Context.NETWORK_STATS_SERVICE) as NetworkStatsManager
            var subscriberID: String? = Prefs.getString(MainActivity.SUBSCRIBER_ID, null)

            if (subscriberID == null) {
                val manager = applicationContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                subscriberID = manager.subscriberId

                Prefs.putString(MainActivity.SUBSCRIBER_ID, subscriberID)
            }
            val today = Calendar.getInstance().timeInMillis

            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DATE, 1)
            val tomorrow = calendar.timeInMillis
            calendar.timeInMillis = today

            calendar.set(Calendar.DAY_OF_MONTH, 1)
            val firstDayInMonth = calendar.timeInMillis

            val data = networkStatsManager.queryDetails(ConnectivityManager.TYPE_MOBILE, subscriberID, firstDayInMonth, tomorrow)
            val wifi = networkStatsManager.queryDetails(ConnectivityManager.TYPE_WIFI, subscriberID, firstDayInMonth, tomorrow)

            return hashMapOf("data" to data, "wifi" to wifi)
        }
    }
}