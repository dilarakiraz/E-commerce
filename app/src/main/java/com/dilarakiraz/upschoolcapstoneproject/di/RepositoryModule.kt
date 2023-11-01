package com.dilarakiraz.upschoolcapstoneproject.di

import com.dilarakiraz.upschoolcapstoneproject.data.repository.ProductRepository
import com.dilarakiraz.upschoolcapstoneproject.data.repository.UserRepository
import com.dilarakiraz.upschoolcapstoneproject.data.source.local.ProductDao
import com.dilarakiraz.upschoolcapstoneproject.data.source.remote.ProductService
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideUserRepository(firebaseAuth: FirebaseAuth, productRepository: ProductRepository): UserRepository =
        UserRepository(firebaseAuth , productRepository)

    @Provides
    @Singleton
    fun provideProductRepository(productService: ProductService, productDao: ProductDao): ProductRepository =
        ProductRepository(productService, productDao)
}