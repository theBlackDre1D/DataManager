package com.example.seremtinameno.datamanager.features.testing

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.example.seremtinameno.datamanager.R
import com.example.seremtinameno.datamanager.features.daily.models.Usage
import com.pedrogomez.renderers.Renderer

class DataRenderer(data: ArrayList<Usage>): Renderer<Usage>() {

    @BindView(R.id.text)
    lateinit var text: TextView

    override fun inflate(inflater: LayoutInflater?, parent: ViewGroup?): View {
        val view = inflater?.inflate(R.layout.item_daily, parent, false)
        ButterKnife.bind(this, view!!)

        return view
    }

    override fun hookListeners(rootView: View?) {

    }

    override fun render() {
        val usage = content
        text.text = usage.data.toString()
    }

    override fun setUpView(rootView: View?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}