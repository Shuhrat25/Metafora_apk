package com.example.metafora.ui

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.metafora.data.prefs.UserPrefs
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

private object Routes {
    const val Splash = "splash"
    const val Welcome = "welcome"
    const val Main = "main"
    const val Game = "game"
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun MetaforaNavHost(
    navController: NavHostController = rememberNavController()
) {
    val ctx = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = Routes.Splash
    ) {
        // --- решаем стартовый экран и уходим дальше ---
        composable(Routes.Splash) {
            SplashDecider(
                onDecided = { dest ->
                    navController.navigate(dest) {
                        popUpTo(Routes.Splash) { inclusive = true } // убираем сплэш из стека
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Routes.Welcome) {
            WelcomeScreen { _, _ ->
                navController.navigate(Routes.Main) {
                    popUpTo(Routes.Welcome) { inclusive = true }
                    launchSingleTop = true
                }
            }
        }

        composable(Routes.Main) {
            MainScreen(
                onStartGame = { navController.navigate(Routes.Game) }
            )
        }

        composable(Routes.Game) {
            val ctx = LocalContext.current
            // <-- это можно: мы внутри composable-ленмбды роута
            val scope = rememberCoroutineScope()

            GameScreen(
                onGameOver = { finalScore: Int ->
                    // обновим рекорд
                    scope.launch {
                        UserPrefs.updateHighScoreIfBetter(ctx, finalScore)
                    }
                    // вернёмся на главный экран
                    // вариант 1: просто снять Game со стека, вернувшись на Main
                    navController.popBackStack(Routes.Main, inclusive = false)
                    // (если хочешь гарантированно оказаться на Main)
                    navController.navigate(Routes.Main) { launchSingleTop = true }
                }
            )
        }
    }
}

/** Небольшой композабл, который решает: есть ли сохранённый пользователь. */
@Composable
private fun SplashDecider(onDecided: (String) -> Unit) {
    val ctx = LocalContext.current
    // читаем текущего пользователя один раз
    val userFlow = UserPrefs.userFlow(ctx)
    val savedUser by userFlow.collectAsState(initial = null)

    LaunchedEffect(savedUser) {
        // когда появится значение — решаем куда идти
        val dest = if (savedUser != null) Routes.Main else Routes.Welcome
        onDecided(dest)
    }

    // можно отрисовать простой индикатор, если хочется
    // Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
}