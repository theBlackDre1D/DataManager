package com.example.seremtinameno.datamanager.features.daily

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TabWidget
import android.widget.TextView
import antonkozyriatskyi.circularprogressindicator.CircularProgressIndicator
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import com.example.seremtinameno.datamanager.R
import com.example.seremtinameno.datamanager.features.datausage.MainActivity
import com.example.seremtinameno.datamanager.features.datausage.MyProgressTextAdapter
import com.github.florent37.hollyviewpager.HollyViewPagerBus
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView
import com.pixplicity.easyprefs.library.Prefs
import java.text.DecimalFormat


class DailyUsageFragment: Fragment(), MyProgressTextAdapter.View {

    @BindView(R.id.scrollView)
    lateinit var observableScrollView: ObservableScrollView

    @BindView(R.id.usedWifi)
    lateinit var usedWifiWidget: TextView

    @BindView(R.id.usedData)
    lateinit var usedDataWidget: TextView

    @BindView(R.id.circularProgressBar)
    lateinit var circularProgressBar: CircularProgressIndicator

    @BindView(R.id.title)
    lateinit var titleWidget: TextView

    private lateinit var unbinder: Unbinder

    private var mobilePlanInMB = 0

    private var usedData = 0.0

    private var precision = DecimalFormat("0.00")


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_daily, container, false)

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        unbinder = ButterKnife.bind(this, view)
        getFromIntent()
        initPlan()
        initProgressBar()

        HollyViewPagerBus.registerScrollView(context, observableScrollView)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unbinder.unbind()
    }

    private fun initPlan() {
        mobilePlanInMB = Prefs.getInt(MainActivity.DATA_LIMIT, 0)
    }

    private fun initProgressBar() {
        val textAdapter = MyProgressTextAdapter()
        textAdapter.setView(this)

        circularProgressBar.maxProgress = mobilePlanInMB.toDouble()
        circularProgressBar.setProgressTextAdapter(textAdapter)
        circularProgressBar.setCurrentProgress(usedData)
    }

    @SuppressLint("SetTextI18n")
    private fun getFromIntent() {
        val args = arguments
        args?.let { _ ->
            val usage = args.getSerializable(USAGE_DATA) as DailyUseActivity.Usage

            usage.data?.let {
                usedData = usage.data / (1024.0 * 1024.0)
                usedDataWidget.text = "${precision.format(usage.data / (1024.0 * 1024.0))} MB"
            }

            usage.wifi?.let {
                usedWifiWidget.text = "${precision.format(usage.wifi!! / (1024.0 * 1024.0))} MB"
            }

            val title = args.getString(TITLE)
            titleWidget.text = title

        }
    }

    companion object {
        const val USAGE_DATA = "usage_data"
        private const val TITLE = "title"

        fun newInstance(instance: Int, usage: DailyUseActivity.Usage, title: String): DailyUsageFragment {
            val args = Bundle()
            args.putInt("something", instance)
            args.putSerializable(USAGE_DATA, usage)
            args.putString(TITLE, title)
            val fragment = DailyUsageFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun getDataLimit(): Double {
        return mobilePlanInMB.toDouble()
    }
}