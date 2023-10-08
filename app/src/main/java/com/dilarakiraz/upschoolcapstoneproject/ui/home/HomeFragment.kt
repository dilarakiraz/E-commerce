package com.dilarakiraz.upschoolcapstoneproject.ui.home

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dilarakiraz.upschoolcapstoneproject.R
import com.dilarakiraz.upschoolcapstoneproject.common.viewBinding
import com.dilarakiraz.upschoolcapstoneproject.databinding.FragmentHomeBinding


class HomeFragment : Fragment(R.layout.fragment_home) {

    private val binding by viewBinding(FragmentHomeBinding::bind)

    private val viewModel by viewModels<HomeViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding){

            btnOut.setOnClickListener{
                showLogoutDialog()
            }
        }
    }

    private fun showLogoutDialog(){
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Çıkış Yap!")
        builder.setMessage("Çıkış yapmak istediğinize emin misiniz?")

        builder.setPositiveButton("Evet"){ _, _ ->
            findNavController().navigate(R.id.homeToSignIn)
        }
        builder.setNegativeButton("Hayır"){ dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

}