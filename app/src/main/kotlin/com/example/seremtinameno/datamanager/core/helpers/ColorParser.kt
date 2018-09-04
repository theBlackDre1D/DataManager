package com.example.seremtinameno.datamanager.core.helpers

import android.content.Context

object ColorParser {

    @JvmOverloads
    fun parse(context: Context, color: String, defaultValue: Int = android.R.color.transparent): Int {
        var parsed = context.resources.getColor(context.resources
                                .getIdentifier(color, "color", context.packageName)
                )

        if (parsed == 0) {
            parsed = defaultValue
        }

        return parsed
    }
}