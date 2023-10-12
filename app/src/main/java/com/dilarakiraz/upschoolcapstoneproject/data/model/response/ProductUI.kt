package com.dilarakiraz.upschoolcapstoneproject.data.model.response

/**
 * Created on 7.10.2023
 * @author Dilara Kiraz
 */

data class ProductUI(
    val id: Int,
    val title: String,
    val price: Double,
    val salePrice: Double,
    val description: String,
    val category: String,
    val imageOne: String,
    val rate: Double,
    val count: Int,
    val saleState: Boolean,
    val isFavorite: Boolean = false
){
}