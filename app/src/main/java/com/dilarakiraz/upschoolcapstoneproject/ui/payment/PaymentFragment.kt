package com.dilarakiraz.upschoolcapstoneproject.ui.payment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dilarakiraz.upschoolcapstoneproject.R
import com.dilarakiraz.upschoolcapstoneproject.common.viewBinding
import com.dilarakiraz.upschoolcapstoneproject.databinding.FragmentPaymentBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created on 17.10.2023
 * @author Dilara Kiraz
 */

@AndroidEntryPoint
class PaymentFragment : Fragment(R.layout.fragment_payment) {

    private val binding by viewBinding(FragmentPaymentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            btnPayNow.setOnClickListener {
                if (isInputValid()) {
                    successFragment()
                } else {
                    showErrorMessage()
                }
            }

            imgBack.setOnClickListener {
                findNavController().navigateUp()
            }

            etCardholderName.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    s?.let { editable ->
                        val newText = editable.toString().toUpperCase()
                        if (editable.toString() != newText) {
                            editable.replace(0, editable.length, newText, 0, newText.length)
                            etCardholderName.setSelection(newText.length)
                        }
                    }
                }
            })

            etCreditCardNumber.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    s?.let { editable ->
                        val cleanText = editable.toString().replace(" ", "")
                        val formattedText = cleanText.chunked(4)
                            .joinToString(" ") // Her 4 karakterden sonra boşluk ekle

                        if (formattedText != editable.toString()) {
                            editable.replace(
                                0,
                                editable.length,
                                formattedText,
                                0,
                                formattedText.length
                            )
                        }

                        if (formattedText.length > 19) { // Kart numarasının 16 haneyi geçmesin
                            editable.replace(
                                0,
                                editable.length,
                                formattedText.substring(0, 19),
                                0,
                                19
                            )
                        }
                    }
                }
            })

            val monthList =
                arrayOf("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12")
            val monthAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                monthList
            )
            actExpireOnMonth.setAdapter(monthAdapter)

            val yearList = (2023..2030).map { it.toString() }
            val yearAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                yearList
            )
            actExpireOnYear.setAdapter(yearAdapter)
        }
    }

    private fun isInputValid(): Boolean {
        with(binding) {
            return etCardholderName.text.toString().isNotBlank() &&
                    etCreditCardNumber.text.toString().replace(" ", "").length == 16 &&
                    actExpireOnMonth.text.isNotBlank() &&
                    actExpireOnYear.text.isNotBlank() &&
                    etCvcCode.text.toString().length == 3 &&
                    etAddress.text.toString().isNotBlank()
        }
    }

    private fun successFragment() {
        findNavController().navigate(R.id.paymentToPaymentSuccess)
    }

    private fun showErrorMessage() {
        Snackbar.make(requireView(), R.string.enter_valid_information, Snackbar.LENGTH_SHORT).show()
    }
}