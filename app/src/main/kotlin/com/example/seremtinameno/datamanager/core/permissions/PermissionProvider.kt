package com.example.seremtinameno.datamanager.core.permissions

import android.Manifest
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import com.fernandocejas.sample.core.platform.BaseActivity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PermissionProvider
@Inject constructor(){

    private lateinit var delegate: Delegate

    fun setDelegate(delegate: Delegate) {
        this.delegate = delegate
    }

    fun checkPermissionReadPhoneState(PERMISSION_READ_STATE: Int) {
        if (ContextCompat.checkSelfPermission(delegate.getActivity(), Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            // We do not have this permission. Let's ask the user
            askForReadPhoneState(PERMISSION_READ_STATE)
        }
        else {
//            delegate.showToastMessage("Permission not granted")
            delegate.permissionGranted()
        }
    }

    private fun askForReadPhoneState(PERMISSION_READ_STATE: Int) {
        ActivityCompat.requestPermissions(delegate.getActivity(), arrayOf(Manifest.permission.READ_PHONE_STATE), PERMISSION_READ_STATE)
    }

    interface Delegate {
        fun getActivity(): BaseActivity
//        fun showToastMessage(message: String)
        fun permissionGranted()
    }
}