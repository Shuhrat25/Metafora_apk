package com.example.metafora

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.metafora.ui.MetaforaNavHost

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Отключаем системный Autofill (карточки с подсказками) для всего приложения
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            window.decorView.importantForAutofill =
                View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS
        }

        setContent {
            val nav = rememberNavController()
            MetaforaNavHost(nav)
        }
    }
}
