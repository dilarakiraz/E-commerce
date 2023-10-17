package com.dilarakiraz.upschoolcapstoneproject.ui.cart

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dilarakiraz.upschoolcapstoneproject.common.loadImage
import com.dilarakiraz.upschoolcapstoneproject.common.setStrikeThrough
import com.dilarakiraz.upschoolcapstoneproject.data.model.response.ProductUI
import com.dilarakiraz.upschoolcapstoneproject.databinding.ItemCartProductBinding

/**
 * Created on 13.10.2023
 * @author Dilara Kiraz
 */

class CartProductsAdapter(
    private val onProductClick: (Int) -> Unit,
    private val onDeleteClick: (Int) -> Unit,
): ListAdapter<ProductUI, CartProductsAdapter.CartProductViewHolder>(ProductDiffCallBack()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartProductViewHolder =
        CartProductViewHolder(
            ItemCartProductBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            onProductClick,
            onDeleteClick
        )

    override fun onBindViewHolder(holder: CartProductViewHolder, position: Int) = holder.bind(getItem(position))


    class CartProductViewHolder(
        private val binding: ItemCartProductBinding,
        private val onProductClick: (Int) -> Unit,
        private val onDeleteClick: (Int) -> Unit
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
                onDeleteClick(product.id)
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