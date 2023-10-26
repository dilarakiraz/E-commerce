package com.dilarakiraz.upschoolcapstoneproject.ui.profile

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.dilarakiraz.upschoolcapstoneproject.R
import com.dilarakiraz.upschoolcapstoneproject.common.Resource
import com.dilarakiraz.upschoolcapstoneproject.common.viewBinding
import com.dilarakiraz.upschoolcapstoneproject.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
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
    private var selectedImageUri: Uri? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileViewModel.performProfileAction(selectedImageUri)
        observeUserProfile()

        with(binding) {
            imgProfile.setOnClickListener { selectImageFromGallery() }
            btnSignOut.setOnClickListener { showSignOutDialog() }
        }
    }

    private fun observeUserProfile() = with(binding) {
        profileViewModel.userProfile.observe(viewLifecycleOwner) { userProfile ->
            when (userProfile) {
                is Resource.Success -> {
                    val document = userProfile.data
                    val userNickname = document.getString("nickname")
                    val userPhoneNumber = document.getString("phone_number")
                    val profileImageUrl = document.getString("profileImageUrl")

                    tvNickname.text = userNickname
                    tvPhoneNumber.text = userPhoneNumber

                    val userEmail = FirebaseAuth.getInstance().currentUser?.email
                    tvEmail.text = userEmail

                    if (profileImageUrl != null) {
                        Glide.with(root)
                            .load(profileImageUrl)
                            .into(imgProfile)
                    }
                }

                is Resource.Error -> {
                    userProfile.throwable.message ?: "Bir hata oluştu."
                }

                is Resource.Fail -> {

                }
            }
        }
    }

    private fun showSignOutDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Çıkış Yap")
            .setMessage("Oturumu kapatmak istediğinize emin misiniz?")
            .setPositiveButton("Evet") { _, _ ->
                FirebaseAuth.getInstance().signOut()
                findNavController().navigate(R.id.profileToSignIn)
            }
            .setNegativeButton("Hayır") { _, _ -> }
            .show()
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val selectedImageUri = data?.data
            binding.imgProfile.setImageURI(selectedImageUri)
            profileViewModel.performProfileAction(selectedImageUri)
        }
    }

    private fun selectImageFromGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK)
        galleryIntent.type = "image/*"
        galleryLauncher.launch(galleryIntent)
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
            }
        }
    }
}
