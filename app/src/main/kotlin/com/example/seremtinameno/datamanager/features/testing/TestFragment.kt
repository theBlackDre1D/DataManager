package com.example.seremtinameno.datamanager.features.testing

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.seremtinameno.datamanager.R

class TestFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.activity_test, container, false)

        return rootView
    }

    companion object {
        fun newInstance(): TestFragment {
            return TestFragment()
        }
    }
}
