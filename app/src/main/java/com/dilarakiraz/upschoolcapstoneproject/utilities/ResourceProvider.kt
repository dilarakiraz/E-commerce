package com.dilarakiraz.upschoolcapstoneproject.utilities

import android.content.Context

class ResourceProvider(private val context: Context) {
    operator fun invoke(resourceId: Int): String {
        return context.getString(resourceId)
    }
}
