package com.example.seremtinameno.datamanager

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import android.app.AppOpsManager.OPSTR_GET_USAGE_STATS
import android.app.AppOpsManager
import android.app.AppOpsManager.MODE_ALLOWED
import android.app.usage.NetworkStats
import android.app.usage.NetworkStatsManager
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Process
import android.provider.Settings
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.widget.TextView
import android.view.View
import android.widget.ProgressBar
import android.widget.ScrollView
import com.example.seremtinameno.datamanager.core.permissions.PermissionProvider
import com.example.seremtinameno.datamanager.core.platform.BaseFragment
import com.example.seremtinameno.datamanager.core.di.ApplicationComponent
import com.example.seremtinameno.datamanager.core.exception.Failure
import com.example.seremtinameno.datamanager.core.extension.failure
import com.example.seremtinameno.datamanager.core.extension.observe
import com.example.seremtinameno.datamanager.core.platform.BaseActivity
import com.example.seremtinameno.datamanager.features.datausage.DataUsageViewModel
import com.example.seremtinameno.datamanager.features.datausage.GetDataUsage
import com.github.mikephil.charting.charts.LineChart
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.android.synthetic.main.loading.*
import timber.log.Timber
import java.util.*
import javax.inject.Inject


class MainActivity : BaseActivity(),        ActivityCompat.OnRequestPermissionsResultCallback,
                                            PermissionProvider.Delegate
{

    private val appComponent: ApplicationComponent by lazy(mode = LazyThreadSafetyMode.NONE) {
        (application as AndroidApplication).appComponent
    }

    private lateinit var dataUsageWidget:           TextView

    private lateinit var loadingWidget:             View

    private lateinit var wrapperWidget:             ScrollView

    private lateinit var progressWidget:            ProgressBar

    private lateinit var textProgressWidget:        TextView

    private lateinit var graphWidget:               LineChart

    private lateinit var wifiUsageWidget:           TextView

    private lateinit var wifiUnitsWidget:           TextView

    private lateinit var dataUnitsWidget:           TextView

//    private lateinit var networkStatsManager:       NetworkStatsManager

    private lateinit var wifiData:                  NetworkStats.Bucket

    private lateinit var mobileData:                NetworkStats.Bucket

    private var startTime:                          Long? = null

    private var endTime:                            Long? = null

    private var mobilePlanInMB:                     Int = 2000

    private var todayUsedMB:                        Int = 0

    private var calculated = false
    private var rendered = false

//    @Inject
    private lateinit var totalDataUsage: DataUsageViewModel

    private lateinit var todayDataUsage: DataUsageViewModel

    @Inject
    lateinit var delegate: PermissionProvider

    @TargetApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initUI()
        showLoading()
        appComponent.inject(this)

        initPrefs()
        initDates()

        delegate.setDelegate(this)
        permissionCheck()
    }

    companion object {
        const val SUBSCRIBER_ID = "SUBSCRIBER_ID"
        const val PERMISSION_READ_STATE = 1
        const val REQUEST_CODE = 1
    }

    private fun loadTotalDataUsage() {
        totalDataUsage = viewModel(viewModelFactory) {
            observe(dataUsage, ::renderTotalDataUsage)
            failure(failure, ::handleFailure)
        }

        val params = buildParams()
        totalDataUsage.loadDataUsage(params)

        loadTodayDataUsage()
    }

    private fun loadTodayDataUsage() {
        todayDataUsage = viewModel(viewModelFactory) {
            observe(dataUsage, ::renderTodayData)
            failure(failure, ::handleFailure)
        }

        val params = GetDataUsage.Params(this, endTime!!, endTime!!)
        todayDataUsage.loadDataUsage(params)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun renderTodayData(todayData: HashMap<String, NetworkStats.Bucket>?) {
        if (!rendered) {
            val todayDataUsage = todayData!!["data"]
            val todayWifiUsage = todayData["wifi"]

            todayUsedMB = (todayDataUsage!!.rxBytes / 1000000).toInt()
            var todayWifiUsed = (todayWifiUsage!!.rxBytes / 1000000)

//            if (todayWifiUsed >= 1024.toLong()) {
//                todayWifiUsed /= 1024
//                wifiUnitsWidget.text = "GB"
//            } else {
//                wifiUnitsWidget.text = "MB"
//            }

            dataUsageWidget.text = todayUsedMB.toString()
            wifiUsageWidget.text = todayWifiUsed.toString()

            rendered = true
        }

    }

    private fun buildParams(): GetDataUsage.Params {
        return GetDataUsage.Params(this, startTime!!, endTime!!)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun renderTotalDataUsage(data: HashMap<String, NetworkStats.Bucket>?) {
        mobileData = (data!!["data"])!!

        calculateDataUsage()
    }

    private fun handleFailure(failure: Failure?) {
        when (failure) {
            is Failure.NetworkConnection -> showMessage(this, "Network error")
            is Failure.ServerError -> showMessage(this, "Server error")
            is Failure.PermissionError -> showMessage(this, "No permissions")
        }
    }

    private fun permissionCheck() {
        if (!checkForPermission(this)) {
            startActivityForResult(Intent((Settings.ACTION_USAGE_ACCESS_SETTINGS)), REQUEST_CODE)
        } else {
            obtainUserData()
        }
    }

    private fun obtainUserData() {
        val permission = delegate.checkPermissionReadPhoneState()
        if (!permission) {
            Timber.d("Asking for permission")
            delegate.askForReadPhoneState(PERMISSION_READ_STATE)
        } else {
            loadTotalDataUsage()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE) {
            obtainUserData()
        }
    }

    override fun initUI() {
        dataUsageWidget =           dataUsage
        wifiUsageWidget =           wifiUsage
        loadingWidget =             loading
        wrapperWidget =             wrapper
        progressWidget =            progressBar
        textProgressWidget =        textProgres
        graphWidget =               graph
        wifiUnitsWidget =           wifiUnits
        dataUnitsWidget =           dataUnits
    }

    private fun initDates() {
        val calendar = Calendar.getInstance()
        endTime = calendar.timeInMillis
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        startTime = calendar.timeInMillis
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.M)
    private fun calculateDataUsage() {
        if (!calculated) {
            val usedMB = mobileData.rxBytes / 1000000.0
            progressWidget.progress = calculatePercentage(usedMB)
            calculated = true
        }
    }

    @SuppressLint("SetTextI18n")
    private fun calculatePercentage(used: Double): Int {
        val percentage = (used/mobilePlanInMB) * 100
        textProgressWidget.text = "$used / $mobilePlanInMB ($percentage)%"

        return percentage.toInt()
    }

    private fun initPrefs() {
        Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(packageName)
                .setUseDefaultSharedPreference(true)
                .build()
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun checkForPermission(context: Context): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(OPSTR_GET_USAGE_STATS, Process.myUid(), context.packageName)
        return mode == MODE_ALLOWED
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_READ_STATE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Timber.d("Permission to read phone stats granted")
                    loadTotalDataUsage()
                } else {
                    Timber.d("Permission to read phone stats denied")
                    showMessage(this, "Permission denied")
                    hideLoading()
                }
            }
        }
    }

    override fun fragment(): BaseFragment {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showLoading() {
        loadingWidget.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        loadingWidget.visibility = View.GONE
    }

    override fun getActivity(): BaseActivity {
        return this
    }
}