package com.rais.nexusbody.di

import com.rais.nexusbody.core.network.FoodApiService
import com.rais.nexusbody.core.network.GroqApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    @Named("FoodRetrofit")
    fun provideFoodRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://world.openfoodfacts.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @Named("GroqRetrofit")
    fun provideGroqRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.groq.com/openai/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideFoodApiService(@Named("FoodRetrofit") retrofit: Retrofit): FoodApiService {
        return retrofit.create(FoodApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideGroqApiService(@Named("GroqRetrofit") retrofit: Retrofit): GroqApiService {
        return retrofit.create(GroqApiService::class.java)
    }
}
