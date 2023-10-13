package com.dilarakiraz.upschoolcapstoneproject.data.repository

import com.dilarakiraz.upschoolcapstoneproject.common.Resource
import com.dilarakiraz.upschoolcapstoneproject.data.model.request.AddToCartRequest
import com.dilarakiraz.upschoolcapstoneproject.data.model.request.ClearCartRequest
import com.dilarakiraz.upschoolcapstoneproject.data.model.request.DeleteFromCartRequest
import com.dilarakiraz.upschoolcapstoneproject.data.model.response.BaseResponse
import com.dilarakiraz.upschoolcapstoneproject.data.model.response.ProductUI
import com.dilarakiraz.upschoolcapstoneproject.data.source.local.ProductDao
import com.dilarakiraz.upschoolcapstoneproject.data.source.remote.ProductService
import mapToProductEntity
import mapToProductUI

/**
 * Created on 7.10.2023
 * @author Dilara Kiraz
 */

class ProductRepository(
    private val productService: ProductService,
    private val productDao: ProductDao
) {

    suspend fun getAllProducts(): Resource<List<ProductUI>> =
        try{
            val result = productService.getProducts()
            val favoriteTitles = productDao.getFavoriteTitles()

            if (result.status == 200){
                val productList = result.products.orEmpty().map {
                    it.mapToProductUI(favoriteTitles.contains(it.title))
                }
                Resource.Success(productList)
            }else{
                Resource.Fail(result.message.orEmpty())
            }
        }catch (e:Exception){
            Resource.Error(throwable = e)
        }


    suspend fun getSaleProducts(): Resource<List<ProductUI>> =
        try {
            val result = productService.getSaleProducts()
            val favoriteTitles = productDao.getFavoriteTitles()
            result.call(
                onSuccess = {
                    result.products.orEmpty().map {
                        it.mapToProductUI(favoriteTitles.contains(it.title))
                    }
                },
                onFail = { result.message }
            )
        } catch (e: Exception) {
            Resource.Error(e)
        }

    suspend fun getProductDetail(id: Int): Resource<ProductUI> =
        try {
            val result = productService.getProductDetail(id)
            val favoriteTitles = productDao.getFavoriteTitles()
            result.call(
                onSuccess = {
                    result.product?.mapToProductUI(favoriteTitles.contains(result.product.title))!!
                },
                onFail = { result.message }
            )
        } catch (e: Exception) {
            Resource.Error(e)
        }

    suspend fun addToFavorites(product: ProductUI) {
        productDao.addToFavorites(product.mapToProductEntity())
    }

    suspend fun deleteFromFavorites(product: ProductUI) {
        productDao.deleteFromFavorites(product.mapToProductEntity())
    }

    suspend fun getFavorites(): Resource<List<ProductUI>> =
        try {
            val products = productDao.getProducts().map {
                it.mapToProductUI()
            }
            Resource.Success(products)
        } catch (e: Exception) {
            Resource.Error(e)
        }

    suspend fun clearFavorites() {
        productDao.clearFavorites()
    }

    suspend fun getCartProducts(userId: String): Resource<List<ProductUI>> =
        try {
            val result = productService.getCartProducts(userId)
            val favoriteTitles = productDao.getFavoriteTitles()
            result.call(
                onSuccess = {
                    result.products.orEmpty().map {
                        it.mapToProductUI(favoriteTitles.contains(it.title))
                    }
                },
                onFail = { result.message }
            )
        } catch (e: Exception) {
            Resource.Error(e)
        }

    suspend fun addToCart(userId: String, id: Int): Resource<BaseResponse> {
        return try {
            Resource.Success(productService.addToCart(AddToCartRequest(userId, id)))
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    suspend fun deleteFromCart(id: Int): Resource<BaseResponse> =
        try {
            Resource.Success(productService.deleteFromCart(DeleteFromCartRequest(id)))
        } catch (e: Exception) {
            Resource.Error(e)
        }

    suspend fun clearCart(userId: String): Resource<BaseResponse> =
        try {
            Resource.Success(productService.clearCart(ClearCartRequest(userId)))
        } catch (e: Exception) {
            Resource.Error(e)
        }

    suspend fun getProductsByCategory(category: String): Resource<List<ProductUI>> =
        try {
            val result = productService.getProductsByCategory(category)
            val favoriteTitles = productDao.getFavoriteTitles()
            result.call(
                onSuccess = {
                    result.products.orEmpty().map {
                        it.mapToProductUI(favoriteTitles.contains(it.title))
                    }
                },
                onFail = { result.message }
            )
        } catch (e: Exception) {
            Resource.Error(e)
        }

    suspend fun searchProduct(query: String): Resource<List<ProductUI>> =
        try {
            val result = productService.searchProduct(query)
            val favoriteTitles = productDao.getFavoriteTitles()
            result.call(
                onSuccess = {
                    result.products.orEmpty().map {
                        it.mapToProductUI(favoriteTitles.contains(it.title))
                    }
                },
                onFail = { result.message }
            )
        } catch (e: Exception) {
            Resource.Error(e)
        }

    suspend fun getCategories(): Resource<List<String>> =
        try {
            Resource.Success(productService.getCategories().categories.orEmpty())
        } catch (e: Exception) {
            Resource.Error(e)
        }

    private fun <T : Any> BaseResponse.call(
        onSuccess: () -> T,
        onFail: () -> String?
    ): Resource<T> {
        return if (status == 200) {
            Resource.Success(onSuccess())
        } else {
            Resource.Fail(onFail().orEmpty())
        }
    }

}