package com.dilarakiraz.upschoolcapstoneproject.di

import android.content.Context
import com.dilarakiraz.upschoolcapstoneproject.utilities.ResourceProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UtilitiesModule {

    @Provides
    @Singleton
    fun provideResourceProvider(@ApplicationContext context: Context) = ResourceProvider(context)
}