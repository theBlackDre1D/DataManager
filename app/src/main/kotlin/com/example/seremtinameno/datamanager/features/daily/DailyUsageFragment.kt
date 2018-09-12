package com.example.seremtinameno.datamanager.features.daily

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import com.example.seremtinameno.datamanager.R
import com.github.florent37.hollyviewpager.HollyViewPagerBus
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView


class DailyUsageFragment: Fragment() {

    @BindView(R.id.scrollView)
    lateinit var observableScrollView: ObservableScrollView

    @BindView(R.id.text1)
    lateinit var text1: TextView

    @BindView(R.id.text2)
    lateinit var text2: TextView

    private lateinit var unbinder: Unbinder

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_daily, container, false)

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        unbinder = ButterKnife.bind(this, view)
        getFromIntent()


        HollyViewPagerBus.registerScrollView(context, observableScrollView)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unbinder.unbind()
    }

    private fun getFromIntent() {
        val args = arguments
        args?.let {
            val usage = args.getSerializable(USAGE_DATA) as DailyUseActivity.Usage
            text1.text = usage.data.toString()
            text2.text = usage.wifi.toString()
        }
    }

    companion object {
        const val USAGE_DATA = "usage_data"

        fun newInstance(instance: Int, usage: DailyUseActivity.Usage): DailyUsageFragment {
            val args = Bundle()
            args.putInt("something", instance)
            args.putSerializable(USAGE_DATA, usage)
            val fragment = DailyUsageFragment()
            fragment.arguments = args
            return fragment
        }
    }
}