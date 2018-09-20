package com.example.seremtinameno.datamanager.features.datausage

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.os.Bundle
import android.app.AppOpsManager.OPSTR_GET_USAGE_STATS
import android.app.AppOpsManager
import android.app.AppOpsManager.MODE_ALLOWED
import android.app.usage.NetworkStats
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Process
import android.provider.Settings
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.widget.TextView
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import antonkozyriatskyi.circularprogressindicator.CircularProgressIndicator
import butterknife.BindView
import butterknife.OnClick
import co.zsmb.materialdrawerkt.builders.drawer
import co.zsmb.materialdrawerkt.builders.footer
import co.zsmb.materialdrawerkt.draweritems.badgeable.primaryItem
import co.zsmb.materialdrawerkt.draweritems.divider
import com.example.seremtinameno.datamanager.core.AndroidApplication
import com.example.seremtinameno.datamanager.R
import com.example.seremtinameno.datamanager.core.permissions.PermissionProvider
import com.example.seremtinameno.datamanager.core.platform.BaseFragment
import com.example.seremtinameno.datamanager.core.di.ApplicationComponent
import com.example.seremtinameno.datamanager.core.exception.Failure
import com.example.seremtinameno.datamanager.core.extension.failure
import com.example.seremtinameno.datamanager.core.extension.observe
import com.example.seremtinameno.datamanager.core.helpers.ColorParser
import com.example.seremtinameno.datamanager.core.platform.BaseActivity
import com.example.seremtinameno.datamanager.features.daily.DailyUseActivity
import com.example.seremtinameno.datamanager.features.settings.RaiseDataLimitDialog
import com.example.seremtinameno.datamanager.features.settings.SettingsActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.mikepenz.materialdrawer.Drawer
import com.pixplicity.easyprefs.library.Prefs
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.experimental.launch
import timber.log.Timber
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
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

    @BindView(R.id.dataViews)
    lateinit var dataViews:                 LinearLayout

    @BindView(R.id.threeLines)
    lateinit var threeLines:                ImageView

//    private lateinit var drawer:            Drawer

    private lateinit var wifiData:          NetworkStats

    private lateinit var mobileData:        NetworkStats

    private var startTime:                  Long? = null

    private var endTime:                    Long? = null

    private var mobilePlanInMB:             Int = 0

    private var mobileDataPerDay = HashMap<String, Long>()

    private var wifiDataPerDay = HashMap<String, Long>()

    private var precision = DecimalFormat("0.00")

    private var mobilePlanSet = false

    private var todayDate = ""

    private val formatter = SimpleDateFormat(DATE_FORMAT)

    private var usedMB = 0.0

    @Inject
    lateinit var monthlyDataUsage:          DataUsageViewModel

    @Inject
    lateinit var permissionProvider:        PermissionProvider

//    @TargetApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupDrawer()

        injectUI(this)
        appComponent.inject(this)
