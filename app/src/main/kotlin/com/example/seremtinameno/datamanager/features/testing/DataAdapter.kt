package com.example.seremtinameno.datamanager.features.testing

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.seremtinameno.datamanager.R
import com.example.seremtinameno.datamanager.features.daily.models.Usage
import com.kd.dynamic.calendar.generator.ImageGenerator
import kotlinx.android.synthetic.main.item_daily.view.*
import java.util.*

class DataAdapter(private val data: ArrayList<Usage>, private val imageGenerator: ImageGenerator): RecyclerView.Adapter<DataAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_daily, parent, false)

        return ViewHolder(view, imageGenerator)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dailyInfo = data[position]
        holder.bind(dailyInfo)
    }



    class ViewHolder(itemView: View, private val imageGenerator: ImageGenerator) : RecyclerView.ViewHolder(itemView) {
//        private val text = itemView.text as TextView
        private val calendarIcon = itemView.calendarIcon as ImageView

        fun bind(dayData: Usage) {
//            text.text = dayData.data.toString()
            setupCalendarIcon()

            calendarIcon.setImageBitmap(imageGenerator.generateDateImage(Calendar.getInstance(),
                    R.drawable.empty_calendar))
        }

        private fun setupCalendarIcon() {
            imageGenerator.setIconSize(30, 30)

            imageGenerator.setDateSize(30f)
            imageGenerator.setMonthSize(10f)

            imageGenerator.setDatePosition(42)
            imageGenerator.setMonthPosition(14)

            imageGenerator.setDateColor(Color.parseColor("#3F0B4A"))
            imageGenerator.setMonthColor(Color.WHITE)
        }
    }
}