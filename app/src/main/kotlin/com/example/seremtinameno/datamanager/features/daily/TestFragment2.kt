package com.example.seremtinameno.datamanager.features.daily

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.seremtinameno.datamanager.R

class TestFragment2: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_test2, container, false)

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        fun newInstance(instance: Int): TestFragment2 {
            val args = Bundle()
            args.putInt("something", instance)
            val fragment = TestFragment2()
            fragment.arguments = args
            return fragment
        }
    }
}