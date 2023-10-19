package com.dilarakiraz.upschoolcapstoneproject.ui.profile

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dilarakiraz.upschoolcapstoneproject.R
import com.dilarakiraz.upschoolcapstoneproject.common.Resource
import com.dilarakiraz.upschoolcapstoneproject.common.viewBinding
import com.dilarakiraz.upschoolcapstoneproject.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created on 19.10.2023
 * @author Dilara Kiraz
 */

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private val binding by viewBinding(FragmentProfileBinding::bind)

    private val profileViewModel: ProfileViewModel by viewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileViewModel.userProfile.observe(viewLifecycleOwner) { userProfile ->
            when (userProfile) {
                is Resource.Success -> {
                    val document = userProfile.data
                    val userNickname = document.getString("nickname")
                    val userPhoneNumber = document.getString("phone_number")

                    binding.tvNickname.text = userNickname
                    binding.tvPhoneNumber.text = userPhoneNumber

                    val userEmail = FirebaseAuth.getInstance().currentUser?.email
                    if (!userEmail.isNullOrBlank()) {
                        binding.tvEmail.text = userEmail
                    }

                    binding.btnSignOut.setOnClickListener {
                        AlertDialog.Builder(requireContext())
                            .setTitle("Çıkış Yap")
                            .setMessage("Oturumu kapatmak istediğinize emin misin?")
                            .setPositiveButton("Evet") { _, _ ->
                                FirebaseAuth.getInstance().signOut()
                                findNavController().navigate(R.id.profileToSignIn)
                            }
                            .setNegativeButton("Hayır") { _, _ ->
                            }.show()
                    }
                }

                is Resource.Error -> {}

                else -> {}
            }
        }
    }
}
