package com.dilarakiraz.upschoolcapstoneproject.data.model.request

/**
 * Created on 7.10.2023
 * @author Dilara Kiraz
 */

data class AddToCartRequest (
    val userId: String,
    val productId: Int
)