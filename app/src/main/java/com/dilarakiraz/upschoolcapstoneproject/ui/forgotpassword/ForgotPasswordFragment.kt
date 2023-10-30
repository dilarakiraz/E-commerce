package com.dilarakiraz.upschoolcapstoneproject.ui.forgotpassword

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.dilarakiraz.upschoolcapstoneproject.R
import com.dilarakiraz.upschoolcapstoneproject.common.gone
import com.dilarakiraz.upschoolcapstoneproject.common.isValidEmail
import com.dilarakiraz.upschoolcapstoneproject.common.showSnackBar
import com.dilarakiraz.upschoolcapstoneproject.common.viewBinding
import com.dilarakiraz.upschoolcapstoneproject.common.visible
import com.dilarakiraz.upschoolcapstoneproject.databinding.FragmentForgotPasswordBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created on 27.10.2023
 * @author Dilara Kiraz
 */

@AndroidEntryPoint
class ForgotPasswordFragment : Fragment(R.layout.fragment_forgot_password) {

    private val binding by viewBinding(FragmentForgotPasswordBinding::bind)

    private val viewModel: ForgotPasswordViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            btnSend.setOnClickListener {
                if (etEmail.isValidEmail(getString(R.string.invalid_mail)))
                    viewModel.sendPasswordResetEmail(etEmail.text.toString())
            }

            signInFragment.setOnClickListener {
                it.findNavController().navigate(R.id.forgotPasswordToSignIn)
            }

            viewModel.result.observe(viewLifecycleOwner) {
                when (it) {
                    is ForgotState.Success -> {
                        requireView().showSnackBar(getString(R.string.email_sent))
                        progressBar.gone()
                    }

                    is ForgotState.ShowPopUp -> {
                        progressBar.gone()
                        requireView().showSnackBar(getString(R.string.something_went_wrong))
                    }

                    is ForgotState.Loading -> {
                        progressBar.visible()
                    }

                    else -> {
                        requireView().showSnackBar("Beklenmeyen bir durumla karşılaşıldı.")
                    }
                }
            }
        }
    }
}