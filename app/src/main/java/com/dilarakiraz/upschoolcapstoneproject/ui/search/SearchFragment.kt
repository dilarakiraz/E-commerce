package com.dilarakiraz.upschoolcapstoneproject.ui.search

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
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
import com.dilarakiraz.upschoolcapstoneproject.databinding.FragmentSearchBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_search) {

    private val binding by viewBinding(FragmentSearchBinding::bind)

    private val viewModel: SearchViewModel by viewModels()

    private val searchProductsAdapter by lazy {
        SearchProductsAdapter(
            ::onProductClick,
            ::onFavoriteClick
        )
    }

    private val requestCodeSpeechInput = 50

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            rvSearchProducts.adapter = searchProductsAdapter

            searchView.setOnQueryTextListener(object :
                androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean = false

                override fun onQueryTextChange(newText: String?): Boolean {
                    if ((newText?.length ?: 0) >= 3) {
                        viewModel.searchProduct(newText)
                    }
                    return false
                }
            })

            icVoice.setOnClickListener {
                initSpeech()
            }
        }
        observeData()
    }

    private fun observeData() = with(binding) {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is SearchState.Loading -> progressBar.visible()

                is SearchState.Success -> {
                    searchProductsAdapter.submitList(state.products)
                    progressBar.gone()
                }

                is SearchState.Error -> {
                    showPopup(state.throwable.message)
                    progressBar.gone()
                }

                is SearchState.EmptyScreen -> progressBar.gone()
            }
        }
    }

    private fun onProductClick(id: Int) {
        val action = SearchFragmentDirections.searchToDetail(id)
        findNavController().navigate(action)
    }

    private fun onFavoriteClick(product: ProductUI) {
        viewModel.setFavoriteState(product)
    }

    private fun initSpeech() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.prompt_text))
        }
        try {
            startActivityForResult(intent, requestCodeSpeechInput)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == requestCodeSpeechInput && resultCode == Activity.RESULT_OK && data != null) {
            val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!result.isNullOrEmpty()) {
                val query = result[0]
                binding.searchView.setQuery(query, true)
                viewModel.searchProduct(query)
            }
        }
    }
}