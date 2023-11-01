package com.dilarakiraz.upschoolcapstoneproject.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dilarakiraz.upschoolcapstoneproject.R
import com.dilarakiraz.upschoolcapstoneproject.common.gone
import com.dilarakiraz.upschoolcapstoneproject.common.loadImage
import com.dilarakiraz.upschoolcapstoneproject.common.showPopup
import com.dilarakiraz.upschoolcapstoneproject.common.viewBinding
import com.dilarakiraz.upschoolcapstoneproject.common.visible
import com.dilarakiraz.upschoolcapstoneproject.data.model.response.ProductUI
import com.dilarakiraz.upschoolcapstoneproject.databinding.FragmentHomeBinding
import com.dilarakiraz.upschoolcapstoneproject.ui.categories.CategoryProductsAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private val binding by viewBinding(FragmentHomeBinding::bind)

    private val viewModel by viewModels<HomeViewModel>()

    private val saleProductsAdapter by lazy {
        SaleProductsAdapter(
            ::onProductClick,
            ::onFavoriteClick
        )
    }
    private val allProductsAdapter by lazy {
        AllProductsAdapter(
            ::onProductClick,
            ::onFavoriteClick
        )
    }
    private val categoryProductsAdapter by lazy { CategoryProductsAdapter(::onCategoryClick) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeData()

        with(binding) {
            rvSaleProducts.adapter = saleProductsAdapter
            rvAllProducts.adapter = allProductsAdapter
            rvCategoryProducts.adapter = categoryProductsAdapter
        }
    }

    private fun observeData() = with(binding) {
        with(viewModel) {
            mainState.observe(viewLifecycleOwner) { state ->
                when (state) {
                    is HomeState.Loading -> progressBar.visible()

                    is HomeState.Success -> {
                        saleProductsAdapter.submitList(state.saleProducts)
                        allProductsAdapter.submitList(state.products)
                        progressBar.gone()
                    }

                    is HomeState.User -> {
                        binding.imgProfile.loadImage(
                            state.userData.profileImageUrl,
                            R.drawable.ic_user
                        )

                        val nickname = state.userData.nickname
                        if (!nickname.isNullOrEmpty()) {
                            binding.tvNickname.text = nickname
                        }

                        val cartProductsCount = state.userData.cartProductsCount
                        binding.tvBagProductsCount.text = "$cartProductsCount"
                    }

                    is HomeState.Category -> {
                        categoryProductsAdapter.updateCategoryList(state.categoryList)

                        if (state.categoryList.isEmpty()) {
                            tvCategories.visibility = View.GONE
                        } else {
                            tvCategories.visibility = View.VISIBLE
                        }
                    }

                    is HomeState.ProductsByCategory -> {
                        allProductsAdapter.submitList(state.productsByCategory)
                    }

                    is HomeState.Error -> {
                        showPopup(state.throwable.message)
                        progressBar.gone()
                    }

                    is HomeState.EmptyScreen -> {
                        progressBar.gone()
                    }
                }
            }
        }
    }

    private fun onProductClick(id: Int) {
        val action = HomeFragmentDirections.homeToDetail(id)
        findNavController().navigate(action)
    }

    private fun onFavoriteClick(product: ProductUI) {
        viewModel.setFavoriteState(product)
    }

    private fun onCategoryClick(category: String) {
        viewModel.getProductsByCategory(category)
    }
}