//        setUpDrawer()
        setupListeners()
        showLoading()

        initDates()
        checkIfLimitIsSet()
        prepareDataPlan()

        permissionProvider.setDelegate(this)
        permissionCheck()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onResume() {
        super.onResume()

        checkIfLimitIsSet()
        prepareDataPlan()
        refreshUI()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun refreshUI() {
//        val plan = Prefs.getInt(DATA_LIMIT, 0)
//        val extraData = Prefs.getInt(RaiseDataLimitDialog.EXTRA_DATA, 0)
//        mobilePlanInMB = plan + extraData
//
//        userPlan.text = mobilePlanInMB.toString()
        calculateDataUsage(usedMB.toLong())
    }

    private fun goToDailyUsage() {
        startActivity(DailyUseActivity.getCallingIntent(this,
                hashMapOf("data" to mobileDataPerDay, "wifi" to wifiDataPerDay)))
    }

    companion object {
        const val SUBSCRIBER_ID = "SUBSCRIBER_ID"
        const val PERMISSION_READ_STATE = 1
        const val REQUEST_CODE = 1
        const val DATA_LIMIT = "data_limit"
        const val DATE_FORMAT = "dd/MM/yyyy"
        const val TEXT_SIZE = 15f

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

//        sortData(mobileDataPerDay)
//        sortData(wifiDataPerDay)

        showTodayData()
        showTodayWifi()

        showInGraph()

        hideLoading()
    }

    private fun sortData(map: HashMap<Long, Long>) {
        map.toList().sortedBy { (key, _) -> key}.toMap()
    }

    private fun showTodayData() {
        var todayData = 0L

        if (mobileDataPerDay.size > 0) {
            if (mobileDataPerDay.containsKey(todayDate)) {
                todayData = mobileDataPerDay[todayDate]!!
            }
        }

        var inDouble = todayData / (1024.0 * 1024.0)

        if (inDouble >= 1024.0) {
            inDouble /= 1024.0
            dataUsageWidget.text = precision.format(inDouble)
            dataUnitsWidget.text = "GB"
        } else {
            dataUsageWidget.text = precision.format(inDouble)
            dataUnitsWidget.text = "MB"
        }
    }

    private fun showTodayWifi() {
        var todayWifi = 0L

        if (wifiDataPerDay.size > 0) {
            if (wifiDataPerDay.containsKey(todayDate)) {
                todayWifi = wifiDataPerDay[todayDate]!!
            }
        }

        var inDouble = todayWifi / (1024.0 * 1024.0)

        if (inDouble >= 1024L) {
            inDouble /= 1024L
            wifiUsageWidget.text = precision.format(inDouble)
            wifiUnitsWidget.text = "GB"
        } else {
            wifiUsageWidget.text = precision.format(inDouble)
            wifiUnitsWidget.text = "MB"
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    private fun processMobileData() {
        var previousDate = ""
//        var previousDate = 0L

        var totalUsage = 0L

        while (mobileData.hasNextBucket()) {
            val bucket = NetworkStats.Bucket()
            mobileData.getNextBucket(bucket)
            totalUsage += bucket.rxBytes
//            val currentBucketDate = DateFormat.getDateInstance().format(bucket.startTimeStamp)
            val currentBucketDate = formatter.format(bucket.startTimeStamp)
//            val currentBucketDate = bucket.startTimeStamp

            if (previousDate == "" || previousDate != currentBucketDate) {
                mobileDataPerDay[currentBucketDate] = bucket.rxBytes
            } else if (mobileDataPerDay.containsKey(currentBucketDate)) {
                val currentValue = mobileDataPerDay[currentBucketDate]
                if (currentValue != null) {
                    mobileDataPerDay.replace(currentBucketDate, currentValue + bucket.rxBytes)
                }
            }
            previousDate = currentBucketDate
        }

        usedMB = totalUsage.toDouble()
        calculateDataUsage(totalUsage)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun processWifiData() {
        var previousDate = ""
//        var previousDate = 0L

        var totalUsage = 0L

        while (wifiData.hasNextBucket()) {
            val bucket = NetworkStats.Bucket()
            wifiData.getNextBucket(bucket)
            totalUsage += bucket.rxBytes
//            val currentBucketDate = DateFormat.getDateInstance().format(bucket.startTimeStamp)
            val currentBucketDate = formatter.format(bucket.startTimeStamp)
//            val currentBucketDate = bucket.startTimeStamp

            if (previousDate == "" || previousDate != currentBucketDate) {
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

//        todayDate = DateFormat.getDateInstance().format(endTime)
        todayDate = formatter.format(endTime)

        val dateInString = todayDate.substring(0,2)
        if (dateInString == "01") {
            newMonth()
        }
    }

    private fun newMonth() {
        Prefs.putInt(RaiseDataLimitDialog.EXTRA_DATA, 0)
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.M)
    private fun calculateDataUsage(totalUsage: Long) {
        val usageInMB = totalUsage / (1024.0 * 1024.0)
//        progressWidget.progress = calculatePercentage(usedMB)

        val textAdapter = MyProgressTextAdapter()
        textAdapter.setView(this)

        userPlan.text = mobilePlanInMB.toString()
        used.text = precision.format(usageInMB)
        progressIndicator.maxProgress = mobilePlanInMB.toDouble()
        progressIndicator.setProgressTextAdapter(textAdapter)
        progressIndicator.setCurrentProgress(usageInMB)
    }

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
        company.valueTextSize = TEXT_SIZE
        company.color = ColorParser.parse(this, "green")

        val data = BarData(company)
        data.barWidth = 0.7f

        graphWidget.data = data

        graphWidget.axisLeft.textColor = ColorParser.parse(this, "white")
        graphWidget.axisLeft.textSize = TEXT_SIZE

        graphWidget.axisRight.textColor = ColorParser.parse(this, "white")
        graphWidget.axisRight.textSize = TEXT_SIZE

        graphWidget.legend.textColor = ColorParser.parse(this, "white")
        graphWidget.legend.textSize = TEXT_SIZE
        graphWidget.description.text = ""

        graphWidget.animateXY(500, 1500)
        graphWidget.invalidate()
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun checkForPermission(context: Context): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE)
                as AppOpsManager
        val mode = appOps.checkOpNoThrow(OPSTR_GET_USAGE_STATS, Process.myUid(),
                context.packageName)
        return mode == MODE_ALLOWED
    }

    private fun prepareDataPlan() {
        if (!mobilePlanSet) {
            dataViews.visibility = View.GONE
        } else {
            dataViews.visibility = View.VISIBLE
            val plan = Prefs.getInt(DATA_LIMIT, 0)
            val extraData = Prefs.getInt(RaiseDataLimitDialog.EXTRA_DATA, 0)
            mobilePlanInMB = plan + extraData
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_READ_STATE -> {
                if (grantResults.isNotEmpty() &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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

    override fun onBackPressed() {
        moveTaskToBack(true)
        finish()
    }

    private fun checkIfLimitIsSet() {
        mobilePlanSet = Prefs.getBoolean(SetDataLimitActivity.LIMIT, false)
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

    override fun onHomePressed() {
        // nothing
        Toasty.info(this, "You are already there :)").show()
    }

    override fun onDailyPressed() {
        goToDailyUsage()
    }

    override fun setupListeners() {
        threeLines.setOnClickListener {
            drawer.openDrawer()
        }
    }
}