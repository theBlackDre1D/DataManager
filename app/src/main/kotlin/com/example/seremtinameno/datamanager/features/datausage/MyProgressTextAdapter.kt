package com.example.seremtinameno.datamanager.features.datausage

import antonkozyriatskyi.circularprogressindicator.CircularProgressIndicator
import java.text.DecimalFormat

class MyProgressTextAdapter: CircularProgressIndicator.ProgressTextAdapter  {

    private lateinit var view:      View

    private var precision =         DecimalFormat("0.00")


    fun setView(view: View) {
        this.view = view
    }

    override fun formatText(progress: Double): String {
        val dataLimit = view.getDataLimit()
        val percentage = calculatePercentage(progress, dataLimit)

        return "${precision.format(percentage)} %"
    }

    private fun calculatePercentage(used: Double, mobilePlanInMB: Double): Double {
        return (used/mobilePlanInMB) * 100.0
    }

    interface View {
        fun getDataLimit(): Double
    }
}