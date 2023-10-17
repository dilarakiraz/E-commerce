package com.dilarakiraz.upschoolcapstoneproject.ui.cart

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dilarakiraz.upschoolcapstoneproject.R
import com.dilarakiraz.upschoolcapstoneproject.common.gone
import com.dilarakiraz.upschoolcapstoneproject.common.showPopup
import com.dilarakiraz.upschoolcapstoneproject.common.viewBinding
import com.dilarakiraz.upschoolcapstoneproject.common.visible
import com.dilarakiraz.upschoolcapstoneproject.data.model.response.ProductUI
import com.dilarakiraz.upschoolcapstoneproject.databinding.FragmentCartBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CartFragment : Fragment(R.layout.fragment_cart) {
    private val binding by viewBinding(FragmentCartBinding::bind)

    private val viewModel by viewModels<CartViewModel>()

    private val cartProductsAdapter by lazy {
        CartProductsAdapter(
            ::onProductClick,
            ::onDeleteClick,
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getCartProducts()

        with(binding) {
            rvCartProducts.adapter = cartProductsAdapter

            tvClear.setOnClickListener {
                viewModel.clearCart()
            }
            btnBuyNow.setOnClickListener {
                findNavController().navigate(R.id.cartToPayment)
            }
        }

        with(viewModel){
            cartProductsAdapter.onIncreaseClick = {
                increase(it)
            }
            cartProductsAdapter.onDecreaseClick= {
                decrease(it)
            }
        }
        observeData()
    }

    private fun observeData() = with(binding) {
        viewModel.cartState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is CartState.Loading -> {
                    progressBar.visible()
                }

                is CartState.Success -> {
                    cartProductsAdapter.submitList(state.products)
                    progressBar.gone()
                }

                is CartState.Error -> {
                    showPopup(state.throwable.message)
                    progressBar.gone()
                }

                is CartState.EmptyScreen -> {
                    progressBar.gone()
                }

                else -> {}
            }
        }

        viewModel.updatedCart.observe(viewLifecycleOwner) { updatedCart ->
            cartProductsAdapter.submitList(updatedCart)
            updateTotalAmount(updatedCart)
        }
    }

    private fun updateTotalAmount(updatedCart: List<ProductUI>?) {
        var totalAmount = 0.0

        updatedCart?.let { cart ->
            for (product in cart) {
                totalAmount += product.price
            }
        }

        val totalAmountText = String.format("%.3f₺", totalAmount)
        binding.tvTotalAmount.text = totalAmountText
    }

    private fun onProductClick(id: Int) {
        val action = CartFragmentDirections.cartToDetail(id)
        findNavController().navigate(action)
    }

    private fun onDeleteClick(id: Int) {
        viewModel.deleteProductFromCart(id)
    }
}