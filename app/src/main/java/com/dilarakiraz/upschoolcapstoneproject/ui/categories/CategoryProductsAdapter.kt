package com.dilarakiraz.upschoolcapstoneproject.ui.categories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dilarakiraz.upschoolcapstoneproject.databinding.ItemCategoryBinding

/**
 * Created on 20.10.2023
 * @author Dilara Kiraz
 */

class CategoryProductsAdapter(private val onCategoryItemClick: (String) -> Unit) :
    RecyclerView.Adapter<CategoryProductsAdapter.ViewHolder>() {
    private val categoryList = ArrayList<String>()

    inner class ViewHolder(private val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.categoryCard.setOnClickListener {
                onCategoryItemClick(categoryList[adapterPosition])
            }
        }

        fun bind(item: String) {
            binding.categoryText.text = item
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val categoryBinding =
            ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(categoryBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(categoryList[position])
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    fun updateCategoryList(newCategoryList: List<String>) {
        categoryList.clear()
        categoryList.addAll(newCategoryList)
        notifyDataSetChanged()
    }
}