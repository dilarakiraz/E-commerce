package com.dilarakiraz.upschoolcapstoneproject.data.source.remote

import com.dilarakiraz.upschoolcapstoneproject.data.model.request.AddToCartRequest
import com.dilarakiraz.upschoolcapstoneproject.data.model.request.ClearCartRequest
import com.dilarakiraz.upschoolcapstoneproject.data.model.request.DeleteFromCartRequest
import com.dilarakiraz.upschoolcapstoneproject.data.model.response.BaseResponse
import com.dilarakiraz.upschoolcapstoneproject.data.model.response.GetCartProductsResponse
import com.dilarakiraz.upschoolcapstoneproject.data.model.response.GetCategoriesResponse
import com.dilarakiraz.upschoolcapstoneproject.data.model.response.GetProductDetailResponse
import com.dilarakiraz.upschoolcapstoneproject.data.model.response.GetProductsByCategoryResponse
import com.dilarakiraz.upschoolcapstoneproject.data.model.response.GetProductsResponse
import com.dilarakiraz.upschoolcapstoneproject.data.model.response.GetSaleProductsResponse
import com.dilarakiraz.upschoolcapstoneproject.data.model.response.SearchProductResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ProductService {
    @POST("add_to_cart.php")
    suspend fun addToCart(
        @Body addToCartRequest: AddToCartRequest
    ): BaseResponse

    @POST("delete_from_cart.php")
    suspend fun deleteFromCart(
        @Body deleteFromCartRequest: DeleteFromCartRequest
    ): BaseResponse

    @GET("get_cart_products.php")
    suspend fun getCartProducts(
        @Query("userId") userId: String
    ): GetCartProductsResponse

    @POST("clear_cart.php")
    suspend fun clearCart(
        @Body clearCartRequest: ClearCartRequest
    ): BaseResponse

    @GET("get_products.php")
    suspend fun getProducts(): GetProductsResponse

    @GET("get_products_by_category.php")
    suspend fun getProductsByCategory(
        @Query("category") category: String
    ): GetProductsByCategoryResponse

    @GET("get_sale_products.php")
    suspend fun getSaleProducts(): GetSaleProductsResponse

    @GET("search_product.php")
    suspend fun searchProduct(
        @Query("query") query: String
    ): SearchProductResponse

    @GET("get_categories.php")
    suspend fun getCategories(): GetCategoriesResponse

    @POST("get_product_detail.php")
    suspend fun getProductDetail(
        @Query("id") id: Int
    ): GetProductDetailResponse
}