package com.example.metafora.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.metafora.R

@Composable
fun GameScreen(
    vm: GameViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onGameOver: (Int) -> Unit
) {
    val s by vm.state.collectAsState()

    // слушаем события окончания игры
    LaunchedEffect(Unit) {
        vm.events.collect { ev ->
            if (ev is UiEvent.GameOver) onGameOver(ev.finalScore)
        }
    }

    // если идёт загрузка вопроса — показываем спиннер
    if (s.loading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color(0xFFFF9800))
        }
        return
    }

    var showHelp by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxSize()) {
        // фон
        Image(
            painter = painterResource(id = R.drawable.fon),
            contentDescription = null,
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Crop
        )

        // контент
        Column(
            Modifier
                .fillMaxSize()
                .padding(25.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Верхняя панель
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "METAFORA",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    "⭐ ${s.score}   ❤️ ${s.lives}",
                    color = Color.White,
                    fontSize = 18.sp
                )
            }

            Spacer(Modifier.height(16.dp))

            // Картинки вопроса
            val imgs = s.question?.images.orEmpty()
            val rows = imgs.chunked(2)
            rows.forEach { row ->
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    row.forEach { uri ->
                        Box(
                            Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                        ) {
                            ImageFromUri(uri, Modifier.matchParentSize())
                        }
                    }
                    if (row.size == 1) {
                        Spacer(
                            Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
            }

            Spacer(Modifier.height(10.dp))

            // Поле ввода
            OutlinedTextField(
                value = s.input,
                onValueChange = vm::updateInput,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                label = { Text("Javob", color = Color.Gray) }
            )

            // Кнопка "Yordam"
            s.question?.help?.let { tip ->
                Spacer(Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { showHelp = !showHelp },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                ) {
                    Text(if (showHelp) "Yopish" else "Yordam")
                }
                if (showHelp) {
                    Spacer(Modifier.height(6.dp))
                    Text(tip, color = Color.White, fontSize = 16.sp)
                }
            }

            Spacer(Modifier.height(16.dp))

            // Кнопка TEKSHIRISH
            Button(
                onClick = vm::check,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(32.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF9800),
                    contentColor = Color.White
                )
            ) {
                Text("TEKSHIRISH", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            // ✅ / ❌ Сообщение — с анимацией
            AnimatedVisibility(
                visible = s.message != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                s.message?.let { msg ->
                    val color =
                        if (msg.startsWith("✅")) Color(0xFF4CAF50) else Color(0xFFF44336)
                    Text(
                        text = msg,
                        color = color,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 14.dp)
                    )
                }
            }
        }
    }
}
