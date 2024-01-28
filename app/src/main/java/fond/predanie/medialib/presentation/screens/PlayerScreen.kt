package fond.predanie.medialib.presentation.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.ui.PlayerView
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import fund.predanie.medialib.R
import fund.predanie.medialib.presentation.MainViewModel
import kotlinx.coroutines.delay

@SuppressLint("RememberReturnType")
@OptIn(ExperimentalFoundationApi::class)
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
fun PlayerScreen(mainViewModel: MainViewModel, navController: NavHostController, action: String?) {
    val context = LocalContext.current
    val uiState by mainViewModel.uiState.collectAsState()
    val playlistsList = uiState.playlistsList?.collectAsLazyPagingItems()

    val player = uiState.playerController

    var currentPosition = remember {
        mutableStateOf(0)
    }

    /*LaunchedEffect(key1 = player?.currentPosition, key2 = player?.isPlaying) {
        delay(1000)
        currentPosition = player?.currentPosition
    }*/

    Column(
        Modifier
            .fillMaxSize()
            .height(300.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        //verticalArrangement = Arrangement.Center
    ) {

        AndroidView(
            factory = { context ->
                PlayerView(context).apply {
                    uiState.playerController
                    controllerHideOnTouch = true
                    setShowPreviousButton(false)
                    setShowNextButton(false)
                    setShowRewindButton(false)
                    setShowVrButton(false)
                    setShowFastForwardButton(false)
                    controllerAutoShow = false
                    controllerShowTimeoutMs = 0
                }
            },
            update = {
                it.player = uiState.playerController
            }
        )
        Spacer(modifier = Modifier.height(54.dp))
        /*Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
        ) {

            TrackSlider(
                value = sliderPosition.longValue.toFloat(),
                onValueChange = {
                    sliderPosition.longValue = it.toLong()
                },
                onValueChangeFinished = {
                    currentPosition.longValue = sliderPosition.longValue
                    player.seekTo(sliderPosition.longValue)
                },
                songDuration = totalDuration.longValue.toFloat()
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {

                Text(
                    text = (currentPosition.longValue).convertToText(),
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp),
                    color = Color.Black,
                    style = TextStyle(fontWeight = FontWeight.Bold)
                )

                val remainTime = totalDuration.longValue - currentPosition.longValue
                Text(
                    text = if (remainTime >= 0) remainTime.convertToText() else "",
                    modifier = Modifier
                        .padding(8.dp),
                    color = Color.Black,
                    style = TextStyle(fontWeight = FontWeight.Bold)
                )
            }
        }
*/
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ControlButton(icon = androidx.media3.ui.R.drawable.exo_icon_previous, size = 40.dp, onClick = {
                player?.seekToPreviousMediaItem()
            })
            Spacer(modifier = Modifier.width(20.dp))
            ControlButton(
                icon = if (player?.isPlaying == true) androidx.media3.ui.R.drawable.exo_icon_pause else R.drawable.playall,
                size = 100.dp,
                onClick = {
                    if (player?.isPlaying == true) {
                        player.pause()
                    } else {
                        player?.play()
                    }
                })
            Spacer(modifier = Modifier.width(20.dp))
            ControlButton(icon = androidx.media3.ui.R.drawable.exo_icon_next, size = 40.dp, onClick = {
                player?.seekToNextMediaItem()
            })
        }
    }
}

/***
 * Player control button
 */
@Composable
fun ControlButton(icon: Int, size: Dp, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .clickable {
                onClick()
            }, contentAlignment = Alignment.Center
    ) {
        Icon(
            modifier = Modifier.size(size / 1.5f),
            painter = painterResource(id = icon),
            tint = Color.Black,
            contentDescription = null
        )
    }
}

/**
 * Tracks and visualizes the song playing actions.
 */
@Composable
fun TrackSlider(
    value: Float,
    onValueChange: (newValue: Float) -> Unit,
    onValueChangeFinished: () -> Unit,
    songDuration: Float
) {
    Slider(
        value = value,
        onValueChange = {
            onValueChange(it)
        },
        onValueChangeFinished = {

            onValueChangeFinished()

        },
        valueRange = 0f..songDuration,
        colors = SliderDefaults.colors(
            thumbColor = Color.Black,
            activeTrackColor = Color.DarkGray,
            inactiveTrackColor = Color.Gray,
        )
    )
}