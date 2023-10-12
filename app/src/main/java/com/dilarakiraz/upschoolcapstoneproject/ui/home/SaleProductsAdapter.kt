package com.dilarakiraz.upschoolcapstoneproject.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dilarakiraz.upschoolcapstoneproject.R
import com.dilarakiraz.upschoolcapstoneproject.common.invisible
import com.dilarakiraz.upschoolcapstoneproject.common.loadImage
import com.dilarakiraz.upschoolcapstoneproject.common.setStrikeThrough
import com.dilarakiraz.upschoolcapstoneproject.common.visible
import com.dilarakiraz.upschoolcapstoneproject.data.model.response.ProductUI
import com.dilarakiraz.upschoolcapstoneproject.databinding.ItemSaleProductBinding

/**
 * Created on 7.10.2023
 * @author Dilara Kiraz
 */

class SaleProductsAdapter(
    private val onProductClick: (Int) -> Unit,
    private val onFavoriteClick: (ProductUI) -> Unit,
): ListAdapter<ProductUI, SaleProductsAdapter.SaleProductViewHolder>(ProductDiffCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SaleProductViewHolder =
        SaleProductViewHolder(
            ItemSaleProductBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            onProductClick,
            onFavoriteClick
        )

    override fun onBindViewHolder(holder: SaleProductViewHolder, position: Int) {
        val product = getItem(position)
        holder.bind(product)
    }

    class SaleProductViewHolder(
        private val binding: ItemSaleProductBinding,
        private val onProductClick: (Int) -> Unit,
        private val onFavoriteClick: (ProductUI) -> Unit,
    ): RecyclerView.ViewHolder(binding.root){


            fun bind(product: ProductUI) = with(binding){
            tvName.text = product.title
            tvPrice.text = "${product.price} ₺"

            if (product.saleState && product.salePrice > 0) {
                tvSalePrice.text = "${product.salePrice} ₺"
                tvSalePrice.visible()
                tvPrice.setStrikeThrough()
            }else {
                tvSalePrice.text = ""
                tvSalePrice.invisible()
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

    class ProductDiffCallBack: DiffUtil.ItemCallback<ProductUI>(){
        override fun areItemsTheSame(oldItem: ProductUI, newItem: ProductUI): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ProductUI, newItem: ProductUI): Boolean {
            return oldItem == newItem
        }
    }
}