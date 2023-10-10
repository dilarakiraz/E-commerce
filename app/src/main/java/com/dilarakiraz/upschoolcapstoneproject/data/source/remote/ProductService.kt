package com.dilarakiraz.upschoolcapstoneproject.data.source.remote

import com.dilarakiraz.upschoolcapstoneproject.common.Constants.EndPoints.GET_PRODUCTS
import com.dilarakiraz.upschoolcapstoneproject.data.model.response.GetProductDetailResponse
import com.dilarakiraz.upschoolcapstoneproject.data.model.response.GetProductsResponse
import com.dilarakiraz.upschoolcapstoneproject.data.model.response.GetSaleProductsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ProductService {
    @GET(GET_PRODUCTS)
    suspend fun getProducts(): GetProductsResponse

    @GET("get_product_detail.php")
    suspend fun getProductDetail(
        @Query("id") id: Int
    ): GetProductDetailResponse

    @GET("get_sale_products.php")
    suspend fun getSaleProducts(): GetSaleProductsResponse
}