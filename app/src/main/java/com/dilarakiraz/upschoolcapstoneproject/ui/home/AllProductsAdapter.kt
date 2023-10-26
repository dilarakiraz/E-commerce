package com.dilarakiraz.upschoolcapstoneproject.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dilarakiraz.upschoolcapstoneproject.R
import com.dilarakiraz.upschoolcapstoneproject.common.loadImage
import com.dilarakiraz.upschoolcapstoneproject.common.setStrikeThrough
import com.dilarakiraz.upschoolcapstoneproject.data.model.response.ProductUI
import com.dilarakiraz.upschoolcapstoneproject.databinding.ItemProductBinding

/**
 * Created on 7.10.2023
 * @author Dilara Kiraz
 */

class AllProductsAdapter(
    private val onProductClick: (Int) -> Unit,
    private val onFavoriteClick: (ProductUI) -> Unit,
) : ListAdapter<ProductUI, AllProductsAdapter.ProductViewHolder>(ProductDiffCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder =
        ProductViewHolder(
            ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            onProductClick,
            onFavoriteClick
        )

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) =
        holder.bind(getItem(position))

    class ProductViewHolder(
        private val binding: ItemProductBinding,
        private val onProductClick: (Int) -> Unit,
        private val onFavoriteClick: (ProductUI) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: ProductUI) = with(binding) {
            tvName.text = product.title

            if (product.saleState) {
                tvPrice.text = "${product.price} ₺"
                tvPrice.setStrikeThrough()
                tvSalePrice.text = "${product.salePrice} ₺"
            } else {
                tvPrice.text = "${product.price} ₺"
                tvSalePrice.visibility = View.GONE
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