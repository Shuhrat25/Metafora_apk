package com.example.metafora.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.metafora.R
import com.example.metafora.data.prefs.UserPrefs

@Composable
fun MainScreen(
    onStartGame: () -> Unit,
) {
    val ctx = LocalContext.current
    val user by UserPrefs.userFlow(ctx).collectAsState(initial = null)
    val highScore by UserPrefs.highScoreFlow(ctx).collectAsState(initial = 0)

    Box(Modifier.fillMaxSize()) {
        // Фон
        Image(
            painter = painterResource(R.drawable.fon),
            contentDescription = null,
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 36.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Имя и очки (опущены на 10px)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = user?.name ?: "Sardor",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "🏆: $highScore",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }

            Spacer(Modifier.height(60.dp)) // отступ до логотипа

            // Логотип (увеличен до 90% ширины)
            Image(
                painter = painterResource(R.drawable.logofull),
                contentDescription = "Logo",
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .height(180.dp), // увеличил высоту немного
                contentScale = ContentScale.Fit
            )

            Spacer(Modifier.height(50.dp)) // кнопка ближе к логотипу, но не внизу

            // Кнопка BOSHLASH
            Button(
                onClick = onStartGame,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(64.dp),
                shape = RoundedCornerShape(32.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF9800), // оранжевый
                    contentColor = Color.White // белый текст
                )
            ) {
                Text(
                    text = "BOSHLASH",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
