package com.dilarakiraz.upschoolcapstoneproject.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dilarakiraz.upschoolcapstoneproject.R
import com.dilarakiraz.upschoolcapstoneproject.common.loadImage
import com.dilarakiraz.upschoolcapstoneproject.common.setStrikeThrough
import com.dilarakiraz.upschoolcapstoneproject.common.visible
import com.dilarakiraz.upschoolcapstoneproject.data.model.response.ProductUI
import com.dilarakiraz.upschoolcapstoneproject.databinding.ItemSearchProductBinding

/**
 * Created on 13.10.2023
 * @author Dilara Kiraz
 */

class SearchProductsAdapter(
    private val onProductClick: (Int) -> Unit,
    private val onFavoriteClick: (ProductUI) -> Unit,
) : ListAdapter<ProductUI, SearchProductsAdapter.ProductViewHolder>(ProductDiffCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder =
        ProductViewHolder(
            ItemSearchProductBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            onProductClick,
            onFavoriteClick
        )

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) =
        holder.bind(getItem(position))

    class ProductViewHolder(
        private val binding: ItemSearchProductBinding,
        private val onProductClick: (Int) -> Unit,
        private val onFavoriteClick: (ProductUI) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: ProductUI) = with(binding) {
            tvName.text = product.title
            tvPrice.text = "${product.price} ₺"

            if (product.saleState) {
                tvSalePrice.text = "${product.salePrice} ₺"
                tvSalePrice.visible()
                tvPrice.setStrikeThrough()
            }

            if (product.isFavorite) {
                ivFavorite.setImageResource(R.drawable.ic_fav)
            } else {
                ivFavorite.setImageResource(R.drawable.ic_unfav)
            }

            ivProduct.loadImage(product.imageOne)

            root.setOnClickListener {
                onProductClick(product.id)
            }

            ivFavorite.setOnClickListener {
                onFavoriteClick(product)
            }


        }
    }

    class ProductDiffCallBack : DiffUtil.ItemCallback<ProductUI>() {
        override fun areItemsTheSame(oldItem: ProductUI, newItem: ProductUI): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ProductUI, newItem: ProductUI): Boolean {
            return oldItem == newItem
        }
    }
}