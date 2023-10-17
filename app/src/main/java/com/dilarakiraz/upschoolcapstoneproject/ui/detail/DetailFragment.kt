package com.dilarakiraz.upschoolcapstoneproject.ui.detail

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.dilarakiraz.upschoolcapstoneproject.R
import com.dilarakiraz.upschoolcapstoneproject.common.gone
import com.dilarakiraz.upschoolcapstoneproject.common.loadImage
import com.dilarakiraz.upschoolcapstoneproject.common.setStrikeThrough
import com.dilarakiraz.upschoolcapstoneproject.common.showPopup
import com.dilarakiraz.upschoolcapstoneproject.common.showSnackBar
import com.dilarakiraz.upschoolcapstoneproject.common.viewBinding
import com.dilarakiraz.upschoolcapstoneproject.common.visible
import com.dilarakiraz.upschoolcapstoneproject.databinding.FragmentDetailBinding

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.tasks.await

/**
 * Created on 8.10.2023
 * @author Dilara Kiraz
 */

@AndroidEntryPoint
class DetailFragment : Fragment(R.layout.fragment_detail) {

    private val binding by viewBinding(FragmentDetailBinding::bind)

    private val viewModel by viewModels<DetailViewModel>()

    private val args by navArgs<DetailFragmentArgs>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getProductDetail(args.id)

        with(binding) {
            icDetailToHome.setOnClickListener {
                findNavController().navigate(R.id.detailToHome)
            }

            btnAddToBag.setOnClickListener {
                if (viewModel.isUserAuthenticated()) {
                    val userId = viewModel.getUserUid()
                    val productId = args.id
                    viewModel.addToCart(userId, productId, requireContext())
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Ürün eklemek için oturum açmalısınız.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        observeData()
    }


    private fun observeData() = with(binding) {
        viewModel.detailState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is DetailState.Loading -> {
                    progressBar.visible()
                }

                is DetailState.Success -> {
                    progressBar.gone()
                    val product = state.product
                    tvTitle.text = product.title
                    tvDescription.text = product.description

                    if (product.saleState) {
                        tvPrice.text = "${product.price} ₺"
                        tvPrice.setStrikeThrough()
                        tvSalePrice.text = "${product.salePrice} ₺"
                    } else {
                        tvPrice.text = "${product.price} ₺"
                        tvSalePrice.visibility = View.GONE
                    }

                    ivProduct.loadImage(product.imageOne)

                    if (product.isFavorite) {
                        ivFavorite.setImageResource(R.drawable.ic_fav)
                    } else {
                        ivFavorite.setImageResource(R.drawable.ic_unfav)
                    }

                    ivFavorite.setOnClickListener {
                        val product = viewModel.selectedProduct.value
                        if (product != null) {
                            if (!viewModel.isFavoriteUpdating()) {
                                viewModel.toggleFavorite(product)
                                if (product.isFavorite) {
                                    ivFavorite.setImageResource(R.drawable.ic_unfav)
                                    showPopup("Ürün favorilerden kaldırıldı")
                                } else {
                                    ivFavorite.setImageResource(R.drawable.ic_fav)
                                    view?.showSnackBar("Ürün favorilere eklendi")
                                }
                            }
                        }
                    }
                    ratingBar.rating = product.rate.toFloat()
                }

                is DetailState.Error -> {
                    ivError.visible()
                    tvError.visible()
                    tvError.text = state.throwable.message.orEmpty()
                    progressBar.gone()
                }

                is DetailState.EmptyScreen -> {
                }
            }
        }
    }
}
