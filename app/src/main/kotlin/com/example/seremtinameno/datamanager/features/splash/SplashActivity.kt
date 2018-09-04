package com.example.seremtinameno.datamanager.features.splash

import android.content.ContextWrapper
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.TaskStackBuilder
import com.crashlytics.android.Crashlytics
import com.example.seremtinameno.datamanager.core.platform.BaseActivity
import com.example.seremtinameno.datamanager.core.platform.BaseFragment
import com.example.seremtinameno.datamanager.features.datausage.MainActivity
import com.example.seremtinameno.datamanager.features.datausage.SetDataLimitActivity
import com.example.seremtinameno.datamanager.features.intro.IntroActivity
import com.pixplicity.easyprefs.library.Prefs
import io.fabric.sdk.android.Fabric

class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initCrashlytics()
        initPrefs()

//        val handler = Handler()
//        handler.postDelayed({

//        }, 500)

        try {
            val set = Prefs.getBoolean(SetDataLimitActivity.LIMIT, false)
            val asked = Prefs.getBoolean(SetDataLimitActivity.USER_ASKED, false)

            if (asked) {
                startActivity(MainActivity.getCallingIntent(this))
            } else {
                startActivity(SetDataLimitActivity.getCallingIntent(this))
            }
        } catch (error: Exception) {
            startActivity(SetDataLimitActivity.getCallingIntent(this))
        }

        finish()
//        TaskStackBuilder.create(this)
//                .addNextIntentWithParentStack(IntroActivity.getCallingIntent(this))
//                .addNextIntent(MainActivity.getCallingIntent(this))
//                .startActivities()

    }

    private fun initPrefs() {
        Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(packageName)
                .setUseDefaultSharedPreference(true)
                .build()
    }


    private fun initCrashlytics() {
//        Fabric.with(this, Crashlytics())
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
