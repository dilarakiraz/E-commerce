package com.dilarakiraz.upschoolcapstoneproject.ui.signin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dilarakiraz.upschoolcapstoneproject.R
import com.dilarakiraz.upschoolcapstoneproject.common.gone
import com.dilarakiraz.upschoolcapstoneproject.common.viewBinding
import com.dilarakiraz.upschoolcapstoneproject.common.visible
import com.dilarakiraz.upschoolcapstoneproject.databinding.FragmentSignInBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignInFragment : Fragment(R.layout.fragment_sign_in) {

    private val binding by viewBinding(FragmentSignInBinding::bind)

    private val viewModel: SignInViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding){

            btnSignIn.setOnClickListener{
                val email =etEmail.text.toString()
                val password = etPassword.text.toString()

                viewModel.checkInfo(email,password)
            }

            ivGoToSignUp.setOnClickListener{
                findNavController().navigate(R.id.signInToSignUp)
            }

            btnSignUp.setOnClickListener {
                findNavController().navigate(R.id.signInToSignUp)
            }
        }
        initObservers()
    }

    private fun initObservers() = with(binding){
        viewModel.state.observe(viewLifecycleOwner) {state->
            when(state){
                SignInState.Loading -> progressBar.visible()
                SignInState.GoToHome -> findNavController().navigate(R.id.signInToHome)

                is SignInState.Error -> {
                   progressBar.gone()
                    showErrorMessage(state.throwable.message ?: "Unknown Error")
                }
            }
        }
    }

    private fun showErrorMessage(message: String){
        AlertDialog.Builder(requireContext())
            .setTitle("Error")
            .setMessage(message)
            .setPositiveButton("OK"){ dialog, _ ->
                dialog.dismiss()
            }.show()
    }
}