package com.example.seremtinameno.datamanager.features.splash

import android.os.Bundle
import android.os.Handler
import com.crashlytics.android.Crashlytics
import com.example.seremtinameno.datamanager.core.platform.BaseActivity
import com.example.seremtinameno.datamanager.core.platform.BaseFragment
import com.example.seremtinameno.datamanager.features.datausage.MainActivity
import io.fabric.sdk.android.Fabric

class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initCrashlytics()

//        val handler = Handler()
//        handler.postDelayed({
            startActivity(MainActivity.getCallingIntent(this))
            finish()
//        }, 500)

    }

    private fun initCrashlytics() {
        Fabric.with(this, Crashlytics())
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
