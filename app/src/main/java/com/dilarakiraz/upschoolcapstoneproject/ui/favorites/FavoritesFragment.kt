package com.dilarakiraz.upschoolcapstoneproject.ui.favorites

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
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

    private val binding by viewBinding(FragmentFavoritesBinding::bind)

    private val viewModel: FavoritesViewModel by viewModels()

    private val favoritesAdapter by lazy { FavoritesAdapter(::onProductClick, ::onDeleteClick) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getFavorites()

        with(binding) {
            rvFavorites.adapter = favoritesAdapter

            ivAllDelete.setOnClickListener {
                viewModel.clearAllFavorites()
            }
        }
        initObservers()
    }

    private fun initObservers() = with(binding) {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is FavoritesState.Loading -> progressBar.visible()

                is FavoritesState.Success -> {
                    favoritesAdapter.submitList(state.favoriteProducts)
                    ivEmpty.gone()
                    tvEmpty.gone()
                    progressBar.gone()
                }

                is FavoritesState.Error -> {
                    showPopup(state.throwable.message)
                    progressBar.gone()
                }

                is FavoritesState.EmptyData -> {
                    progressBar.gone()
                }

                is FavoritesState.EmptyScreen -> {
                    progressBar.gone()
                    ivEmpty.visible()
                    tvEmpty.visible()
                    rvFavorites.gone()
                    tvEmpty.text = state.message
                }
            }
        }
    }

    private fun onProductClick(id: Int) {
        val action = FavoritesFragmentDirections.favoritesToDetail(id)
        val navOptions = NavOptions.Builder()
            .setLaunchSingleTop(true)
            .setEnterAnim(R.anim.fade_in)
            .setExitAnim(R.anim.fade_out)
            .setPopEnterAnim(R.anim.fade_in)
            .setPopExitAnim(R.anim.fade_out)
            .build()

        findNavController().navigate(action, navOptions)
    }

    private fun onDeleteClick(product: ProductUI) {
        viewModel.deleteFromFavorites(product)
    }
}