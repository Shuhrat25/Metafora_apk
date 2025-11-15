package com.example.metafora.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "questions")
data class Question(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val prompt: String = "METAFORA",           // заголовок
    val images: List<String>,                  // "https://..." или "res://book"
    val answer: String,                        // правильный ответ
    val lang: String? = "uz",                  // язык (по желанию)
    val help: String? = null
)
