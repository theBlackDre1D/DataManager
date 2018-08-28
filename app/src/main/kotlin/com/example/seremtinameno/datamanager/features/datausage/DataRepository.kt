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
import java.util.*
import javax.inject.Inject

interface DataRepository {
    fun getData(applicationContext: Context, startTime: Long, endTime: Long): Either<Failure, HashMap<String, NetworkStats.Bucket>>
//    fun getNetworkStats(applicationContext: Context, startTime: Long, endTime: Long, networkType: Int): Either<Failure, HashMap<String, NetworkStats>>

    class Data
    @Inject constructor(private val permissions: PermissionProvider): DataRepository {
        @RequiresApi(Build.VERSION_CODES.M)
        override fun getData(applicationContext: Context, startTime: Long, endTime: Long): Either<Failure, HashMap<String, NetworkStats.Bucket>> {
            return when (permissions.checkPermissionReadPhoneState()) {
                true -> Either.Right(obtainData(applicationContext, startTime, endTime))
                false -> Either.Left(Failure.PermissionError())
            }
        }

        @RequiresApi(Build.VERSION_CODES.M)
        @SuppressLint("MissingPermission", "HardwareIds")
        private fun obtainData(applicationContext: Context, startTime: Long, endTime: Long): HashMap<String, NetworkStats.Bucket> {
            val networkStatsManager = applicationContext.getSystemService(Context.NETWORK_STATS_SERVICE) as NetworkStatsManager
            var subscriberID: String? = Prefs.getString(MainActivity.SUBSCRIBER_ID, null)

            if (subscriberID == null) {
                val manager = applicationContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                subscriberID = manager.subscriberId

                Prefs.putString(MainActivity.SUBSCRIBER_ID, subscriberID)
            }

            val data = networkStatsManager.querySummaryForDevice(ConnectivityManager.TYPE_MOBILE, subscriberID, startTime, endTime)
            val wifi = networkStatsManager.querySummaryForDevice(ConnectivityManager.TYPE_WIFI, subscriberID, startTime, endTime)

            return hashMapOf("data" to data, "wifi" to wifi)
        }

//        @SuppressLint("MissingPermission", "HardwareIds")
//        @RequiresApi(Build.VERSION_CODES.M)
//        override fun getNetworkStats(applicationContext: Context, startTime: Long, endTime: Long,
//                                     networkType: Int):
//                Either<Failure, HashMap<String, NetworkStats>> {
//
//            val networkStatsManager = applicationContext.getSystemService(Context.NETWORK_STATS_SERVICE) as NetworkStatsManager
//            var subscriberID: String? = Prefs.getString(MainActivity.SUBSCRIBER_ID, null)
//
//            if (subscriberID == null) {
//                val manager = applicationContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
//                subscriberID = manager.subscriberId
//
//                Prefs.putString(MainActivity.SUBSCRIBER_ID, subscriberID)
//            }
//
//            val calendar = Calendar.getInstance()
//            calendar.add(Calendar.DATE, 1)
//            val tomorrow = calendar.timeInMillis
//
//            val networkStats = networkStatsManager.queryDetails(ConnectivityManager.TYPE_MOBILE, subscriberID, startTime, tomorrow)
//
//            var previousDate = ""
//
////            while (networkStats.hasNextBucket()) {
////                val bucket = NetworkStats.Bucket()
////                networkStats.getNextBucket(bucket)
////                val currentBucketDate = DateFormat.getDateInstance().format(bucket.startTimeStamp)
////
////                if (previousDate == "" || previousDate != currentBucketDate) {
////                    dataPerDay[currentBucketDate] = bucket.rxBytes
////                } else if (dataPerDay.containsKey(currentBucketDate)) {
////                    val currentValue = dataPerDay[currentBucketDate]
////                    if (currentValue != null) {
////                        dataPerDay.replace(currentBucketDate, currentValue + bucket.rxBytes)
////                    }
////                }
////                previousDate = currentBucketDate
////            }
//        }
    }
}