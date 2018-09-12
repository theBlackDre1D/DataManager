package com.example.seremtinameno.datamanager.features.daily

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.view.View
import butterknife.BindView
import com.example.seremtinameno.datamanager.R
import com.example.seremtinameno.datamanager.core.AndroidApplication
import com.example.seremtinameno.datamanager.core.di.ApplicationComponent
import com.example.seremtinameno.datamanager.core.platform.BaseActivity
import com.example.seremtinameno.datamanager.core.platform.BaseFragment
import com.example.seremtinameno.datamanager.features.datausage.MainActivity
import com.github.florent37.hollyviewpager.HollyViewPager
import com.github.florent37.hollyviewpager.HollyViewPagerConfigurator
import java.io.Serializable
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class DailyUseActivity : BaseActivity() {

    private val appComponent: ApplicationComponent by lazy(mode = LazyThreadSafetyMode.NONE) {
        (application as AndroidApplication).appComponent
    }

    @BindView(R.id.hollyViewPager)
    lateinit var hollyViewPager: HollyViewPager

    @BindView(R.id.loading)
    lateinit var loading: View

    private lateinit var mobileData: HashMap<String, Long>

    private lateinit var wifiData: HashMap<String, Long>

    private lateinit var dataTogether: HashMap<String, Usage>

    private val orderedDates = ArrayList<String>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily_use)

        injectUI(this)
        appComponent.inject(this)
        getFromIntent()
        processToOneEntity()
        createOrderDates()

        initHollyViewPager()
    }

    companion object {
        const val USAGE = "usage"

        fun getCallingIntent(context: Context, usage: HashMap<String, HashMap<String, Long>>): Intent {
            val intent = Intent(context, DailyUseActivity::class.java)
            intent.putExtra(USAGE, usage)
            return intent
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SimpleDateFormat")
    private fun createOrderDates() {
        val keys = dataTogether.keys

        val comparator = compareBy<String> { LocalDate.parse(it, DateTimeFormatter.ofPattern(MainActivity.DATE_FORMAT))}

        keys.sortedWith(comparator).forEach { key ->
            orderedDates.add(key)
        }
    }

    private fun processToOneEntity() {
        dataTogether = HashMap()

        mobileData.forEach { key, value ->
            dataTogether.put(key, Usage(data = value, wifi = null))
        }

        wifiData.forEach { key, value ->
            if (dataTogether.containsKey(key)) {
                dataTogether[key]!!.wifi = value
            } else {
                dataTogether.put(key, Usage(data = null, wifi = value))
            }
        }
    }

    private fun getFromIntent() {
        val usage = intent.extras[USAGE] as HashMap<String, HashMap<String, Long>>
        mobileData = usage["data"]!!
        wifiData = usage["wifi"]!!
    }

    private fun initHollyViewPager() {
        hollyViewPager.viewPager.pageMargin = resources.getDimensionPixelOffset(R.dimen.viewpager_margin)

        hollyViewPager.configurator = HollyViewPagerConfigurator {
            ((it+4)%10)/10f
        }

        hollyViewPager.setAdapter(object : FragmentPagerAdapter(supportFragmentManager) {
            override fun getItem(position: Int): Fragment {
                val key = orderedDates[position]
                return DailyUsageFragment.newInstance(0, dataTogether[key]!!)
            }

            override fun getCount(): Int {
                return dataTogether.size
            }

            override fun getPageTitle(position: Int): CharSequence? {
                return "Title: $position"
            }
        })
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

    data class Usage(val data: Long?,
                     var wifi: Long?): Serializable
}
