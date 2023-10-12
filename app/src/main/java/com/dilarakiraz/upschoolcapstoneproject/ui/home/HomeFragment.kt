package com.dilarakiraz.upschoolcapstoneproject.ui.home

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dilarakiraz.upschoolcapstoneproject.R
import com.dilarakiraz.upschoolcapstoneproject.common.gone
import com.dilarakiraz.upschoolcapstoneproject.common.showPopup
import com.dilarakiraz.upschoolcapstoneproject.common.viewBinding
import com.dilarakiraz.upschoolcapstoneproject.common.visible
import com.dilarakiraz.upschoolcapstoneproject.data.model.response.ProductUI
import com.dilarakiraz.upschoolcapstoneproject.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private val binding by viewBinding(FragmentHomeBinding::bind)

    private val viewModel by viewModels<HomeViewModel>()

    private val saleProductsAdapter by lazy { SaleProductsAdapter(::onProductClick, ::onFavoriteClick) }

    private val allProductsAdapter by lazy { AllProductsAdapter(::onProductClick, ::onFavoriteClick)}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding){
            btnOut.setOnClickListener{
                showLogoutDialog()
            }
            rvSaleProducts.adapter = saleProductsAdapter
            rvAllProducts.adapter = allProductsAdapter
        }

        observeData()
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

    private fun observeData() = with(binding){
        viewModel.mainState.observe(viewLifecycleOwner){state ->
            when(state){
                is HomeState.Loading -> progressBar.visible()

                is HomeState.Success -> {
                    saleProductsAdapter.submitList(state.saleProducts)
                    allProductsAdapter.submitList(state.products)
                    progressBar.gone()
                }

                is HomeState.Error -> {
                    showPopup(state.throwable.message)
                    progressBar.gone()
                }

                is HomeState.EmptyScreen -> {
                    progressBar.gone()
                }

                else -> {}
            }

        }
    }

    private fun onProductClick(id:Int){
        val action = HomeFragmentDirections.homeToDetail(id)
        findNavController().navigate(action)
    }

    private fun onFavoriteClick(product: ProductUI){
        viewModel.setFavoriteState(product)
    }

}