package com.example.seremtinameno.datamanager.features.datausage

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.os.Bundle
import android.app.AppOpsManager.OPSTR_GET_USAGE_STATS
import android.app.AppOpsManager
import android.app.AppOpsManager.MODE_ALLOWED
import android.app.usage.NetworkStats
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Process
import android.provider.Settings
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.widget.TextView
import android.view.View
import antonkozyriatskyi.circularprogressindicator.CircularProgressIndicator
import butterknife.BindView
import butterknife.OnClick
import com.example.seremtinameno.datamanager.AndroidApplication
import com.example.seremtinameno.datamanager.R
import com.example.seremtinameno.datamanager.core.permissions.PermissionProvider
import com.example.seremtinameno.datamanager.core.platform.BaseFragment
import com.example.seremtinameno.datamanager.core.di.ApplicationComponent
import com.example.seremtinameno.datamanager.core.exception.Failure
import com.example.seremtinameno.datamanager.core.extension.failure
import com.example.seremtinameno.datamanager.core.extension.observe
import com.example.seremtinameno.datamanager.core.platform.BaseActivity
import com.example.seremtinameno.datamanager.features.datausage.daily.TestActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.pixplicity.easyprefs.library.Prefs
import timber.log.Timber
import java.text.DecimalFormat
import java.util.Calendar
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class MainActivity : BaseActivity(),        ActivityCompat.OnRequestPermissionsResultCallback,
                                            PermissionProvider.Delegate,
                                            MyProgressTextAdapter.View
{

    private val appComponent: ApplicationComponent by lazy(mode = LazyThreadSafetyMode.NONE) {
        (application as AndroidApplication).appComponent
    }

    @BindView(R.id.dataUsage)
    lateinit var dataUsageWidget:           TextView

    @BindView(R.id.loading)
    lateinit var loadingWidget:             View

    @BindView(R.id.userPlan)
    lateinit var userPlan:                  TextView

    @BindView(R.id.used)
    lateinit var used:                      TextView

    @BindView(R.id.graph)
    lateinit var graphWidget:               BarChart

    @BindView(R.id.wifiUsage)
    lateinit var wifiUsageWidget:           TextView

    @BindView(R.id.wifiUnits)
    lateinit var wifiUnitsWidget:           TextView

    @BindView(R.id.dataUnits)
    lateinit var dataUnitsWidget:           TextView

    @BindView(R.id.progressIndicator)
    lateinit var progressIndicator:         CircularProgressIndicator

    private lateinit var wifiData:          NetworkStats

    private lateinit var mobileData:        NetworkStats

    private var startTime:                  Long? = null

    private var endTime:                    Long? = null

    private var mobilePlanInMB:             Int = 2000

    private var mobileDataPerDay =          HashMap<Long, Long>()

    private var wifiDataPerDay =            HashMap<Long, Long>()

    private var precision =                 DecimalFormat("0.00")

    @Inject
    lateinit var monthlyDataUsage:          DataUsageViewModel

    @Inject
    lateinit var permissionProvider:        PermissionProvider

    @TargetApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        injectUI(this)
        appComponent.inject(this)
        showLoading()

        initPrefs()
        initDates()

        permissionProvider.setDelegate(this)
        permissionCheck()
    }

    @OnClick(R.id.testButton)
    fun goToTest() {
        startActivity(TestActivity.getCallingIntent(this))
    }

    companion object {
        const val SUBSCRIBER_ID = "SUBSCRIBER_ID"
        const val PERMISSION_READ_STATE = 1
        const val REQUEST_CODE = 1

        fun getCallingIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }

    private fun loadData() {

        monthlyDataUsage = viewModel(viewModelFactory) {
            observe(dataUsage, ::renderData)
            failure(failure, ::handleFailure)
        }

        val params = GetDataUsage.Params(this)
        monthlyDataUsage.loadDataUsage(params)

    }

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(Build.VERSION_CODES.M)
    private fun renderData(data: HashMap<String, NetworkStats>?) {
        mobileData = (data!!["data"])!!
        wifiData = (data["wifi"])!!

        processMobileData()
        processWifiData()

//        val todayDate = DateFormat.getDateInstance().format(endTime)
        val todayDate = endTime

        var todayData = 0L
        var todayWifi = 0L

        if (mobileDataPerDay.containsKey(todayDate)) {
            todayData = (mobileDataPerDay[todayDate])!!
        }

        if (wifiDataPerDay.containsKey(todayDate)) {
            todayWifi = (wifiDataPerDay[todayDate])!!
        }
        todayData /= (1024L * 1024L)
        todayWifi /= (1024L * 1024L)

        if (todayData >= 1024L) {
            todayData /= 1024L
            dataUsageWidget.text = precision.format(todayData)
            dataUnitsWidget.text = "GB"
        } else {
            dataUsageWidget.text = precision.format(todayData)
            dataUnitsWidget.text = "MB"

        }

        if (todayWifi >= 1024L) {
            todayWifi /= 1024L
            wifiUsageWidget.text = precision.format(todayWifi)
            wifiUnitsWidget.text = "GB"
        } else {
            wifiUsageWidget.text = precision.format(todayWifi)
            wifiUnitsWidget.text = "MB"
        }

        sortData(mobileDataPerDay)
        sortData(wifiDataPerDay)

        showInGraph()

        hideLoading()
    }

    private fun sortData(map: HashMap<Long, Long>) {
        map.toList().sortedBy { (key, _) -> key}.toMap()
    }

    @TargetApi(Build.VERSION_CODES.N)
    private fun processMobileData() {
//        var previousDate = ""
        var previousDate = 0L

        var totalUsage = 0L

        while (mobileData.hasNextBucket()) {
            val bucket = NetworkStats.Bucket()
            mobileData.getNextBucket(bucket)
            totalUsage += bucket.rxBytes
//            val currentBucketDate = DateFormat.getDateInstance().format(bucket.startTimeStamp)
            val currentBucketDate = bucket.startTimeStamp

            if (previousDate == 0L || previousDate != currentBucketDate) {
                mobileDataPerDay[currentBucketDate] = bucket.rxBytes
            } else if (mobileDataPerDay.containsKey(currentBucketDate)) {
                val currentValue = mobileDataPerDay[currentBucketDate]
                if (currentValue != null) {
                    mobileDataPerDay.replace(currentBucketDate, currentValue + bucket.rxBytes)
                }
            }
            previousDate = currentBucketDate
        }

        calculateDataUsage(totalUsage)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun processWifiData() {
//        var previousDate = ""
        var previousDate = 0L

        var totalUsage = 0L

        while (wifiData.hasNextBucket()) {
            val bucket = NetworkStats.Bucket()
            wifiData.getNextBucket(bucket)
            totalUsage += bucket.rxBytes
//            val currentBucketDate = DateFormat.getDateInstance().format(bucket.startTimeStamp)
            val currentBucketDate = bucket.startTimeStamp

            if (previousDate == 0L || previousDate != currentBucketDate) {
                wifiDataPerDay[currentBucketDate] = bucket.rxBytes
            } else if (wifiDataPerDay.containsKey(currentBucketDate)) {
                val currentValue = wifiDataPerDay[currentBucketDate]
                if (currentValue != null) {
                    wifiDataPerDay.replace(currentBucketDate, currentValue + bucket.rxBytes)
                }
            }
            previousDate = currentBucketDate
        }
    }

    private fun handleFailure(failure: Failure?) {
        hideLoading()
        when (failure) {
            is Failure.NetworkConnection -> showErrorToast(this, "Network error")
            is Failure.ServerError -> showErrorToast(this, "Server error")
            is Failure.PermissionError -> showErrorToast(this, "No permissions")
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
        val permission = permissionProvider.checkPermissionReadPhoneState()
        if (!permission) {
            Timber.d("Asking for permission")
            permissionProvider.askForReadPhoneState(PERMISSION_READ_STATE)
        } else {
            loadData()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE) {
            obtainUserData()
        }
    }

    private fun initDates() {
        val calendar = Calendar.getInstance()
        endTime = calendar.timeInMillis
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        startTime = calendar.timeInMillis
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.M)
    private fun calculateDataUsage(totalUsage: Long) {
        val usedMB = totalUsage / (1024.0 * 1024.0)
//        progressWidget.progress = calculatePercentage(usedMB)

        val textAdapter = MyProgressTextAdapter()
        textAdapter.setView(this)

        userPlan.text = mobilePlanInMB.toString()
        used.text = precision.format(usedMB)
        progressIndicator.maxProgress = 2000.0
        progressIndicator.setProgressTextAdapter(textAdapter)
        progressIndicator.setCurrentProgress(usedMB)
    }

//    @SuppressLint("SetTextI18n")
//    private fun calculatePercentage(used: Double): Int {
//        val percentage = (used/mobilePlanInMB) * 100
////        textProgressWidget.text = "${precision.format(used)} / $mobilePlanInMB (${precision.format(percentage)})%"
//        return percentage.toInt()
//    }

    private fun showInGraph() {
        val list = ArrayList<BarEntry>()

        var position = 0f
        for (i in mobileDataPerDay) {
            val newEntry = BarEntry(position, i.value.toFloat() / (1024f * 1024f))
            list.add(newEntry)
            position++
        }

        val company = BarDataSet(list, "Daily usage in this month")
        company.axisDependency = YAxis.AxisDependency.LEFT
        company.valueTextColor = Color.WHITE

        val data = BarData(company)
        data.barWidth = 0.9f

        graphWidget.data = data

        graphWidget.axisLeft.textColor = Color.WHITE
        graphWidget.axisRight.textColor = Color.WHITE
        graphWidget.legend.textColor = Color.WHITE
        graphWidget.legend.textSize = 15f
        graphWidget.description.text = ""

        graphWidget.animateXY(500, 1500)
        graphWidget.invalidate()
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

    override fun getDataLimit(): Double {
        return mobilePlanInMB.toDouble()
    }
}