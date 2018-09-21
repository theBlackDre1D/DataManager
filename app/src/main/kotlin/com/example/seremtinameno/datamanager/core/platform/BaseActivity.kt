/**
 * Copyright (C) 2018 Fernando Cejas Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.seremtinameno.datamanager.core.platform

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import butterknife.ButterKnife
import co.zsmb.materialdrawerkt.builders.drawer
import co.zsmb.materialdrawerkt.builders.footer
import co.zsmb.materialdrawerkt.draweritems.badgeable.primaryItem
import co.zsmb.materialdrawerkt.draweritems.divider
import com.example.seremtinameno.datamanager.R
import com.example.seremtinameno.datamanager.core.helpers.ColorParser
import com.example.seremtinameno.datamanager.features.settings.SettingsActivity
import com.mikepenz.materialdrawer.Drawer
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import javax.inject.Inject

/**
 * Base Activity class with helper methods for handling fragment transactions and back button
 * events.
 *
 * @see AppCompatActivity
 */
abstract class BaseActivity : AppCompatActivity() {

    protected lateinit var drawer: Drawer

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        addFragment(savedInstanceState)
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

//            accountHeader {
//                background = ColorParser.parse(this@MainActivity, "white")
//                profile("Michal", "user.email@gmail.com") {
//                    icon = R.drawable.app_icon
//                }
//            }
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
//        drawer = newDrawer.await()
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

//    abstract fun initUI()
    abstract fun fragment(): BaseFragment
    abstract fun showLoading()
    abstract fun hideLoading()
}
