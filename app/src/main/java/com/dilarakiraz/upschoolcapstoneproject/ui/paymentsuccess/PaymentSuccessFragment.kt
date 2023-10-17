package com.dilarakiraz.upschoolcapstoneproject.ui.paymentsuccess

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dilarakiraz.upschoolcapstoneproject.R
import com.dilarakiraz.upschoolcapstoneproject.common.Resource
import com.dilarakiraz.upschoolcapstoneproject.common.showSnackBar
import com.dilarakiraz.upschoolcapstoneproject.common.viewBinding
import com.dilarakiraz.upschoolcapstoneproject.databinding.FragmentPaymentSuccessBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created on 17.10.2023
 * @author Dilara Kiraz
 */

@AndroidEntryPoint
class PaymentSuccessFragment : Fragment(R.layout.fragment_payment_success) {

    private val binding by viewBinding(FragmentPaymentSuccessBinding::bind)

    private val paymentSuccessViewModel: PaymentSuccessViewModel by viewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            btnContinueShopping.setOnClickListener {
                paymentSuccessViewModel.clearCart()
            }
        }
        init()
    }

    private fun init() {
        paymentSuccessViewModel.result.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> findNavController().navigate(R.id.successToHome)
                is Resource.Error -> requireView().showSnackBar("Something went wrong")
                is Resource.Fail -> Unit
            }
        }
    }
}