package studio.vadim.predanie.presentation.screens

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewState

@Composable
fun ProfileScreen() {
    val state = rememberWebViewState("https://fond.predanie.ru")

    WebView(
        state,
        onCreated = { it.settings.javaScriptEnabled = true }
    )
}