package com.example.seremtinameno.datamanager.features.intro

import agency.tango.materialintroscreen.MaterialIntroActivity
import agency.tango.materialintroscreen.MessageButtonBehaviour
import agency.tango.materialintroscreen.SlideFragmentBuilder
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.Toast
import com.example.seremtinameno.datamanager.R
import com.example.seremtinameno.datamanager.core.helpers.ColorParser
import es.dmoral.toasty.Toasty

class IntroActivity: MaterialIntroActivity() {

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        enableLastSlideAlphaExitTransition(true)

        backButtonTranslationWrapper
                .setEnterTranslation { view, percentage -> view.alpha = percentage }

        addSlide(SlideFragmentBuilder()
                .backgroundColor(ColorParser.parse(this, "white"))
                .buttonsColor(ColorParser.parse(this, "green"))
                .title("Welcome screen")
                .description("description description description description description " +
                        "description description description description description description description " +
                        "description description description description description description " +
                        "description description description description description " +
                        "description description description description ")
                .build(),
                MessageButtonBehaviour(View.OnClickListener {
                    Toasty.success(this, "Something happened", Toast.LENGTH_SHORT, true).show()
                }, "Something")
        )

        addSlide(SlideFragmentBuilder()
                .backgroundColor(ColorParser.parse(this, "purple"))
                .buttonsColor(ColorParser.parse(this, "green"))
                .title("Welcome screen")
                .description("description description description description description " +
                        "description description description description description description description " +
                        "description description description description description description " +
                        "description description description description description " +
                        "description description description description ")
                .build(),
                MessageButtonBehaviour(View.OnClickListener {
                    Toasty.success(this, "Something happened", Toast.LENGTH_SHORT, true).show()
                }, "Something")
        )

        addSlide(SlideFragmentBuilder()
                .backgroundColor(R.color.green)
                .buttonsColor(R.color.grey)
                .title("That's it")
                .description("Would you join us?")
                .build())
    }

    override fun onFinish() {
        super.onFinish()
        Toasty.success(this, "Finished", Toast.LENGTH_SHORT, true).show()
    }

    companion object {
        fun getCallingIntent(context: Context): Intent {
            return Intent(context, IntroActivity::class.java)
        }
    }
}