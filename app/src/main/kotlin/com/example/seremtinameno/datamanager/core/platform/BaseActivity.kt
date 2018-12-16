package com.example.seremtinameno.datamanager.core.platform

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import butterknife.ButterKnife
import co.zsmb.materialdrawerkt.builders.accountHeader
import co.zsmb.materialdrawerkt.builders.drawer
import co.zsmb.materialdrawerkt.builders.footer
import co.zsmb.materialdrawerkt.draweritems.badgeable.primaryItem
import co.zsmb.materialdrawerkt.draweritems.divider
import co.zsmb.materialdrawerkt.draweritems.profile.profile
import com.example.seremtinameno.datamanager.R
import com.example.seremtinameno.datamanager.core.helpers.ColorParser
import com.example.seremtinameno.datamanager.features.settings.SettingsActivity
import com.mikepenz.materialdrawer.Drawer
import es.dmoral.toasty.Toasty
import javax.inject.Inject

/**
 * Base Activity class with helper methods for handling fragment transactions and back button
 * events.
 *
 * @see AppCompatActivity
 */
abstract class BaseActivity : AppCompatActivity() {

    protected lateinit var drawer:          Drawer

    protected lateinit var basicTextFont:   Typeface

    protected lateinit var headlineFont:    Typeface

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        addFragment(savedInstanceState)
        setupFonts()
    }

    override fun onBackPressed() {
//        (supportFragmentManager.findFragmentById(
//                id.fragmentContainer) as BaseFragment).onBackPressed()
        super.onBackPressed()
    }

//    private fun addFragment(savedInstanceState: Bundle?) =
//            savedInstanceState ?: supportFragmentManager.inTransaction { add(
//                    id.fragmentContainer, fragment()) }

    fun showMessage(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun showErrorToast(context: Context, message: String) {
        Toasty.error(context, message, Toast.LENGTH_SHORT, true).show()
    }

    fun showSuccessToast(context: Context, message: String) {
        Toasty.success(context, message, Toast.LENGTH_SHORT, true).show()
    }

    fun injectUI(activity: BaseActivity) {
        ButterKnife.bind(activity)
    }

    inline fun <reified T : ViewModel> viewModel(factory: ViewModelProvider.Factory, body: T.() -> Unit): T {
        val vm = ViewModelProviders.of(this, factory)[T::class.java]
        vm.body()
        return vm
    }

    protected fun setupDrawer() {
        drawer = drawer {
            sliderBackgroundColor = ColorParser.parse(this@BaseActivity, "grey_light").toLong()
            headerViewRes = R.layout.header

            accountHeader {
                background = R.drawable.header

                profile("Michal", "user.email@gmail.com") {
                    icon = R.drawable.profile_picture_placeholder
                    textColor = ColorParser.parse(application, "black").toLong()
                }

                profile("Lukas", "lukas.shaggy@gmail.com") {
                    icon = R.drawable.profile_picture_placeholder
                    textColor = ColorParser.parse(application, "black").toLong()
                }

                profile("Vlado", "vladimir.vilhanovie@gmail.com") {
                    icon = R.drawable.profile_picture_placeholder
                    textColor = ColorParser.parse(application, "black").toLong()
                }
            }
            primaryItem("Home") {
                icon = R.drawable.home_icon

                onClick { _ ->
                    onHomePressed()
                    false
                }
            }
            primaryItem("Daily") {
                icon = R.drawable.daily_icon

                onClick { _ ->
                    onDailyPressed()
                    false
                }
            }
            primaryItem("Settings") {
                icon = R.drawable.settings_button_black
                onClick { _ ->
                    onSettingsPressed()
                    false
                }
            }

            divider {}

            footer {
                primaryItem("Source on GitHub") {
                    icon = R.drawable.github_logo
                    onClick { _ ->
                        val browserIntent = Intent(Intent.ACTION_VIEW,
                                Uri.parse("https://github.com/theBlackDre1D/" +
                                        "DataManager/tree/devel"))
                        startActivity(browserIntent)
                        false
                    }
                }
            }
        }
    }

    protected open fun onHomePressed() {
        showErrorToast(this, "Not implemented")
    }
    protected open fun onDailyPressed() {
        showErrorToast(this, "Not implemented")
    }

    protected open fun onSettingsPressed() {
        startActivity(SettingsActivity.getCallingIntent(this@BaseActivity))
    }

    protected open fun setupListeners() {
        // nothing
    }

    protected fun alreadyHere() {
        Toasty.info(this, "You are already there :)").show()
    }

    private fun setupFonts() {
        basicTextFont = Typeface.createFromAsset(assets, "fonts/FTY STRATEGYCIDE NCV.ttf")
        headlineFont = Typeface.createFromAsset(assets, "fonts/Machine Gunk.otf")
    }

//    abstract fun initUI()
    abstract fun fragment(): BaseFragment
    abstract fun showLoading()
    abstract fun hideLoading()
}
