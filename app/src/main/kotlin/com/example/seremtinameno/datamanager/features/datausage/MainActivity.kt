package com.example.seremtinameno.datamanager.features.datausage

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
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Build
import android.os.Process
import android.provider.Settings
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.telephony.TelephonyManager
import android.widget.TextView
import android.view.View
import android.widget.ProgressBar
import android.widget.ScrollView
import butterknife.ButterKnife
import butterknife.OnClick
import com.anychart.anychart.*
import com.example.seremtinameno.datamanager.AndroidApplication
import com.example.seremtinameno.datamanager.R
import com.example.seremtinameno.datamanager.core.permissions.PermissionProvider
import com.example.seremtinameno.datamanager.core.platform.BaseFragment
import com.example.seremtinameno.datamanager.core.di.ApplicationComponent
import com.example.seremtinameno.datamanager.core.exception.Failure
import com.example.seremtinameno.datamanager.core.extension.failure
import com.example.seremtinameno.datamanager.core.extension.observe
import com.example.seremtinameno.datamanager.core.platform.BaseActivity
import com.example.seremtinameno.datamanager.features.datausage.daily.DailyUsageFragment
import com.example.seremtinameno.datamanager.features.datausage.daily.TestActivity
import com.example.seremtinameno.datamanager.features.datausage.daily.TestFragment1
import com.example.seremtinameno.datamanager.features.datausage.daily.TestFragment2
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.ncapdevi.fragnav.FragNavController
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.android.synthetic.main.loading.*
import timber.log.Timber
import java.text.DateFormat
import java.text.DecimalFormat
import java.util.Calendar
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class MainActivity : BaseActivity(),        ActivityCompat.OnRequestPermissionsResultCallback,
                                            PermissionProvider.Delegate
