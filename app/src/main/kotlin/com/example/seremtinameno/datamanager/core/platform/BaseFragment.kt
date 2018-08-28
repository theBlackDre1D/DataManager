
package com.example.seremtinameno.datamanager.core.platform

import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.ButterKnife
import com.example.seremtinameno.datamanager.AndroidApplication
import com.example.seremtinameno.datamanager.core.di.ApplicationComponent
//import com.example.seremtinameno.datamanager.core.extension.viewContainer
import kotlinx.android.synthetic.main.progress.*
//import kotlinx.android.synthetic.main.toolbar.progress
import javax.inject.Inject

/**
 * Base Fragment class with helper methods for handling views and back button events.
 *
 * @see Fragment
 */
abstract class BaseFragment : Fragment() {

    abstract fun layoutId(): Int

    val appComponent: ApplicationComponent by lazy(mode = LazyThreadSafetyMode.NONE) {
        (activity?.application as AndroidApplication).appComponent
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(layoutId(), container, false)

    open fun onBackPressed() {}

    internal fun firstTimeCreated(savedInstanceState: Bundle?) = savedInstanceState == null

    internal fun showProgress() = progressStatus(View.VISIBLE)

    internal fun hideProgress() = progressStatus(View.GONE)

    private fun progressStatus(viewStatus: Int) =
            with(activity) { if (this is BaseActivity) this.progress.visibility = viewStatus }

//    fun butterKnifeInject(fragment: BaseFragment) {
//        ButterKnife.bind(fragment)
//    }

//    internal fun notify(@StringRes message: Int) =
//            Snackbar.make(viewContainer, message, Snackbar.LENGTH_SHORT).show()

//    internal fun notifyWithAction(@StringRes message: Int, @StringRes actionText: Int, action: () -> Any) {
//        val snackBar = Snackbar.make(viewContainer, message, Snackbar.LENGTH_INDEFINITE)
//        snackBar.setAction(actionText) { _ -> action.invoke() }
//        snackBar.setActionTextColor(ContextCompat.getColor(appContext,
//                color.colorTextPrimary))
//        snackBar.show()
//    }
}
