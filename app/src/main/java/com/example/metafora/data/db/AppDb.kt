package com.example.metafora.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.metafora.data.model.Question

@Database(entities = [Question::class], version = 2, exportSchema = false) // <-- было 1, стало 2
@TypeConverters(Converters::class)
abstract class AppDb : RoomDatabase() {
    abstract fun questionDao(): QuestionDao

    companion object {
        @Volatile private var inst: AppDb? = null
        fun get(context: Context): AppDb =
            inst ?: synchronized(this) {
                inst ?: Room.databaseBuilder(
                    context.applicationContext, AppDb::class.java, "metafora.db"
                )
                    .fallbackToDestructiveMigration()   // <-- можно убрать позже, когда сделаем миграции
                    .build().also { inst = it }
            }
    }
}