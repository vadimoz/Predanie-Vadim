package studio.vadim.predanie.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.ui.PlayerView
import androidx.navigation.NavHostController
import studio.vadim.predanie.presentation.MainViewModel

@Composable
@androidx.media3.common.util.UnstableApi
fun PlayerScreen(mainViewModel: MainViewModel, navController: NavHostController) {
    val uiState by mainViewModel.uiState.collectAsState()
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AndroidView(
            factory = { context ->
                PlayerView(context).apply {
                    player = uiState.playerController
                    controllerHideOnTouch = false
                    controllerAutoShow = false
                    controllerShowTimeoutMs = 0
                    showController()
                }
            },
            update = {
                it.player = uiState.playerController
            }
        )
    }
}