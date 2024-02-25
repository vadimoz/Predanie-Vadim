package fond.predanie.medialib.presentation.screens

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaItem
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import fond.predanie.medialib.presentation.playerService.playlistAccordion.PlaylistAccordionGroup
import fond.predanie.medialib.presentation.playerService.playlistAccordion.PlaylistAccordionModel
import fund.predanie.medialib.R
import fund.predanie.medialib.presentation.MainViewModel

@Composable
fun PlayerScreen(mainViewModel: MainViewModel, navController: NavHostController, action: Nothing?) {
    val context = LocalContext.current
    val uiState by mainViewModel.uiState.collectAsState()
    val playlistsList = uiState.playlistsList?.collectAsLazyPagingItems()

    SongScreenBody(mainViewModel, navController)
}

@Composable
fun SongScreenBody(mainViewModel: MainViewModel, navController: NavHostController) {

    val endAnchor = LocalConfiguration.current.screenHeightDp * LocalDensity.current.density
    val anchors = mapOf(
        0f to 0, endAnchor to 1
    )

    val backgroundColor = Color.White

    val dominantColor by remember { mutableStateOf(Color.Transparent) }

    val context = LocalContext.current

    /*val imagePainter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context).data(uiState.playerController?.mediaMetadata?.artworkUri).crossfade(true).build()
    )*/

    val iconResId = R.drawable.playall
    //if (uiState.playerController.playerState == PlayerState.PLAYING) R.drawable.ic_round_pause else R.drawable.ic_round_play_arrow

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
        /*.swipeable(
            state = swipeableState,
            anchors = anchors,
            thresholds = { _, _ -> FractionalThreshold(0.34f) },
            orientation = Orientation.Vertical
        )*/
    ) {
        /*if (swipeableState.currentValue >= 1) {
            LaunchedEffect(key1 = Unit) {
                onNavigateUp()
            }
        }*/
        SongScreenContent(mainViewModel, navController)
    }
}

@Composable
fun SongScreenContent(
    mainViewModel: MainViewModel,
    navController: NavHostController
) {
    val uiState by mainViewModel.uiState.collectAsState()
    val context = LocalContext.current

    /*val gradientColors = if (isSystemInDarkTheme()) {
        listOf(
            dominantColor, MaterialTheme.colors.background
        )
    } else {
        listOf(
            MaterialTheme.colors.background, MaterialTheme.colors.background
        )
    }

    val sliderColors = if (isSystemInDarkTheme()) {
        SliderDefaults.colors(
            thumbColor = MaterialTheme.colors.onBackground,
            activeTrackColor = MaterialTheme.colors.onBackground,
            inactiveTrackColor = MaterialTheme.colors.onBackground.copy(
                alpha = ProgressIndicatorDefaults.IndicatorBackgroundOpacity
            ),
        )
    } else SliderDefaults.colors(
        thumbColor = dominantColor,
        activeTrackColor = dominantColor,
        inactiveTrackColor = dominantColor.copy(
            alpha = ProgressIndicatorDefaults.IndicatorBackgroundOpacity
        ),
    )*/

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                /*.background(
                    Brush.verticalGradient(
                        //colors = gradientColors,
                        endY = LocalConfiguration.current.screenHeightDp.toFloat() * LocalDensity.current.density
                    )
                )*/
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            Column {
                IconButton(
                    onClick = { Log.d("Player", "Nvigate") }
                ) {
                    Image(
                        imageVector = Icons.Rounded.KeyboardArrowDown,
                        contentDescription = "Close",
                        colorFilter = ColorFilter.tint(LocalContentColor.current)
                    )
                }
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp)
                ) {
                    /*Box(
                        modifier = Modifier
                            .padding(vertical = 32.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .weight(1f, fill = false)
                            .aspectRatio(1f)

                    ) {
                        AnimatedVinyl(painter = imagePainter, isSongPlaying = isSongPlaying)
                    }*/

                    Text(
                        text = uiState.playerController?.mediaMetadata?.title.toString(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = uiState.playerController?.mediaMetadata?.artist.toString(),
                        //style = MaterialTheme.typography.subtitle1,
                        //color = MaterialTheme.colors.onBackground,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.graphicsLayer {
                            alpha = 0.60f
                        })

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp)
                    ) {

                        Slider(
                            value = uiState.currentSongPosition.toFloat(),
                            modifier = Modifier.fillMaxWidth(),
                            valueRange = 0f..(uiState.playerController?.duration?.toFloat() ?: 0.toFloat()),
                            //colors = sliderColors,
                            onValueChange = { Log.d("VALUE", "SLIDER") },
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CompositionLocalProvider() {
                            Text(
                                uiState.currentSongPosition,
                                //style = MaterialTheme.typography.body2
                            )
                        }
                        CompositionLocalProvider() {
                            Text(
                                uiState.playerController?.currentPosition.toString(),
                                //style = MaterialTheme.typography.body2
                            )
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = "Skip Previous",
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable(onClick = { uiState.playerController?.previous() })
                                .padding(12.dp)
                                .size(32.dp)
                        )
                        Icon(
                            imageVector = Icons.Rounded.Refresh,
                            contentDescription = "Replay 10 seconds",
                            modifier = Modifier
                                .clip(CircleShape)
                                //.clickable(onClick = onRewind)
                                .padding(12.dp)
                                .size(32.dp)
                        )
                        Icon(
                            painter = painterResource(R.drawable.playall),
                            contentDescription = "Play",
                            //tint = MaterialTheme.colors.background,
                            modifier = Modifier
                                .clip(CircleShape)
                                //.background(MaterialTheme.colors.onBackground)
                                .clickable(onClick = { uiState.playerController?.play() })
                                .size(64.dp)
                                .padding(8.dp)
                        )
                        Icon(
                            imageVector = Icons.Rounded.Refresh,
                            contentDescription = "Forward 10 seconds",
                            modifier = Modifier
                                .clip(CircleShape)
                                //.clickable(onClick = onForward)
                                .padding(12.dp)
                                .size(32.dp)
                        )
                        Icon(
                            imageVector = Icons.Rounded.ArrowForward,
                            contentDescription = "Skip Next",
                            modifier = Modifier
                                .clip(CircleShape)
                                //.clickable(onClick = playNextSong)
                                .padding(12.dp)
                                .size(32.dp)
                        )

                    }
                    val currentPlaylistFromDB = uiState.mainPlaylist

                    Row(modifier = Modifier.padding(bottom = 5.dp)) {
                        //Выводим очередь воспроизведения

                        val rows = mutableListOf<MediaItem>()

                        if (currentPlaylistFromDB != null) {
                            for (item in currentPlaylistFromDB.playlistJson) {
                                rows.add(item)
                            }
                        }

                        val parts = PlaylistAccordionModel(
                            header = "Очередь воспроизведения",
                            rows
                        )

                        val group = listOf(parts)

                        if (currentPlaylistFromDB != null) {
                            PlaylistAccordionGroup(
                                modifier = Modifier.padding(top = 8.dp),
                                group = group,
                                exp = false,
                                playerList = currentPlaylistFromDB.playlistJson,
                                navController = navController,
                                mainViewModel = mainViewModel,
                                globalItemCount = currentPlaylistFromDB.playlistJson.count(),
                                partCount = currentPlaylistFromDB.playlistJson.count()
                            )
                        }
                    }

                    Text(text = "Очистить", fontSize = 12.sp, modifier = Modifier.clickable {
                        mainViewModel.cleanQueue(context)
                    })
                }
            }

        }


    }
}
