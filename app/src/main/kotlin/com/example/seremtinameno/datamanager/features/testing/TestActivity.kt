package com.example.seremtinameno.datamanager.features.testing

import android.content.Context
import android.content.Intent
import android.os.Bundle
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

class TestActivity: BaseActivity() {

    private val appComponent: ApplicationComponent by lazy(mode = LazyThreadSafetyMode.NONE) {
        (application as AndroidApplication).appComponent
    }

    @BindView(R.id.picker)
    lateinit var picker:        DiscreteScrollView

    private val data =          ArrayList<Usage>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        appComponent.inject(this)
        ButterKnife.bind(this)

        for (i in 0..10) {
            val newUsage = Usage(45323678L, 451987L)
            data.add(newUsage)
        }

        setupDiscreteSW()
    }

    private fun setupDiscreteSW() {
        picker.adapter = DataAdapter(data, ImageGenerator(this))

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

    companion object {
        fun getCallingIntent(context: Context): Intent {
            return Intent(context, TestActivity::class.java)
        }
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