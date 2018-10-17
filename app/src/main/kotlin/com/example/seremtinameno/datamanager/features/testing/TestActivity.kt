package com.example.seremtinameno.datamanager.features.testing

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.example.seremtinameno.datamanager.R
import com.example.seremtinameno.datamanager.core.AndroidApplication
import com.example.seremtinameno.datamanager.core.di.ApplicationComponent
import com.example.seremtinameno.datamanager.core.platform.BaseActivity
import com.example.seremtinameno.datamanager.core.platform.BaseFragment
import com.example.seremtinameno.datamanager.features.daily.models.Usage
import com.kd.dynamic.calendar.generator.ImageGenerator
import com.yarolegovich.discretescrollview.DiscreteScrollView
import com.yarolegovich.discretescrollview.transform.Pivot
import com.yarolegovich.discretescrollview.transform.ScaleTransformer
import java.text.DecimalFormat

class TestActivity: BaseActivity(), DiscreteScrollView.ScrollStateChangeListener<DataAdapter.ViewHolder> {

    private val appComponent: ApplicationComponent by lazy(mode = LazyThreadSafetyMode.NONE) {
        (application as AndroidApplication).appComponent
    }

    @BindView(R.id.picker)
    lateinit var picker:        DiscreteScrollView

    @BindView(R.id.dataUsage)
    lateinit var dataUsage:     TextView

    @BindView(R.id.dataUnits)
    lateinit var dataUnits:     TextView

    @BindView(R.id.header)
    lateinit var header:        TextView

    private val data =          ArrayList<Usage>()

    private var precision =                 DecimalFormat("0.00")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        appComponent.inject(this)
        injectUI(this)
//        ButterKnife.bind(this)

        for (i in 0..10) {
            val newUsage = Usage(45323678L, 451987L)
            data.add(newUsage)
        }

        setupFonts()
        setupDiscreteSW()
        updateUIWithCurrentInformation()
    }

    private fun setupFonts() {
        dataUsage.typeface = basicTextFont
        header.typeface = headlineFont
    }

    private fun setupDiscreteSW() {
        picker.adapter = DataAdapter(data, ImageGenerator(this))
        picker.addScrollStateChangeListener(this)

        setItemsTransformation()
    }

    private fun setItemsTransformation() {
        picker.setItemTransformer(ScaleTransformer.Builder()
                .setMaxScale(1.05f)
                .setMinScale(0.8f)
                .setPivotX(Pivot.X.CENTER)
                .setPivotY(Pivot.Y.BOTTOM)
                .build()
        )
    }

    private fun updateUIWithCurrentInformation() {
        val actualItem = data[picker.currentItem]
        val usage = precision.format(actualItem.data!! / (1024.0 * 1024.0))
        dataUsage.text = usage.toString()

        showMessage(this, "Scroll ended")
    }

    companion object {
        fun getCallingIntent(context: Context): Intent {
            return Intent(context, TestActivity::class.java)
        }
    }

    override fun onScroll(scrollPosition: Float, currentPosition: Int, newPosition: Int, currentHolder: DataAdapter.ViewHolder?, newCurrent: DataAdapter.ViewHolder?) {
        // nothing
    }

    override fun onScrollEnd(currentItemHolder: DataAdapter.ViewHolder, adapterPosition: Int) {
        updateUIWithCurrentInformation()
    }

    override fun onScrollStart(currentItemHolder: DataAdapter.ViewHolder, adapterPosition: Int) {
        // nothing
    }

    override fun fragment(): BaseFragment {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showLoading() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun hideLoading() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}