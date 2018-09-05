package com.example.seremtinameno.datamanager.features.datausage

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.seremtinameno.datamanager.R
import com.example.seremtinameno.datamanager.core.platform.BaseActivity
import com.example.seremtinameno.datamanager.core.platform.BaseFragment
import com.example.seremtinameno.datamanager.features.settings.SettingsActivity
import com.example.seremtinameno.datamanager.features.splash.SplashActivity
import com.pixplicity.easyprefs.library.Prefs
import es.dmoral.toasty.Toasty

class SetDataLimitActivity : BaseActivity() {

    @BindView(R.id.dataLimit)
    lateinit var dataLimit: EditText

    @BindView(R.id.MB)
    lateinit var MB: RadioButton

    @BindView(R.id.GB)
    lateinit var GB: RadioButton

    private var comingFrom = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_data_limit)

        ButterKnife.bind(this)

        getFromIntent()
        MB.isChecked = true
    }

    companion object {
        const val LIMIT = "limit"
        const val USER_ASKED = "asked"
        const val FROM = "from"

        fun getCallingIntent(context: Context, comingFrom: String): Intent {
            val intent = Intent(context, SetDataLimitActivity::class.java)
            intent.putExtra(FROM, comingFrom)

            return intent
        }
    }

    private fun getFromIntent() {
        val intent = intent

        comingFrom = intent.getStringExtra(FROM)
    }

    private fun checkCheck(): Boolean {
        return if (MB.isChecked && GB.isChecked) {
            Toasty.error(this, "Only one option can be checked", Toast.LENGTH_SHORT, true).show()
            false
        } else {
            true
        }
    }

    @OnClick(R.id.cancel)
    fun cancelClicked() {
        val dialog = AlertDialog.Builder(this).create()
        dialog.setTitle("Sure?")
        dialog.setMessage("Are you sure you want to leave without set data limit?")
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel"
        ) { dialog, _ ->
            dialog.dismiss()
        }

        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Confirm"
        ) { dialog, _ ->
            if (comingFrom == SplashActivity.ACTIVITY_NAME) {
                noDataLimit()
                Toasty.error(this, "Data limit not set", Toast.LENGTH_SHORT, true).show()
            }

            goToMainActivity()
        }

        dialog.show()
    }

    private fun goToMainActivity() {
        Prefs.putBoolean(USER_ASKED, true)
        startActivity(MainActivity.getCallingIntent(this))
    }

    @OnClick(R.id.confirm)
    fun confirmButton() {
        if (checkCheck()) {
            var inNumber = dataLimit.text.toString().toInt()

            if (GB.isChecked) {
                inNumber *= 1024
            }

            Prefs.putInt(MainActivity.DATA_LIMIT, inNumber)

            Toasty.success(this, "Data limit set!", Toast.LENGTH_SHORT, true).show()
            withDataLimit()

            if (comingFrom == SettingsActivity.ACTIVITY_NAME) {
                super.onBackPressed()
            } else {
                goToMainActivity()
            }
        }
    }

    private fun noDataLimit() {
        Prefs.putBoolean(LIMIT, false)
    }

    private fun withDataLimit() {
        Prefs.putBoolean(LIMIT, true)
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
