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
import android.net.ConnectivityManager
import android.os.Build
import android.os.Process
import android.provider.Settings
import android.support.annotation.RequiresApi
import android.support.constraint.solver.widgets.ConstraintAnchor
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

    private lateinit var usageWidget: TextView

    private lateinit var loadingWidget: View

    private lateinit var wrapperWidget: ScrollView

    private lateinit var progressWidget: ProgressBar

    private lateinit var textProgressWidget: TextView

    private lateinit var networkStatsManager: NetworkStatsManager

    private lateinit var wifiData: NetworkStats.Bucket

    private lateinit var mobileData: NetworkStats.Bucket

    private var subscriberID: String? = null

    private var startTime: Long? = null

    private var endTime: Long? = null

    private var mobilePlanInMB: Int = 2000

//    @Inject
    private lateinit var dataUsage: DataUsageViewModel

    @Inject
    lateinit var delegate: PermissionProvider

    @TargetApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initUI()
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

    private fun loadDataUsage() {
        dataUsage = viewModel(viewModelFactory) {
            observe(dataUsage, ::renderDataUsage)
            failure(failure, ::handleFailure)
        }

        val params = buildParams()
        dataUsage.loadDataUsage(params)
    }

    private fun buildParams(): GetDataUsage.Params {

        return GetDataUsage.Params(this, startTime!!, endTime!!, ConnectivityManager.TYPE_MOBILE)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun renderDataUsage(data: NetworkStats.Bucket?) {
        mobileData = data!!

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
            loadDataUsage()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE) {
            obtainUserData()
        }
    }

    override fun initUI() {
        usageWidget = usage
        loadingWidget = loading
        wrapperWidget = wrapper
        progressWidget = progressBar
        textProgressWidget = textProgres
    }

    @SuppressLint("NewApi", "HardwareIds")
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun initDataInfo() {
        initDates()

        wifiData = networkStatsManager.querySummaryForDevice(ConnectivityManager.TYPE_WIFI, subscriberID, startTime!!, endTime!!)
        mobileData = networkStatsManager.querySummaryForDevice(ConnectivityManager.TYPE_MOBILE, subscriberID, startTime!!, endTime!!)

        hideLoading()
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
        val usedMB = mobileData.rxBytes / 1000000.0
        usageWidget.text = "$usedMB MB"

        progressWidget.progress = calculatePercentage(usedMB)
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
                    // permission granted!
                    Timber.d("Permission to read phone stats granted")
                    loadDataUsage()
                } else {
                    // permission denied
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

//    override fun permissionGranted() {
//        initDataInfo()
//    }
}



