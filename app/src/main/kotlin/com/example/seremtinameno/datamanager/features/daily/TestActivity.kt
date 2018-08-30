package com.example.seremtinameno.datamanager.features.datausage.daily

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import butterknife.BindView
import com.example.seremtinameno.datamanager.AndroidApplication
import com.example.seremtinameno.datamanager.R
import com.example.seremtinameno.datamanager.core.di.ApplicationComponent
import com.example.seremtinameno.datamanager.core.platform.BaseActivity
import com.example.seremtinameno.datamanager.core.platform.BaseFragment
import com.ncapdevi.fragnav.FragNavController

class TestActivity : BaseActivity(), FragNavController.RootFragmentListener
{
    @BindView(R.id.viewPager)
    lateinit var viewPager: ViewPager

    @BindView(R.id.tabs)
    lateinit var tabLayout: TabLayout

    private val appComponent: ApplicationComponent by lazy(mode = LazyThreadSafetyMode.NONE) {
        (application as AndroidApplication).appComponent
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        injectUI(this)
        appComponent.inject(this)
//        initBottomBar(savedInstanceState)
        initFragments()
    }

    private fun initFragments() {
        setupViewPager(viewPager)
        tabLayout.setupWithViewPager(viewPager)
    }

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = ViewPagerAdapater(supportFragmentManager)
        adapter.addFragment(DailyUsageFragment.newInstance(0), "Daily usage")
        adapter.addFragment(TestFragment1.newInstance(0), "Test1")
        adapter.addFragment(TestFragment2.newInstance(0), "Test2")
        viewPager.adapter = adapter
    }

    private fun initBottomBar(savedInstanceState: Bundle?) {
        val builder = FragNavController.newBuilder(savedInstanceState, supportFragmentManager, R.id.container)

        val fragments = ArrayList<Fragment>()

        fragments.add(DailyUsageFragment.newInstance(0))
        fragments.add(TestFragment1.newInstance(0))
        fragments.add(TestFragment2.newInstance(0))

        builder.rootFragments(fragments)
        builder.rootFragmentListener(this, 3)
    }

    companion object {
        fun getCallingIntent(context: Context): Intent {
            return Intent(context, TestActivity::class.java)
        }
    }

    override fun getRootFragment(index: Int): Fragment {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun fragment(): BaseFragment {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showLoading() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun hideLoading() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
