package com.example.seremtinameno.datamanager.features.splash

import android.os.Build
import android.os.Bundle
import com.example.seremtinameno.datamanager.features.kill_application.KillActivity
import com.example.seremtinameno.datamanager.core.platform.BaseActivity
import com.example.seremtinameno.datamanager.core.platform.BaseFragment
import com.example.seremtinameno.datamanager.features.datausage.MainActivity
import com.example.seremtinameno.datamanager.features.datausage.SetDataLimitActivity
import com.example.seremtinameno.datamanager.features.intro.IntroActivity
import com.pixplicity.easyprefs.library.Prefs


class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            val androidVersion = android.os.Build.VERSION.SDK_INT
            if (androidVersion < Build.VERSION_CODES.M) {
                // application can't continue because Android at version lower than M doesn't have main feature
                startActivity(KillActivity.getCallingIntent(this))
            } else {
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
