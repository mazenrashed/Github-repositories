package com.mazenrashed.github.data.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class ServiceGenerator {

    fun getRestService(): EndPoints {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(getHttpClient())
            .build()
        return retrofit.create(EndPoints::class.java)
    }

    private fun getHttpClient() : OkHttpClient{
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val httpClientBuilder = OkHttpClient.Builder()
        httpClientBuilder.interceptors().add(interceptor)
        return httpClientBuilder.build()
    }

    companion object {
        private const val BASE_URL = "https://api.github.com"
    }
}