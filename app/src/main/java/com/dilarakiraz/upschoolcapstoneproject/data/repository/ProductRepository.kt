package com.dilarakiraz.upschoolcapstoneproject.data.repository

import com.dilarakiraz.upschoolcapstoneproject.common.Resource
import com.dilarakiraz.upschoolcapstoneproject.data.model.mapper.mapToProductEntity
import com.dilarakiraz.upschoolcapstoneproject.data.model.mapper.mapToProductUI
import com.dilarakiraz.upschoolcapstoneproject.data.model.request.AddToCartRequest
import com.dilarakiraz.upschoolcapstoneproject.data.model.request.ClearCartRequest
import com.dilarakiraz.upschoolcapstoneproject.data.model.request.DeleteFromCartRequest
import com.dilarakiraz.upschoolcapstoneproject.data.model.response.BaseResponse
import com.dilarakiraz.upschoolcapstoneproject.data.model.response.ProductUI
import com.dilarakiraz.upschoolcapstoneproject.data.source.local.ProductDao
import com.dilarakiraz.upschoolcapstoneproject.data.source.remote.ProductService


/**
 * Created on 7.10.2023
 * @author Dilara Kiraz
 */

class ProductRepository(
    private val productService: ProductService,
    private val productDao: ProductDao
) {

    suspend fun getAllProducts(): Resource<List<ProductUI>> {
        val result = productService.getProducts()
        val favoriteTitles = productDao.getFavoriteTitles()

        return result.call {
            result.products.orEmpty().map {
                it.mapToProductUI(favoriteTitles.contains(it.title))
            }
        }
    }

    suspend fun getSaleProducts(): Resource<List<ProductUI>> {
        val result = productService.getSaleProducts()
        val favoriteTitles = productDao.getFavoriteTitles()

        return result.call {
            result.products.orEmpty().filter { (it.salePrice ?: 0.0) > 0.0 }.map {
                it.mapToProductUI(favoriteTitles.contains(it.title))
            }
        }
    }

//    suspend fun getSaleProducts(): Resource<List<ProductUI>> {
//        val result = productService.getSaleProducts()
//        val favoriteTitles = productDao.getFavoriteTitles()
//
//        return result.call {
//            result.products.orEmpty().map {
//                it.mapToProductUI(favoriteTitles.contains(it.title))
//            }
//        }
//    }

    suspend fun getProductDetail(id: Int): Resource<ProductUI> {
        val result = productService.getProductDetail(id)
        val favoriteTitles = productDao.getFavoriteTitles()

        return result.call {
            result.product.mapToProductUI(favoriteTitles.contains(result.product?.title))
        }
    }

    suspend fun addToFavorites(product: ProductUI) =
        productDao.addToFavorites(product.mapToProductEntity())

    suspend fun deleteFromFavorites(product: ProductUI) =
        productDao.deleteFromFavorites(product.mapToProductEntity())

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

    suspend fun getCartProducts(userId: String): Resource<List<ProductUI>> {
        val result = productService.getCartProducts(userId)
        val favoriteTitles = productDao.getFavoriteTitles()

        return result.call {
            result.products.orEmpty().map {
                it.mapToProductUI(favoriteTitles.contains(it.title))
            }
        }
    }

    suspend fun addToCart(userId: String, id: Int): Resource<Unit> {
        val result = productService.addToCart(AddToCartRequest(userId, id))
        return result.call {}
    }

    suspend fun deleteFromCart(id: Int): Resource<Unit> {
        val result = productService.deleteFromCart(DeleteFromCartRequest(id))
        return result.call {}
    }

    suspend fun clearCart(userId: String): Resource<Unit> {
        val result = productService.clearCart(ClearCartRequest(userId))
        return result.call {}
    }

    suspend fun getProductsByCategory(category: String): Resource<List<ProductUI>> {
        val result = productService.getProductsByCategory(category)
        val favoriteTitles = productDao.getFavoriteTitles()

        return result.call {
            result.products.orEmpty().map {
                it.mapToProductUI(favoriteTitles.contains(it.title))
            }
        }
    }

    suspend fun getCategories(): Resource<List<String>> =
        try {
            Resource.Success(productService.getCategories().categories.orEmpty())
        } catch (e: Exception) {
            Resource.Error(e)
        }

    suspend fun searchProduct(query: String): Resource<List<ProductUI>> {
        val result = productService.searchProduct(query)
        val favoriteTitles = productDao.getFavoriteTitles()

        return result.call {
            result.products.orEmpty().map {
                it.mapToProductUI(favoriteTitles.contains(it.title))
            }
        }
    }

    private fun <T : Any> BaseResponse.call(onSuccess: () -> T): Resource<T> {
        return try {
            return if (status == 200) {
                Resource.Success(onSuccess())
            } else {
                Resource.Fail(this.message.orEmpty())
            }
        } catch (e: Exception) {
            Resource.Error(throwable = e)
        }
    }
}