package com.dilarakiraz.upschoolcapstoneproject.utilities

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

/**
 * Created on 4.11.2023
 * @author Dilara Kiraz
 */

class PhoneTextWatcher (private val editText: EditText) : TextWatcher {
    private var isRunning: Boolean = false

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (isRunning) {
            return
        }

        var cursorPosition = start + count
        val digits = s.filter(Char::isDigit)
            .dropWhile { it == '0' }
            .take(10)
        cursorPosition -= s.take(cursorPosition).run {
            count { !it.isDigit() } + filter(Char::isDigit).takeWhile { it == '0' }.count()
        }

        val output = StringBuilder(digits)

        fun punctuate(position: Int, punctuation: String) {
            if (digits.length > position) {
                output.insert(position, punctuation)
                if (cursorPosition > position) {
                    cursorPosition += punctuation.length
                }
            }
        }

        punctuate(8, " ")
        punctuate(6, " ")
        punctuate(3, ") ")
        punctuate(0, "(")

        isRunning = true
        editText.setText(output)
        editText.setSelection(cursorPosition.coerceAtMost(output.length))
        isRunning = false
    }

    override fun afterTextChanged(s: Editable) {
    }
}