package com.dilarakiraz.upschoolcapstoneproject.ui.favorites

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dilarakiraz.upschoolcapstoneproject.common.loadImage
import com.dilarakiraz.upschoolcapstoneproject.common.setStrikeThrough
import com.dilarakiraz.upschoolcapstoneproject.data.model.response.ProductUI
import com.dilarakiraz.upschoolcapstoneproject.databinding.ItemFavoriteProductBinding
import com.dilarakiraz.upschoolcapstoneproject.ui.home.AllProductsAdapter

/**
 * Created on 10.10.2023
 * @author Dilara Kiraz
 */

class FavoritesAdapter(
    private val onProductClick: (Int) -> Unit,
    private val onDeleteClick: (ProductUI) -> Unit,
) : ListAdapter<ProductUI, FavoritesAdapter.ProductViewHolder>(AllProductsAdapter.ProductDiffCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder =
        ProductViewHolder(
            ItemFavoriteProductBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            onProductClick,
            onDeleteClick
        )

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) =
        holder.bind(getItem(position))

    class ProductViewHolder(
        private val binding: ItemFavoriteProductBinding,
        private val onProductClick: (Int) -> Unit,
        private val onDeleteClick: (ProductUI) -> Unit,
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

            ivProduct.loadImage(product.imageOne)

            root.setOnClickListener {
                onProductClick(product.id)
            }

            ivDelete.setOnClickListener {
                onDeleteClick(product)
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