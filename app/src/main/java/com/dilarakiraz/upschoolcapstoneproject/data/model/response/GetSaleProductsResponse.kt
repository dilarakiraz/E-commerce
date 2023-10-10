package com.dilarakiraz.upschoolcapstoneproject.data.model.response

/**
 * Created on 7.10.2023
 * @author Dilara Kiraz
 */

data class GetSaleProductsResponse(
    val products: List<Product>?,
    var status: Int? = null,
    var message: String? = null
)