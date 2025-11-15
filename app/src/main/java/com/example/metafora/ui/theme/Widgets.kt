package com.example.metafora.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@SuppressLint("DiscouragedApi", "LocalContextResourcesRead")
@Composable
fun ImageFromUri(uri: String, modifier: Modifier = Modifier) {
    val ctx = LocalContext.current
    if (uri.startsWith("res://")) {
        val name = uri.removePrefix("res://")
        val resId = ctx.resources.getIdentifier(name, "drawable", ctx.packageName)
        if (resId != 0) {
            Image(
                painter = painterResource(id = resId),
                contentDescription = null,
                modifier = modifier,
                contentScale = ContentScale.Crop
            )
        } else {
            // Фолбек: сразу видно, если имя файла не совпало
            Box(
                modifier = modifier
                    .background(Color(0xFFFFE0E0))
                    .border(1.dp, Color.Red),
                contentAlignment = Alignment.Center
            ) {
                Text("not found:\n$name", color = Color.Red, fontSize = 12.sp)
            }
        }
    } else {
        // URL (онлайн) — через Coil
        AsyncImage(
            model = uri,
            contentDescription = null,
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    }
}
