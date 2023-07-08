package studio.vadim.predanie.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun PlayerScreen() {

    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // Fetching the Local Context
        /*val mContext = LocalContext.current

        // Declaring ExoPlayer
        val ctx = LocalContext.current
        val mExoPlayer = remember(mContext) {
            ExoPlayer.Builder(ctx)
                .build()
                .also { exoPlayer ->
                    val mediaItem = MediaItem.fromUri("https://storage.googleapis.com/exoplayer-test-media-0/play.mp3")
                    exoPlayer.setMediaItem(mediaItem)
                }
        }*/

        // Implementing ExoPlayer
        /*AndroidView(factory = { context ->
            PlayerView(context).apply {
                player = exoPlayer
            }
        })*/
    }
}