//                                            FragNavController.RootFragmentListener
{

    private val appComponent: ApplicationComponent by lazy(mode = LazyThreadSafetyMode.NONE) {
        (application as AndroidApplication).appComponent
    }

    private lateinit var dataUsageWidget:           TextView

    private lateinit var loadingWidget:             View

    private lateinit var wrapperWidget:             ScrollView

    private lateinit var progressWidget:            ProgressBar

    private lateinit var textProgressWidget:        TextView

    private lateinit var graphWidget:               BarChart

    private lateinit var wifiUsageWidget:           TextView

    private lateinit var wifiUnitsWidget:           TextView

    private lateinit var dataUnitsWidget:           TextView

    private lateinit var wifiData:                  NetworkStats.Bucket

    private lateinit var mobileData:                NetworkStats.Bucket

    private var startTime:                          Long? = null

    private var endTime:                            Long? = null

    private var mobilePlanInMB:                     Int = 2000

    private var todayUsedMB:                        Double = 0.0

    private var dataPerDay =                        HashMap<String, Long>()

    private var calculated = false
    private var rendered = false

    private var precision = DecimalFormat("0.00")

//    @Inject
    private lateinit var totalDataUsage:            DataUsageViewModel

    private lateinit var todayDataUsage:            DataUsageViewModel

    @Inject
    lateinit var delegate:                          PermissionProvider

    @TargetApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initUI()
        butterKnifeInject(this)
        appComponent.inject(this)

        initPrefs()
        initDates()

        delegate.setDelegate(this)
        permissionCheck()
        testQuery()
        showInGraph()
//        initBottomBar(savedInstanceState)
    }

    @OnClick(R.id.testButton)
    fun goToTest() {
        startActivity(TestActivity.getCallingIntent(this))
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
    }

    private fun loadTodayDataUsage() {
        todayDataUsage = viewModel(viewModelFactory) {
            observe(dataUsage, ::renderTodayData)
            failure(failure, ::handleFailure)
        }
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DATE, -1)
        val secondStartDate = calendar.timeInMillis

        val params = GetDataUsage.Params(this, secondStartDate, endTime!!)
        todayDataUsage.loadDataUsage(params)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun renderTodayData(todayData: HashMap<String, NetworkStats.Bucket>?) {
        if (!rendered) {
            val todayDataUsage = todayData!!["data"]
            val todayWifiUsage = todayData["wifi"]

            todayUsedMB = todayDataUsage!!.rxBytes / (1024.0 * 1024.0)
            var todayWifiUsed = todayWifiUsage!!.rxBytes / (1024.0 * 1024.0)

            if (todayWifiUsed >= 1024L) {
                todayWifiUsed /= 1024L
                wifiUnitsWidget.text = "GB"
            } else {
                wifiUnitsWidget.text = "MB"
            }

            dataUsageWidget.text = precision.format(todayUsedMB)
            wifiUsageWidget.text = precision.format(todayWifiUsed)

            rendered = true
        }
    }

    private fun buildParams(): GetDataUsage.Params {
        return GetDataUsage.Params(this, startTime!!, endTime!!)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun renderTotalDataUsage(data: HashMap<String, NetworkStats.Bucket>?) {
        mobileData = (data!!["data"])!!
        wifiData = (data["wifi"])!!

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

    private fun loadData() {
        showLoading()

        loadTodayDataUsage()
        loadTotalDataUsage()

        hideLoading()
    }

    private fun obtainUserData() {
        val permission = delegate.checkPermissionReadPhoneState()
        if (!permission) {
            Timber.d("Asking for permission")
            delegate.askForReadPhoneState(PERMISSION_READ_STATE)
        } else {
            loadData()
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
            val usedMB = mobileData.rxBytes / (1024.0 * 1024.0)
            progressWidget.progress = calculatePercentage(usedMB)
            calculated = true
        }
    }

    @SuppressLint("SetTextI18n")
    private fun calculatePercentage(used: Double): Int {
        val percentage = (used/mobilePlanInMB) * 100
        textProgressWidget.text = "${precision.format(used)} / $mobilePlanInMB (${precision.format(percentage)})%"

        return percentage.toInt()
    }

    @TargetApi(Build.VERSION_CODES.N)
    @SuppressLint("MissingPermission", "HardwareIds")
    @RequiresApi(Build.VERSION_CODES.M)
    private fun testQuery() {
        val networkStatsManager = applicationContext.getSystemService(Context.NETWORK_STATS_SERVICE) as NetworkStatsManager
        var subscriberID: String? = Prefs.getString(SUBSCRIBER_ID, null)

        if (subscriberID == null) {
            val manager = applicationContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            subscriberID = manager.subscriberId

            Prefs.putString(SUBSCRIBER_ID, subscriberID)
        }

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, 1)
        val tomorrow = calendar.timeInMillis

        val networkStats = networkStatsManager.queryDetails(ConnectivityManager.TYPE_MOBILE, subscriberID, startTime!!, tomorrow)

        var previousDate = ""

        while (networkStats.hasNextBucket()) {
            val bucket = NetworkStats.Bucket()
            networkStats.getNextBucket(bucket)
            val currentBucketDate = DateFormat.getDateInstance().format(bucket.startTimeStamp)

            if (previousDate == "" || previousDate != currentBucketDate) {
                dataPerDay[currentBucketDate] = bucket.rxBytes
            } else if (dataPerDay.containsKey(currentBucketDate)) {
                val currentValue = dataPerDay[currentBucketDate]
                if (currentValue != null) {
                    dataPerDay.replace(currentBucketDate, currentValue + bucket.rxBytes)
                }
            }
            previousDate = currentBucketDate
        }
    }

    private fun showInGraph() {
        val list = ArrayList<BarEntry>()

        var position = 0f
        for (i in dataPerDay) {
            val newEntry = BarEntry(position, i.value.toFloat() / (1024f * 1024f))
            list.add(newEntry)
            position++
        }

        val company = BarDataSet(list, "Monthly usage")
        company.axisDependency = YAxis.AxisDependency.LEFT

        val data = BarData(company)
        data.barWidth = 0.9f

        graphWidget.data = data
        graphWidget.axisLeft.textColor = Color.WHITE
        graphWidget.axisRight.textColor = Color.WHITE
        graphWidget.legend.textColor = Color.WHITE
        graphWidget.description.text = ""
        graphWidget.invalidate()
    }

//    private fun fakeEntrees2() {
//        val data = ArrayList<DataEntry>()
//        val chart = AnyChart.column()
//
//        for (i in dataPerDay) {
//            val newEntry = ValueDataEntry(i.key, i.value / (1024f * 1024f))
//            data.add(newEntry)
//        }
//
//        chart.setData(data)
//        anyGraph.setBackgroundColor(resources.getColor(R.color.darb_blue))
//        anyGraph.setChart(chart)
//    }


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
                    loadData()
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