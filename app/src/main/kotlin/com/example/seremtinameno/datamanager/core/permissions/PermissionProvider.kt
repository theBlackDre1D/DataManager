package com.example.seremtinameno.datamanager.core.permissions

import android.Manifest
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import com.example.seremtinameno.datamanager.core.platform.BaseActivity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PermissionProvider
@Inject constructor()
{

    private lateinit var delegate: Delegate

    fun setDelegate(delegate: Delegate) {
        this.delegate = delegate
    }

    fun checkPermissionReadPhoneState(): Boolean {
        return ContextCompat.checkSelfPermission(delegate.getActivity(), Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
    }

    fun askForReadPhoneState(PERMISSION_READ_STATE: Int) {
        ActivityCompat.requestPermissions(delegate.getActivity(), arrayOf(Manifest.permission.READ_PHONE_STATE), PERMISSION_READ_STATE)
    }

    interface Delegate {
        fun getActivity(): BaseActivity
    }
}