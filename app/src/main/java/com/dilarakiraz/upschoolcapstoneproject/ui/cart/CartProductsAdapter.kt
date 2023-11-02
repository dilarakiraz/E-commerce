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
 * Created on 18.10.2023
 * @author Dilara Kiraz
 */

class CartProductsAdapter(
    private val onProductClick: (Int) -> Unit,
    private val onDeleteClick: (Int) -> Unit,
    private val onPriceChangeClick: (Double, Double) -> Unit
) : ListAdapter<ProductUI, CartProductsAdapter.CartProductViewHolder>(ProductDiffCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartProductViewHolder =
        CartProductViewHolder(
            ItemCartProductBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            onProductClick,
            onDeleteClick,
            onPriceChangeClick
        )

    override fun onBindViewHolder(holder: CartProductViewHolder, position: Int) =
        holder.bind(getItem(position))

    class CartProductViewHolder(
        private val binding: ItemCartProductBinding,
        private val onProductClick: (Int) -> Unit,
        private val onDeleteClick: (Int) -> Unit,
        private val onPriceChangeClick: (Double, Double) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        private var productCount = 1

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

            imgIncrease.setOnClickListener {
                onPriceChangeClick(product.price, product.salePrice)
                productCount++
                tvProductCount.text = productCount.toString()
            }

            imgDecrease.setOnClickListener {
                productCount--
                tvProductCount.text = productCount.toString()
                if (productCount <= 0) {
                    onDeleteClick(product.id)
                } else {
                    onPriceChangeClick(-product.price, -product.salePrice)
                }
            }

            tvProductCount.text = productCount.toString()
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