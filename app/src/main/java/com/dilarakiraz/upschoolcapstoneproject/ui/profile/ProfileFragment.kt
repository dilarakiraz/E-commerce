package com.dilarakiraz.upschoolcapstoneproject.ui.profile

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dilarakiraz.upschoolcapstoneproject.Manifest
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

    private val REQUEST_IMAGE_PICK = 101

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

                    binding.imgProfile.setOnClickListener {
                        checkGalleryPermissionAndOpenPicker()
                    }
                }

                is Resource.Error -> {
                    // Hata işlemleri burada
                }

                else -> {}
            }
        }
    }

    private fun checkGalleryPermissionAndOpenPicker() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // İzin verilmedi, izin iste
            requestPermissions(
                arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                REQUEST_IMAGE_PICK
            )
        } else {
            // İzin zaten verilmiş, galeriye git
            openImagePicker()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_IMAGE_PICK) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // İzin verildi, galeriye git
                openImagePicker()
            } else {
                // İzin reddedildi, kullanıcıya bir açıklama gösterebilirsiniz
                // Örneğin, bir Snackbar veya AlertDialog kullanarak
            }
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri = data.data
            // Seçilen resmi kullanın veya yükleyin
            // Örneğin, Firebase Storage'a yükleyebilir veya ImageView'ınızı güncelleyebilirsiniz.
        }
    }
}
