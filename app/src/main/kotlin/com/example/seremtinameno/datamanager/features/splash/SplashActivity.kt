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

        try {
            val set = Prefs.getBoolean(SetDataLimitActivity.LIMIT, false)
            val asked = Prefs.getBoolean(SetDataLimitActivity.USER_ASKED, false)
            val firstLaunch = Prefs.getBoolean(IntroActivity.FIRST_LAUNCH, true)

            if (firstLaunch) {
                startActivityForResult(IntroActivity.getCallingIntent(this), 1)
            }

            if (asked) {
                startActivity(MainActivity.getCallingIntent(this))
            } else {
                startActivity(SetDataLimitActivity.getCallingIntent(this, ACTIVITY_NAME))
            }
        } catch (error: Exception) {
            startActivity(SetDataLimitActivity.getCallingIntent(this, ACTIVITY_NAME))
        }

        finish()
    }

    companion object {
        const val ACTIVITY_NAME = "splash"
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

    override fun onHomePressed() {
        // nothing
    }

    override fun onDailyPressed() {
        // nothing
    }
}
