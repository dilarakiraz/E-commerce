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
        try {
            val result = productService.getProducts()
            val favoriteTitles = productDao.getFavoriteTitles()

            if (result.status == 200) {
                val productList = result.products.orEmpty().map {
                    it.mapToProductUI(favoriteTitles.contains(it.title))
                }
                Resource.Success(productList)
            } else {
                Resource.Fail(result.message.orEmpty())
            }
        } catch (e: Exception) {
            Resource.Error(throwable = e)
        }


    suspend fun getSaleProducts(): Resource<List<ProductUI>> =
        try {
            val result = productService.getSaleProducts()
            if (result.status == 200) {
                val favoriteTitles = productDao.getFavoriteTitles()
                val productUIList = result.products.orEmpty().map {
                    it.mapToProductUI(favoriteTitles.contains(it.title))
                }
                Resource.Success(productUIList)
            } else {
                Resource.Fail(result.message.orEmpty())
            }
        } catch (e: Exception) {
            Resource.Error(e)
        }

    suspend fun getProductDetail(id: Int): Resource<ProductUI> =
        try {
            val result = productService.getProductDetail(id)
            if (result.status == 200 && result.product != null) {
                val favoriteTitles = productDao.getFavoriteTitles()
                val productUI =
                    result.product.mapToProductUI(favoriteTitles.contains(result.product.title))
                Resource.Success(productUI)
            } else {
                Resource.Fail(result.message.orEmpty())
            }
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

    suspend fun clearFavorites(): Resource<Unit> {
        return try {
            productDao.clearFavorites()
            Resource.Success(Unit)
        } catch (e: Throwable) {
            Resource.Error(e)
        }
    }

    suspend fun getCartProducts(userId: String): Resource<List<ProductUI>> =
        try {
            val result = productService.getCartProducts(userId)
            if (result.status == 200) {
                val favoriteTitles = productDao.getFavoriteTitles()
                val productUIList = result.products.orEmpty().map {
                    it.mapToProductUI(favoriteTitles.contains(it.title))
                }
                Resource.Success(productUIList)
            } else {
                Resource.Fail(result.message.orEmpty())
            }
        } catch (e: Exception) {
            Resource.Error(e)
        }

    suspend fun addToCart(userId: String, id: Int): Resource<Boolean> {
        return try {
            val response = productService.addToCart(AddToCartRequest(userId, id))
            if (response.status != null && response.status == 200) {
                Resource.Success(true)
            } else {
                val errorMessage = response.message ?: "Ürün sepete eklenemedi."
                Resource.Error(Exception(errorMessage))
            }
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
            if (result.status == 200) {
                val favoriteTitles = productDao.getFavoriteTitles()
                val productUIList = result.products.orEmpty().map {
                    it.mapToProductUI(favoriteTitles.contains(it.title))
                }
                Resource.Success(productUIList)
            } else {
                Resource.Fail(result.message.orEmpty())
            }
        } catch (e: Exception) {
            Resource.Error(e)
        }

    suspend fun getCategories(): Resource<List<String>> =
        try {
            Resource.Success(productService.getCategories().categories.orEmpty())
        } catch (e: Exception) {
            Resource.Error(e)
        }

    suspend fun searchProduct(query: String): Resource<List<ProductUI>> =
        try {
            val result = productService.searchProduct(query)
            if (result.status == 200) {
                val favoriteTitles = productDao.getFavoriteTitles()
                Resource.Success(
                    result.products.orEmpty().map {
                        it.mapToProductUI(favoriteTitles.contains(it.title))
                    }
                )
            } else {
                Resource.Fail(result.message.orEmpty())
            }
        } catch (e: Exception) {
            Resource.Error(e)
        }
}