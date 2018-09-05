package com.example.seremtinameno.datamanager.features.settings

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import butterknife.BindView
import butterknife.OnClick
import com.example.seremtinameno.datamanager.R
import com.pixplicity.easyprefs.library.Prefs

class RaiseDataLimitDialog(context: Context): Dialog(context, R.style.Dialog) {

    @BindView(R.id.extraData)
    lateinit var extraData: EditText

    @BindView(R.id.confirm)
    lateinit var confirm: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.raise_data_limit_dialog)
    }

    companion object {
        const val EXTRA_DATA = "extra_data"
    }

    @OnClick(R.id.confirm)
    fun confirm() {
        Prefs.putInt(EXTRA_DATA, extraData.toString().toInt())
    }
}