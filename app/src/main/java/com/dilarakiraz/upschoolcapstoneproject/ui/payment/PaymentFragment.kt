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
import com.google.android.material.button.MaterialButton
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
                    val text = s.toString()
                    if (!text.isBlank()) {
                        val upperText = text.toUpperCase()
                        if (text != upperText) {
                            if (text != upperText) {
                                etCardholderName.setText(upperText)
                                etCardholderName.setSelection(upperText.length)
                            }
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
                    val cleanText = s.toString().replace(" ", "")
                    val formattedText = buildString {
                        for (i in cleanText.indices) {
                            if (i % 4 == 0 && i > 0) {
                                append(" ") // Her 4 karakterden sonra boşluk ekler
                            }
                            append(cleanText[i])
                        }
                    }
                    if (formattedText != s.toString()) {
                        etCreditCardNumber.removeTextChangedListener(this)
                        etCreditCardNumber.setText(formattedText)
                        etCreditCardNumber.setSelection(formattedText.length)
                        etCreditCardNumber.addTextChangedListener(this)
                    }

                    // Kart numarasının 16 haneyi geçmemesi
                    if (formattedText.length > 19) {
                        etCreditCardNumber.removeTextChangedListener(this)
                        etCreditCardNumber.setText(formattedText.substring(0, 19))
                        etCreditCardNumber.setSelection(19)
                        etCreditCardNumber.addTextChangedListener(this)
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
        val cardHolderName = binding.etCardholderName.text.toString()
        val cardNumber = binding.etCreditCardNumber.text.toString().replace(" ", "")
        val expireMonth = binding.actExpireOnMonth.text.toString()
        val expireYear = binding.actExpireOnYear.text.toString()
        val cvcCode = binding.etCvcCode.text.toString()
        val address = binding.etAddress.text.toString()

        if (cardHolderName.isBlank() || cardNumber.length != 16 || expireMonth.isEmpty() ||
            expireYear.isEmpty() || cvcCode.length != 3 || address.isBlank()
        ) {
            return false
        }
        return true
    }

    private fun successFragment() {
        findNavController().navigate(R.id.paymentToPaymentSuccess)
    }

    private fun showErrorMessage() {
        Snackbar.make(requireView(), "Lütfen geçerli bilgileri girin", Snackbar.LENGTH_SHORT).show()
    }
}