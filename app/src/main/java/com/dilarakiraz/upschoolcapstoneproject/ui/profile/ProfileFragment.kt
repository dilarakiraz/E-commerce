package com.dilarakiraz.upschoolcapstoneproject.ui.profile

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
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

    private val REQUEST_CODE = 1

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

//                    binding.imgProfile.setOnClickListener {
//                        requestGalleryPermission()
//                    }
                    binding.imgProfile.setOnClickListener { selectImageFromGallery() }
                }

                is Resource.Error -> {}

                else -> {}
            }
        }
    }

    private fun selectImageFromGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK)
        galleryIntent.type = "image/*"
        startActivityForResult(galleryIntent, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { selectedImageUri ->
                binding.imgProfile.setImageURI(selectedImageUri)
            }
        }
    }

    private fun requestGalleryPermission() {
        val permission = Manifest.permission.READ_EXTERNAL_STORAGE
        val granted = PackageManager.PERMISSION_GRANTED
        if (ContextCompat.checkSelfPermission(requireContext(), permission) != granted) {
            AlertDialog.Builder(requireContext())
                .setTitle("Galeri Erişim İzni")
                .setMessage("Profil resmi seçmek için galeriye erişim izni gereklidir. İzin vermek ister misiniz?")
                .setPositiveButton("Evet") { _, _ ->
                    val permissions = arrayOf(permission)
                    requestPermissions(permissions, REQUEST_CODE)
                }
                .setNegativeButton("Hayır") { _, _ ->
                }
                .show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectImageFromGallery()
            } else {}
        }
    }
}
