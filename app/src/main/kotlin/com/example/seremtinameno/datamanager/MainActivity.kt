package com.example.seremtinameno.datamanager

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.view.Menu
import android.view.MenuItem
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
import android.support.v4.app.ActivityCompat
import android.widget.TextView
import butterknife.BindView
import android.telephony.TelephonyManager
import android.view.View
import android.widget.ScrollView
import com.example.seremtinameno.datamanager.core.permissions.PermissionProvider
import com.example.seremtinameno.datamanager.core.platform.BaseFragment
import com.fernandocejas.sample.core.di.ApplicationComponent
import com.fernandocejas.sample.core.platform.BaseActivity
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import java.util.*
import javax.inject.Inject


class MainActivity : BaseActivity(),        NavigationView.OnNavigationItemSelectedListener,
                                            ActivityCompat.OnRequestPermissionsResultCallback,
                                            PermissionProvider.Delegate
{

    private val appComponent: ApplicationComponent by lazy(mode = LazyThreadSafetyMode.NONE) {
        (application as AndroidApplication).appComponent
    }

    @BindView(R.id.usage)
    lateinit var usage: TextView

    @BindView(R.id.wrapper)
    lateinit var wrapper: ScrollView

    @BindView(R.id.loading)
    lateinit var loading: View

    private lateinit var networkStatsManager: NetworkStatsManager

    private lateinit var data: NetworkStats

    private var subscriberID: String? = null

    private var startTime: Long? = null

    private var endTime: Long? = null

    @Inject
    lateinit var delegate: PermissionProvider

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val toggle = ActionBarDrawerToggle(
//                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
//        drawer_layout.addDrawerListener(toggle)
//        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
        butterKnifeInject(this)
        appComponent.inject(this)

        initPrefs()

        delegate.setDelegate(this)
        delegate.checkPermissionReadPhoneState(PERMISSION_READ_STATE)
//        checkPermission()
    }

    companion object {
        const val SUBSCRIBER_ID = "SUBSCRIBER_ID"
        const val PERMISSION_READ_STATE = 1
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_camera -> {
                // Handle the camera action
            }
            R.id.nav_gallery -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_manage -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    @SuppressLint("NewApi", "HardwareIds")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun initDataInfo() {
        if (!checkForPermission(this)) {
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        }

        networkStatsManager = applicationContext.getSystemService(Context.NETWORK_STATS_SERVICE) as NetworkStatsManager

        if (subscriberID == null) {
            val manager = applicationContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            subscriberID = manager.subscriberId

            Prefs.putString(SUBSCRIBER_ID, subscriberID)
        } else {
            subscriberID = Prefs.getString(SUBSCRIBER_ID, null)
        }

        val calendar = Calendar.getInstance()
        endTime = calendar.timeInMillis
//        val currentMonth = calendar.get(Calendar.MONTH) + 1
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        startTime = calendar.timeInMillis

        data = networkStatsManager.querySummary(ConnectivityManager.TYPE_WIFI, subscriberID, startTime!!, endTime!!)

        hideLoading()
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
                    // you may now do the action that requires this permission
                    initDataInfo()
                } else {
                    // permission denied
                    showMessage(this, "Permission denied")
                }
            }
        }
    }

    override fun fragment(): BaseFragment {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showLoading() {
        loading.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        loading.visibility = View.GONE
    }

    override fun getActivity(): BaseActivity {
        return this
    }

    override fun permissionGranted() {
        initDataInfo()
    }
}



