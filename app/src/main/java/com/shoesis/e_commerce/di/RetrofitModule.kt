package com.shoesis.e_commerce.di

import com.shoesis.e_commerce.common.Constants.BASE_URL
import com.shoesis.e_commerce.data.source.ProductService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RetrofitModule {

    @Provides
    @Singleton
    fun provideRetrofitService(): ProductService {
        return ProductService()
    }
}

