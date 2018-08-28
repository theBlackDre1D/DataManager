package com.example.seremtinameno.datamanager.features.datausage.daily

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import com.example.seremtinameno.datamanager.AndroidApplication
import com.example.seremtinameno.datamanager.R
import com.example.seremtinameno.datamanager.core.di.ApplicationComponent
import com.example.seremtinameno.datamanager.core.platform.BaseActivity
import com.example.seremtinameno.datamanager.core.platform.BaseFragment
import com.ncapdevi.fragnav.FragNavController

const val INDEX_RECENTS = FragNavController.TAB1
const val INDEX_FAVORITES = FragNavController.TAB2
const val INDEX_NEARBY = FragNavController.TAB3
const val INDEX_FRIENDS = FragNavController.TAB4
const val INDEX_FOOD = FragNavController.TAB5
const val INDEX_RECENTS2 = FragNavController.TAB6
const val INDEX_FAVORITES2 = FragNavController.TAB7
const val INDEX_NEARBY2 = FragNavController.TAB8
const val INDEX_FRIENDS2 = FragNavController.TAB9
const val INDEX_FOOD2 = FragNavController.TAB10
const val INDEX_RECENTS3 = FragNavController.TAB11
const val INDEX_FAVORITES3 = FragNavController.TAB12

class TestActivity : BaseActivity(), FragNavController.RootFragmentListener
{

    private val appComponent: ApplicationComponent by lazy(mode = LazyThreadSafetyMode.NONE) {
        (application as AndroidApplication).appComponent
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        appComponent.inject(this)

        initBottomBar(savedInstanceState)
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

    override fun initUI() {
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
