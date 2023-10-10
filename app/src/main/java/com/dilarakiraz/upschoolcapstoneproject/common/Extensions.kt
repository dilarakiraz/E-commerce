package com.dilarakiraz.upschoolcapstoneproject.common

import android.graphics.Paint
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide


fun View.visible() {
    this.visibility = View.VISIBLE
}

fun setViewsVisible(vararg views: View) = views.forEach {
    it.visible()
}

fun View.gone() {
    this.visibility = View.GONE
}

fun TextView.setStrikeThrough() {
    paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
}

fun ImageView.loadImage(url: String?) {
    Glide.with(this.context).load(url).into(this)
}
