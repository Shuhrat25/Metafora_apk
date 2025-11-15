package com.example.metafora.data.net

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object NetModule {
    private val logger = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }
    private val client = OkHttpClient.Builder().addInterceptor(logger).build()

    // ЗАМЕНИ базовый URL на свой (можно на Firebase/Sheets/любой REST)
    private const val BASE_URL = "https://your.api.example/"

    val api: QuestionApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()
        .create(QuestionApi::class.java)
}
