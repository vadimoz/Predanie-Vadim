package fund.predanie.medialib.presentation.screens

import android.content.pm.ActivityInfo
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import androidx.navigation.NavHostController
import fund.predanie.medialib.R
import fund.predanie.medialib.presentation.MainViewModel

@Composable
fun LockScreenOrientation(orientation: Int) {
    val context = LocalContext.current
    DisposableEffect(orientation) {
        val activity = context.findActivity() ?: return@DisposableEffect onDispose {}
        val originalOrientation = activity.requestedOrientation
        activity.requestedOrientation = orientation
        onDispose {
            // restore original orientation when view disappears
            activity.requestedOrientation = originalOrientation
        }
    }
}

@Composable
@androidx.media3.common.util.UnstableApi
fun PlayerScreen(mainViewModel: MainViewModel, navController: NavHostController) {
    LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)

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
                    defaultArtwork = getResources().getDrawable(R.drawable.artwork)
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                }
            },
            update = {
                it.player = uiState.playerController
                it.player?.play()
            }
        )
    }
}