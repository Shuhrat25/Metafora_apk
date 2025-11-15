package com.example.metafora.data.net

import com.example.metafora.data.model.Question
import retrofit2.http.GET
import retrofit2.http.Query

interface QuestionApi {
    // Ожидаем, что сервер вернёт одну задачу формата Question
    @GET("questions/random")
    suspend fun getRandom(@Query("lang") lang: String? = null): Question
}
