package com.dilarakiraz.upschoolcapstoneproject.data.repository

import com.dilarakiraz.upschoolcapstoneproject.common.Resource
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

            if (result.status == 200) {
                val productList = result.products.orEmpty().map {
                    it.mapToProductUI(favoriteTitles.contains(it.title))
                }
                Resource.Success(productList)
            } else {
                Resource.Fail(result.message.orEmpty())
            }
        } catch (e: Exception) {
            Resource.Error(e)
        }

    suspend fun getProductDetail(id: Int): Resource<ProductUI> =
        try {
            val result = productService.getProductDetail(id)
            val favoriteTitles = productDao.getFavoriteTitles()

            if (result.status == 200 && result.product != null) {
                val productUI = result.product.mapToProductUI(favoriteTitles.contains(result.product.title))
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

    suspend fun getFavorites(): Resource<List<ProductUI>> =
        try {
            val products = productDao.getProducts().map {
                it.mapToProductUI()
            }
            Resource.Success(products)
        } catch (e: Exception) {
            Resource.Error(e)
        }

    suspend fun deleteFromFavorites(product: ProductUI) {
        productDao.deleteFromFavorites(product.mapToProductEntity())
    }

    suspend fun clearFavorites() {
        productDao.clearFavorites()
    }

}