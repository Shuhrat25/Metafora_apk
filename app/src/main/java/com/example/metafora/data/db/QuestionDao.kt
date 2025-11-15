package com.example.metafora.data.db

import androidx.room.*
import com.example.metafora.data.model.Question

@Dao
interface QuestionDao {

    // взять первый вопрос (можно использовать при первом запуске)
    @Query("SELECT * FROM questions ORDER BY id LIMIT 1")
    suspend fun first(): Question?

    // взять все вопросы, отсортированные по id (для перехода к следующему)
    @Query("SELECT * FROM questions ORDER BY id")
    suspend fun getAll(): List<Question>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<Question>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(q: Question): Long

    @Query("SELECT COUNT(*) FROM questions")
    suspend fun count(): Int
}
