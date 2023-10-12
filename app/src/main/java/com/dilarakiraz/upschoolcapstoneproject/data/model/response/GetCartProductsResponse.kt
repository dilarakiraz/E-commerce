package com.dilarakiraz.upschoolcapstoneproject.data.model.response

/**
 * Created on 7.10.2023
 * @author Dilara Kiraz
 */

data class GetCartProductsResponse(
    val products: List<Product>?
) : BaseResponse()