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
import com.dilarakiraz.upschoolcapstoneproject.common.viewBinding
import com.dilarakiraz.upschoolcapstoneproject.common.visible
import com.dilarakiraz.upschoolcapstoneproject.databinding.FragmentDetailBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

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
                viewModel.addToCart(args.id)
            }

            ivFavorite.setOnClickListener {
                viewModel.toggleFavorite()
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

                    state.product.let { product ->
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

                        ivFavorite.setBackgroundResource(
                            if (product.isFavorite) R.drawable.ic_fav
                            else R.drawable.ic_unfav
                        )

                        ratingBar.rating = product.rate.toFloat()
                        tvRatingValue.text = "${product.rate}"

                        tvCount.text = "Count: ${product.count}"

                        if (state.toastMessage != null) {
                            Toast.makeText(requireContext(), state.toastMessage, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }

                is DetailState.EmptyScreen -> {
                    progressBar.gone()
                    ivEmpty.visible()
                    tvEmpty.visible()
                    tvEmpty.text = state.message
                }

                is DetailState.ShowPopUp -> {
                    progressBar.gone()
                    Snackbar.make(requireView(), state.errorMessage, 1000).show()
                }

                else -> {}
            }
        }
    }
}
