package com.example.seremtinameno.datamanager.features.daily

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.view.View
import butterknife.BindView
import com.example.seremtinameno.datamanager.R
import com.example.seremtinameno.datamanager.core.AndroidApplication
import com.example.seremtinameno.datamanager.core.di.ApplicationComponent
import com.example.seremtinameno.datamanager.core.platform.BaseActivity
import com.example.seremtinameno.datamanager.core.platform.BaseFragment
import com.github.florent37.hollyviewpager.HollyViewPager
import com.github.florent37.hollyviewpager.HollyViewPagerConfigurator

class DailyUseActivity : BaseActivity() {

    private val appComponent: ApplicationComponent by lazy(mode = LazyThreadSafetyMode.NONE) {
        (application as AndroidApplication).appComponent
    }

    @BindView(R.id.hollyViewPager)
    lateinit var hollyViewPager: HollyViewPager

    @BindView(R.id.loading)
    lateinit var loading: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily_use)

        injectUI(this)
        appComponent.inject(this)

        initHollyViewPager()

    }

    companion object {
        fun getCallingIntent(context: Context): Intent {
            return Intent(context, DailyUseActivity::class.java)
        }
    }

    private fun initHollyViewPager() {
        hollyViewPager.viewPager.pageMargin = resources.getDimensionPixelOffset(R.dimen.viewpager_margin)

        hollyViewPager.configurator = HollyViewPagerConfigurator {
            ((it+4)%10)/10f
        }

        hollyViewPager.setAdapter(object : FragmentPagerAdapter(supportFragmentManager) {
            override fun getItem(position: Int): Fragment {
                return DailyUsageFragment.newInstance(0)
            }

            override fun getCount(): Int {
                return 5
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
}
