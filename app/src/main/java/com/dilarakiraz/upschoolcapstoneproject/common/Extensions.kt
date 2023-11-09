package com.dilarakiraz.upschoolcapstoneproject.common

import android.app.Dialog
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.util.Patterns
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.dilarakiraz.upschoolcapstoneproject.databinding.DialogPopUpBinding
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout


fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun setViewsVisible(vararg views: View) = views.forEach {
    it.visible()
}

fun View.showSnackBar(message: String, duration: Int = Snackbar.LENGTH_SHORT) {
    Snackbar.make(this, message, duration).show()
}

fun View.gone() {
    this.visibility = View.GONE
}

fun setViewsGone(vararg views: View) = views.forEach {
    it.gone()
}

fun TextView.setStrikeThrough() {
    paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
}

fun ImageView.loadImage(url: String?, placeholder: Int? = null) {
    if (placeholder != null) {
        Glide.with(this.context).load(url).placeholder(placeholder).into(this)
    } else {
        Glide.with(this.context).load(url).into(this)
    }
}

fun TextInputEditText.isValidEmail(errorString: String): Boolean {
    val textInputLayout = this.parent.parent as TextInputLayout
    return if (Patterns.EMAIL_ADDRESS.matcher(text.toString()).matches()) {
        textInputLayout.isErrorEnabled = false
        true
    } else {
        textInputLayout.error = errorString
        false
    }
}

fun Fragment.showPopup(
    errorMsg: String? = null,
    okayButtonListener: (() -> Unit)? = null,
) {
    val dialog = Dialog(requireContext())
    val binding = DialogPopUpBinding.inflate(dialog.layoutInflater, null, false)
    dialog.setContentView(binding.root)
    dialog.setWidthPercent(80)
    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog.setCancelable(false)
    dialog.setCanceledOnTouchOutside(false)

    binding.tvError.text = errorMsg

    binding.btnOkay.setOnClickListener {
        if (!requireActivity().isFinishing) {
            dialog.dismiss()
        }
        okayButtonListener?.invoke()
    }

    if (!requireActivity().isFinishing) {
        dialog.show()
    }
}

fun Dialog.setWidthPercent(percentage: Int) {
    val percent = percentage.toFloat() / 100
    val dm = Resources.getSystem().displayMetrics
    val rect = dm.run { Rect(0, 0, widthPixels, heightPixels) }
    val percentWidth = rect.width() * percent
    window?.setLayout(percentWidth.toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
}