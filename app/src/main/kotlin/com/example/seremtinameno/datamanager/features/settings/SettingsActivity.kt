package com.example.seremtinameno.datamanager.features.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Switch
import butterknife.BindView
import butterknife.OnClick
import com.example.seremtinameno.datamanager.R
import com.example.seremtinameno.datamanager.core.AndroidApplication
import com.example.seremtinameno.datamanager.core.di.ApplicationComponent
import com.example.seremtinameno.datamanager.core.platform.BaseActivity
import com.example.seremtinameno.datamanager.core.platform.BaseFragment
import com.example.seremtinameno.datamanager.features.datausage.MainActivity
import com.example.seremtinameno.datamanager.features.datausage.SetDataLimitActivity
import com.pixplicity.easyprefs.library.Prefs

class SettingsActivity : BaseActivity() {

    @BindView(R.id.allowNotifications)
    lateinit var allowNotifications:        Switch

    @BindView(R.id.enableDataLimit)
    lateinit var dataLimitStatus:           Switch

    private var changed = false

    private val appComponent: ApplicationComponent by lazy(mode = LazyThreadSafetyMode.NONE) {
        (application as AndroidApplication).appComponent
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        injectUI(this)
        appComponent.inject(this)

        initCurrentPrefs()
    }

    companion object {

        const val ACTIVITY_NAME = "settings"
        const val NOTIFICATIONS = "notifications"

        fun getCallingIntent(context: Context): Intent {
            return Intent(context, SettingsActivity::class.java)
        }
    }

    private fun initCurrentPrefs() {
        dataLimitStatus.isChecked = Prefs.getBoolean(SetDataLimitActivity.LIMIT, true)
        allowNotifications.isChecked = Prefs.getBoolean(NOTIFICATIONS, true)
    }

    @OnClick(R.id.enableDataLimit)
    fun dataLimitStatus() {
        Prefs.putBoolean(SetDataLimitActivity.LIMIT, dataLimitStatus.isChecked)
    }

    @OnClick(R.id.allowNotifications)
    fun notificationsStatus() {
        Prefs.putBoolean(NOTIFICATIONS, allowNotifications.isChecked)
    }

    @OnClick(R.id.newDataLimit)
    fun clicked() {
        startActivity(SetDataLimitActivity.getCallingIntent(this, ACTIVITY_NAME))
    }

    @OnClick(R.id.raiseDataLimit)
    fun raiseDataLimit() {
        //TODO: create dialog for this
        changed = true
    }

    override fun onBackPressed() {
        if (changed) {
            startActivity(MainActivity.getCallingIntent(this))
        } else {
            super.onBackPressed()
        }
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
