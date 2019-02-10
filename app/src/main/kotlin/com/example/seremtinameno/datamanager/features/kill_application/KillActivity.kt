package com.example.seremtinameno.datamanager.features.kill_application

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.seremtinameno.datamanager.R
import es.dmoral.toasty.Toasty
import org.jetbrains.anko.alert
import org.jetbrains.anko.yesButton

class KillActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kill)

        alert("You don't have minimal version of Android necessary to run thi app correctly. \n \n"
        + "Press OK to exit application.") {
            yesButton { finish() }
        }.show()

        Toasty.error(this, "We're really sorry :(", Toast.LENGTH_LONG).show()
    }

    companion object {
        fun getCallingIntent(context: Context): Intent {
            val intent = Intent(context, KillActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            return intent
        }
    }
}
