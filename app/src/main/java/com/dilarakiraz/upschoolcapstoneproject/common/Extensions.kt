package com.dilarakiraz.upschoolcapstoneproject.common

import android.view.View


fun View.visible() {
    this.visibility = View.VISIBLE
}

fun setViewsVisible(vararg views: View) = views.forEach {
    it.visible()
}

fun View.gone() {
    this.visibility = View.GONE
}
