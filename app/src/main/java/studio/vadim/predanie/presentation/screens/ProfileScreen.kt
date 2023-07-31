package studio.vadim.predanie.presentation.screens

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Player.STATE_ENDED
import androidx.media3.ui.PlayerView
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import studio.vadim.predanie.data.room.AppDatabase
import studio.vadim.predanie.R
import studio.vadim.predanie.presentation.MainViewModel
import studio.vadim.predanie.presentation.playerService.playlistAccordion.PlaylistAccordionGroup
import studio.vadim.predanie.presentation.playerService.playlistAccordion.PlaylistAccordionModel
import studio.vadim.predanie.presentation.screens.accordion.theme.Purple200
import java.util.concurrent.TimeUnit


@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@SuppressLint("ServiceCast")
@Composable
fun ProfileScreen(mainViewModel: MainViewModel, navController: NavHostController, action: String?) {
    val context = LocalContext.current
    val uiState by mainViewModel.uiState.collectAsState()

    val newItems = uiState.newList.collectAsLazyPagingItems()
    val favAuthorsList = uiState.favAuthorsList?.collectAsLazyPagingItems()
    val favCompositionsList = uiState.favCompositionsList?.collectAsLazyPagingItems()
    val favTracksList = uiState.favTracksList?.collectAsLazyPagingItems()
    val historyList = uiState.historyList?.collectAsLazyPagingItems()
    val downloadsList = uiState.downloadsList?.collectAsLazyPagingItems()

    var shouldShowControls by remember { mutableStateOf(true) }

    var isPlaying by remember { mutableStateOf(uiState.playerController?.isPlaying) }

    var totalDuration by remember { mutableStateOf(0L) }

    var currentTime by remember { mutableStateOf(0L) }

    var bufferedPercentage by remember { mutableStateOf(0) }

    var playbackState by remember { mutableStateOf(uiState.playerController?.playbackState) }

    val PLAYER_SEEK_BACK_INCREMENT = 5 * 1000L // 5 seconds
    val PLAYER_SEEK_FORWARD_INCREMENT = 10 * 1000L // 10 seconds


    val currentPlaylistFromDB =
        AppDatabase.getInstance(LocalContext.current).mainPlaylistDao().findByName("Main")

    if (!mainViewModel.isInternetConnected(context)) {
        Column(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
        ) {
            Row(modifier = Modifier.padding(top = 20.dp, start = 20.dp, bottom = 20.dp)) {
                Text(
                    modifier = Modifier.padding(top = 8.dp),
                    text = "§",
                    fontSize = 25.sp,
                    color = Color(android.graphics.Color.parseColor("#FFD600"))
                )
                Text(
                    modifier = Modifier.padding(start = 5.dp),
                    text = "Offline режим",
                    fontSize = 35.sp,
                    color = Color(android.graphics.Color.parseColor("#2F2F2F"))
                )

            }
        }
    }

    if (currentPlaylistFromDB.playlistJson[0].mediaMetadata.title != "null") {
        Column(
            Modifier
                .fillMaxSize()
                .height(300.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Box(modifier = Modifier.height(200.dp)) {
                DisposableEffect(key1 = Unit) {
                    val listener =
                        object : Player.Listener {
                            override fun onEvents(
                                player: Player,
                                events: Player.Events
                            ) {
                                super.onEvents(player, events)
                                totalDuration = player.duration.coerceAtLeast(0L)
                                currentTime = player.currentPosition.coerceAtLeast(0L)
                                bufferedPercentage = player.bufferedPercentage
                                isPlaying = player.isPlaying
                                playbackState = player.playbackState
                            }
                        }

                    uiState.playerController?.addListener(listener)

                    onDispose {
                        uiState.playerController?.removeListener(listener)
                        uiState.playerController?.release()
                    }
                }

                AndroidView(
                    modifier =
                    Modifier.clickable {
                        shouldShowControls = shouldShowControls.not()
                    },
                    factory = { context ->
                        PlayerView(context).apply {
                            player = uiState.playerController
                            useController = false
                        }
                    },
                    update = {
                        it.player = uiState.playerController
                    }
                )

                PlayerControls(
                    modifier = Modifier.fillMaxSize(),
                    isVisible = { shouldShowControls },
                    isPlaying = { isPlaying },
                    title = { uiState.playerController?.mediaMetadata?.displayTitle.toString() },
                    playbackState = { playbackState },
                    onReplayClick = { uiState.playerController?.seekBack() },
                    onForwardClick = { uiState.playerController?.seekForward() },
                    onPauseToggle = {
                        when {
                            uiState.playerController?.isPlaying == true -> {
                                // pause the video
                                uiState.playerController?.pause()
                            }
                            uiState.playerController?.isPlaying?.not() ?: true  &&
                                    playbackState == STATE_ENDED -> {
                                uiState.playerController?.seekTo(0)
                                uiState.playerController?.playWhenReady = true
                            }
                            else -> {
                                // play the video
                                // it's already paused
                                uiState.playerController?.play()
                            }
                        }
                        isPlaying = isPlaying?.not()
                    },
                    totalDuration = { totalDuration },
                    currentTime = { currentTime },
                    bufferedPercentage = { bufferedPercentage },
                    onSeekChanged = { timeMs: Float ->
                        uiState.playerController?.seekTo(timeMs.toLong())
                    }
                )
            }


            /*Image(
                painter = painterResource(id = R.drawable.bg_gradient),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )*/

            LaunchedEffect(Unit) {
                if (action == "play") {
                    uiState.playerController?.play()
                }
            }

            //Очередь воспроизведения
            Column(
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(bottom = 20.dp)) {
                    //Выводим очередь воспроизведения

                    val rows = mutableListOf<MediaItem>()


                    for (item in currentPlaylistFromDB.playlistJson) {
                        rows.add(item)
                    }

                    val parts = PlaylistAccordionModel(
                        header = "Очередь воспроизведения",
                        rows
                    )

                    val group = listOf(parts)

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

            if (mainViewModel.isInternetConnected(context)) {
                Column(
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(top = 20.dp, start = 20.dp, bottom = 20.dp)) {
                        Text(
                            modifier = Modifier.padding(top = 8.dp),
                            text = "§",
                            fontSize = 25.sp,
                            color = Color(android.graphics.Color.parseColor("#FFD600"))
                        )
                        Text(
                            modifier = Modifier.padding(start = 5.dp),
                            text = "Моя история",
                            fontSize = 35.sp,
                            color = Color(android.graphics.Color.parseColor("#2F2F2F"))
                        )
                    }
                }

                LazyRow() {
                    if (historyList != null) {
                        items(historyList.itemCount) { index ->
                            historyList[index]?.let {
                                ListRow(model = it, navController)
                            }
                        }
                    }
                }
            }

            if (mainViewModel.isInternetConnected(context)) {
                //Избранное
                Column(
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(top = 20.dp, start = 20.dp, bottom = 20.dp)) {
                        Text(
                            modifier = Modifier.padding(top = 8.dp),
                            text = "§",
                            fontSize = 25.sp,
                            color = Color(android.graphics.Color.parseColor("#FFD600"))
                        )
                        Text(
                            modifier = Modifier.padding(start = 5.dp),
                            text = "Моё избранное",
                            fontSize = 35.sp,
                            color = Color(android.graphics.Color.parseColor("#2F2F2F"))
                        )

                    }
                }

                LazyRow() {
                    if (favAuthorsList != null) {
                        items(favAuthorsList.itemCount) { index ->
                            favAuthorsList[index]?.let { ListRow(model = it, navController) }
                        }
                    }
                }

                LazyRow() {
                    if (favCompositionsList != null) {
                        items(favCompositionsList.itemCount) { index ->
                            favCompositionsList[index]?.let { ListRow(model = it, navController) }
                        }
                    }
                }

                LazyRow() {
                    if (favTracksList != null) {
                        items(favTracksList.itemCount) { index ->
                            favTracksList[index]?.let { ListRow(model = it, navController) }
                        }
                    }
                }

            }

            Column(
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(top = 20.dp, start = 20.dp, bottom = 20.dp)) {
                    Text(
                        modifier = Modifier.padding(top = 8.dp),
                        text = "§",
                        fontSize = 25.sp,
                        color = Color(android.graphics.Color.parseColor("#FFD600"))
                    )
                    Text(
                        modifier = Modifier.padding(start = 5.dp),
                        text = "Мои загрузки",
                        fontSize = 35.sp,
                        color = Color(android.graphics.Color.parseColor("#2F2F2F"))
                    )
                }
            }

            LazyRow() {
                if (downloadsList != null) {
                    items(downloadsList.itemCount) { index ->
                        downloadsList[index]?.let {
                            ListRow(model = it, navController)
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(top = 20.dp, start = 20.dp, bottom = 20.dp)) {
                    Text(
                        modifier = Modifier.padding(top = 8.dp),
                        text = "§",
                        fontSize = 25.sp,
                        color = Color(android.graphics.Color.parseColor("#FFD600"))
                    )
                    Text(
                        modifier = Modifier.padding(start = 5.dp),
                        text = "Мои настройки",
                        fontSize = 35.sp,
                        color = Color(android.graphics.Color.parseColor("#2F2F2F"))
                    )

                }
            }
            if (mainViewModel.isInternetConnected(context)) {
                //Популярное аудио
                Column(
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(top = 20.dp, start = 20.dp, bottom = 20.dp)) {
                        Text(
                            modifier = Modifier.padding(top = 8.dp),
                            text = "§",
                            fontSize = 25.sp,
                            color = Color(android.graphics.Color.parseColor("#FFD600"))
                        )
                        Text(
                            modifier = Modifier.padding(start = 5.dp),
                            text = "Мои плейлисты",
                            fontSize = 35.sp,
                            color = Color(android.graphics.Color.parseColor("#2F2F2F"))
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PlayerControls(
    modifier: Modifier,
    isVisible: () -> Boolean,
    isPlaying: () -> Boolean?,
    title: () -> String,
    playbackState: () -> Int?,
    onReplayClick: () -> Unit,
    onForwardClick: () -> Unit,
    onPauseToggle: () -> Unit,
    totalDuration: () -> Long,
    currentTime: () -> Long,
    bufferedPercentage: () -> Int,
    onSeekChanged: (Float) -> Unit
) {

    val visible = remember(isVisible()) { isVisible() }

    AnimatedVisibility(
        modifier = modifier,
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(modifier = Modifier.background(Color.Black.copy(alpha = 0.6f))) {
            TopControl(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .fillMaxWidth(),
                title = title
            )

            CenterControls(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth(),
                isPlaying = isPlaying as () -> Boolean,
                onReplayClick = onReplayClick,
                onForwardClick = onForwardClick,
                onPauseToggle = onPauseToggle,
                playbackState = playbackState as () -> Int
            )

            BottomControls(
                modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .animateEnterExit(
                        enter =
                        slideInVertically(
                            initialOffsetY = { fullHeight: Int ->
                                fullHeight
                            }
                        ),
                        exit =
                        slideOutVertically(
                            targetOffsetY = { fullHeight: Int ->
                                fullHeight
                            }
                        )
                    ),
                totalDuration = totalDuration,
                currentTime = currentTime,
                bufferedPercentage = bufferedPercentage,
                onSeekChanged = onSeekChanged
            )
        }
    }
}


@Composable
fun TopControl(modifier: Modifier = Modifier, title: () -> String) {
    val videoTitle = remember(title()) { title() }

    Text(

        modifier = modifier.padding(16.dp),
        text = videoTitle,
        style = MaterialTheme.typography.bodyLarge,
        color = Purple200
    )
}

@Composable
fun CenterControls(
    modifier: Modifier = Modifier,
    isPlaying: () -> Boolean,
    playbackState: () -> Int,
    onReplayClick: () -> Unit,
    onPauseToggle: () -> Unit,
    onForwardClick: () -> Unit
) {
    val isVideoPlaying = remember(isPlaying()) { isPlaying() }

    val playerState = remember(playbackState()) { playbackState() }

    Row(modifier = modifier, horizontalArrangement = Arrangement.SpaceEvenly) {
        IconButton(modifier = Modifier.size(40.dp), onClick = onReplayClick) {
            Image(
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                painter = painterResource(id = R.drawable.replay),
                contentDescription = "Replay 5 seconds"
            )
        }

        IconButton(modifier = Modifier.size(40.dp), onClick = onPauseToggle) {
            Image(
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                painter =
                when {
                    isVideoPlaying -> {
                        painterResource(id = androidx.media3.ui.R.drawable.exo_icon_pause)
                    }

                    isVideoPlaying.not() && playerState == STATE_ENDED -> {
                        painterResource(id = R.drawable.replay)
                    }

                    else -> {
                        painterResource(id = R.drawable.playall)
                    }
                },
                contentDescription = "Play/Pause"
            )
        }

        IconButton(modifier = Modifier.size(40.dp), onClick = onForwardClick) {
            Image(
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                painter = painterResource(id = androidx.media3.ui.R.drawable.exo_ic_forward),
                contentDescription = "Forward 10 seconds"
            )
        }
    }
}

fun Long.formatMinSec(): String {
    return if (this == 0L) {
        "..."
    } else {
        String.format(
            "%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(this),
            TimeUnit.MILLISECONDS.toSeconds(this) -
                    TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(this)
                    )
        )
    }
}

@Composable
fun BottomControls(
    modifier: Modifier = Modifier,
    totalDuration: () -> Long,
    currentTime: () -> Long,
    bufferedPercentage: () -> Int,
    onSeekChanged: (timeMs: Float) -> Unit
) {

    val duration = remember(totalDuration()) { totalDuration() }

    val videoTime = remember(currentTime()) { currentTime() }

    val buffer = remember(bufferedPercentage()) { bufferedPercentage() }

    Column(modifier = modifier.padding(bottom = 32.dp)) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Slider(
                value = buffer.toFloat(),
                enabled = false,
                onValueChange = { /*do nothing*/ },
                valueRange = 0f..100f,
                colors =
                SliderDefaults.colors(
                    disabledThumbColor = Color.Transparent,
                    disabledActiveTrackColor = Color.Gray
                )
            )

            Slider(
                modifier = Modifier.fillMaxWidth(),
                value = videoTime.toFloat(),
                onValueChange = onSeekChanged,
                valueRange = 0f..duration.toFloat(),
                colors =
                SliderDefaults.colors(
                    thumbColor = Purple200,
                    activeTickColor = Purple200
                )
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = duration.formatMinSec(),
                color = Purple200
            )

            IconButton(
                modifier = Modifier.padding(horizontal = 16.dp),
                onClick = {}
            ) {
                Image(
                    contentScale = ContentScale.Crop,
                    painter = painterResource(id = androidx.media3.ui.R.drawable.exo_ic_fullscreen_enter),
                    contentDescription = "Enter/Exit fullscreen"
                )
            }
        }
    }
}