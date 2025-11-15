package com.example.metafora.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.metafora.R
import com.example.metafora.data.prefs.User
import com.example.metafora.data.prefs.UserPrefs
import kotlinx.coroutines.launch

@Composable
fun WelcomeScreen(onStart: (String, Int) -> Unit) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    // Если пользователь уже сохранён — уходим сразу
    val savedUser: User? by UserPrefs.userFlow(ctx).collectAsState(initial = null)
    LaunchedEffect(savedUser) {
        savedUser?.let { onStart(it.name, it.age) }
    }

    var name by remember { mutableStateOf("") }
    var ageText by remember { mutableStateOf("") }

    // Фильтруем имя: буквы любых алфавитов + пробел/дефис/апостроф; ограничим длину
    fun filterName(raw: String): String =
        raw.filter { it.isLetter() || it == ' ' || it == '-' || it == '\'' }
            .take(30)

    val ageOnlyDigits = remember(ageText) { ageText.filter { it.isDigit() } }
    val btnEnabled = name.isNotBlank() && ageOnlyDigits.isNotBlank()

    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.fon),
            contentDescription = null,
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("METAFORA", fontSize = 32.sp, textAlign = TextAlign.Center)
            Spacer(Modifier.height(24.dp))

            LabeledRoundedField(
                value = name,
                onValueChange = { name = filterName(it) },
                label = "ISM",
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,
                capitalization = KeyboardCapitalization.Words
            )

            Spacer(Modifier.height(12.dp))

            LabeledRoundedField(
                value = ageText,
                onValueChange = { ageText = it },
                label = "YOSH",
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done,
                capitalization = KeyboardCapitalization.None
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    val age = ageOnlyDigits.toIntOrNull() ?: 0
                    scope.launch {
                        UserPrefs.save(ctx, name.trim(), age)
                        onStart(name.trim(), age)
                    }
                },
                enabled = btnEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF9800), // оранжевый
                    contentColor = Color.White // белый текст
                )
            ) {
                Text("Keyingisi")
            }
        }
    }
}

@Composable
private fun LabeledRoundedField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType,
    imeAction: ImeAction,
    capitalization: KeyboardCapitalization
) {
    Column(Modifier.fillMaxWidth()) {
        Text(text = label, fontSize = 20.sp, color = Color(0xFFDB6C20))
        Spacer(Modifier.height(6.dp))
        TextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = imeAction,
                capitalization = capitalization
            ),
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                cursorColor = Color(0xFFDB6C20)
            )
        )
    }
}
