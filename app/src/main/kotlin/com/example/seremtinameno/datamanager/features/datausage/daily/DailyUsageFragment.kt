package com.example.seremtinameno.datamanager.features.datausage.daily

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.ButterKnife
import butterknife.Unbinder
import com.example.seremtinameno.datamanager.R


class DailyUsageFragment: Fragment() {

    private lateinit var unbinder: Unbinder

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_daily, container, false)

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        unbinder = ButterKnife.bind(this, view)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unbinder.unbind()
    }

    companion object {
        fun newInstance(instance: Int): DailyUsageFragment {
            val args = Bundle()
            args.putInt("something", instance)
            val fragment = DailyUsageFragment()
            fragment.arguments = args
            return fragment
        }
    }
}