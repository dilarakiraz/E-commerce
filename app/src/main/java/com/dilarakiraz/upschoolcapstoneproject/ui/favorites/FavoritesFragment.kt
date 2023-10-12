package com.dilarakiraz.upschoolcapstoneproject.ui.favorites

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dilarakiraz.upschoolcapstoneproject.R
import com.dilarakiraz.upschoolcapstoneproject.common.gone
import com.dilarakiraz.upschoolcapstoneproject.common.showPopup
import com.dilarakiraz.upschoolcapstoneproject.common.viewBinding
import com.dilarakiraz.upschoolcapstoneproject.common.visible
import com.dilarakiraz.upschoolcapstoneproject.data.model.response.ProductUI
import com.dilarakiraz.upschoolcapstoneproject.databinding.FragmentFavoritesBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FavoritesFragment : Fragment(R.layout.fragment_favorites) {

    private val binding by viewBinding (FragmentFavoritesBinding::bind)

    private val viewModel: FavoritesViewModel by viewModels()

    private val favoritesAdapter by lazy { FavoritesAdapter(::onProductClick, ::onDeleteClick) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getFavorites()

        with(binding){
            rvFavorites.adapter = favoritesAdapter
        }

        initObservers()
    }

    private fun initObservers() = with(binding) {
        viewModel.state.observe(viewLifecycleOwner){ state ->
            when(state) {
                is FavoritesState.Loading -> progressBar.visible()

                is FavoritesState.Success -> {
                    favoritesAdapter.submitList(state.favoriteProducts)
                    progressBar.gone()
                }

                is FavoritesState.Error -> {
                    showPopup(state.throwable.message)
                    progressBar.gone()
                }

                is FavoritesState.EmptyData -> {
                    progressBar.gone()
                }
            }
        }
    }

    private fun onProductClick(id: Int){
        val action = FavoritesFragmentDirections.favoritesToDetail(id)
        findNavController().navigate(action)
    }

    private fun onDeleteClick(product: ProductUI){
        viewModel.deleteFromFavorites(product)
    }
}