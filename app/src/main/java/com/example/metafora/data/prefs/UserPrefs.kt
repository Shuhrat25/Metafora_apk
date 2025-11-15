package com.example.metafora.data.prefs

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("user_prefs")

data class User(val name: String, val age: Int)

object UserPrefs {
    private val KEY_NAME = stringPreferencesKey("name")
    private val KEY_AGE  = intPreferencesKey("age")
    private val KEY_HIGH = intPreferencesKey("high_score")

    // --- User ---
    fun userFlow(ctx: Context): Flow<User?> =
        ctx.dataStore.data.map { p ->
            val n = p[KEY_NAME] ?: return@map null
            val a = p[KEY_AGE] ?: 0
            User(n, a)
        }

    suspend fun save(ctx: Context, name: String, age: Int) {
        ctx.dataStore.edit { p ->
            p[KEY_NAME] = name
            p[KEY_AGE] = age
        }
    }

    // --- High Score ---
    fun highScoreFlow(ctx: Context): Flow<Int> =
        ctx.dataStore.data.map { it[KEY_HIGH] ?: 0 }

    suspend fun updateHighScoreIfBetter(ctx: Context, newScore: Int) {
        ctx.dataStore.edit { p ->
            val old = p[KEY_HIGH] ?: 0
            if (newScore > old) p[KEY_HIGH] = newScore
        }
    }
}
