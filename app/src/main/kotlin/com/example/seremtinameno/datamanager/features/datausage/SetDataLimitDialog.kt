package com.example.seremtinameno.datamanager.features.datausage

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.Window
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.seremtinameno.datamanager.R
import com.pixplicity.easyprefs.library.Prefs
import es.dmoral.toasty.Toasty

class SetDataLimitDialog(context: Context, val delegate: View): Dialog(context, R.style.Dialog) {

    @BindView(R.id.dataLimit)
    lateinit var dataLimit: EditText

    @BindView(R.id.MB)
    lateinit var MB: RadioButton

    @BindView(R.id.GB)
    lateinit var GB: RadioButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_set_data_limit)

        ButterKnife.bind(this)

        MB.isChecked = true

    }

    private fun checkCheck(): Boolean {
        return if (MB.isChecked && GB.isChecked) {
            Toasty.error(context, "Only one option can be checked", Toast.LENGTH_SHORT, true).show()
            false
        } else {
            true
        }
    }

    @OnClick(R.id.cancel)
    fun cancelClicked() {
        val dialog = AlertDialog.Builder(context).create()
        dialog.setTitle("Sure?")
        dialog.setMessage("Are you sure you want to leave without set data limit?")
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel"
        ) { dialog, which ->
            dialog.dismiss()
        }

        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Confirm"
        ) { dialog, which ->
            delegate.setWithoutDataLimit()

            Toasty.error(context, "Data limit not set", Toast.LENGTH_SHORT, true).show()

            super.onBackPressed()
        }

        dialog.show()
    }

    @OnClick(R.id.confirm)
    fun confirmButton() {
        if (checkCheck()) {
            Prefs.putInt(MainActivity.DATA_LIMIT, dataLimit.text.toString().toInt())

            Toasty.success(context, "Data limit set!", Toast.LENGTH_SHORT, true).show()
            delegate.setWithDataLimit()

            super.onBackPressed()
        }
    }

    interface View {
        fun setWithDataLimit()

        fun setWithoutDataLimit()
    }
}