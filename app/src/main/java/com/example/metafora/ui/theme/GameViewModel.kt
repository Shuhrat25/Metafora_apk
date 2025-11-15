// GameViewModel.kt
package com.example.metafora.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.metafora.data.Repository
import com.example.metafora.data.model.Question
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GameState(
    val loading: Boolean = false,
    val lives: Int = 3,
    val score: Int = 0,
    val input: String = "",
    val question: Question? = null,
    val message: String? = null,
)

sealed class UiEvent {
    data class GameOver(val finalScore: Int) : UiEvent()
}

class GameViewModel(app: Application) : AndroidViewModel(app) {

    // Репозиторий (нужен context -> используем application)
    private val repo: Repository = Repository(app)

    // состояние игры
    val _state = MutableStateFlow(GameState(loading = true))
    val state: StateFlow<GameState> = _state.asStateFlow()

    // события (GameOver)
    private val _events = MutableSharedFlow<UiEvent>()
    val events: SharedFlow<UiEvent> = _events.asSharedFlow()

    init {
        // при старте подгружаем первый вопрос
        viewModelScope.launch {
            loadNextQuestion()
        }
    }

    fun updateInput(t: String) = _state.update { it.copy(input = t) }

    // Загрузка следующего вопроса
    private suspend fun loadNextQuestion() {
        _state.update { it.copy(loading = true, message = null, input = "") }

        val q = repo.nextQuestion()      // suspend-функция из Repository

        _state.update {
            it.copy(
                loading = false,
                question = q
            )
        }
    }

    // Нажатие на кнопку TEKSHIRISH
    fun check() {
        viewModelScope.launch {
            val s = _state.value
            val q = s.question ?: return@launch

            val userAnswer = s.input.trim()
            val correct = userAnswer.equals(q.answer?.trim().orEmpty(), ignoreCase = true)

            _state.update {
                it.copy(
                    score = it.score + if (correct) 1 else 0,
                    lives = if (correct) it.lives else it.lives - 1,
                    message = if (correct) "✅ To‘g‘ri!" else "❌ Noto‘g‘ri",
                    input = ""
                )
            }

            // даём времени показать сообщение
            delay(1000)

            val after = _state.value
            if (after.lives <= 0) {
                // жизней нет — заканчиваем игру
                _events.emit(UiEvent.GameOver(after.score))
            } else {
                // ещё живы — грузим следующий вопрос
                loadNextQuestion()
            }
        }
    }
}
