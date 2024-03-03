package fond.predanie.medialib.presentation.screens

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.List
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
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
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter.State.Empty.painter
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.slaviboy.composeunits.dh
import fond.predanie.medialib.presentation.playerService.playlistAccordion.PlaylistAccordionGroup
import fond.predanie.medialib.presentation.playerService.playlistAccordion.PlaylistAccordionModel
import fund.predanie.medialib.R
import fund.predanie.medialib.presentation.MainViewModel
import io.appmetrica.analytics.impl.x
import kotlin.time.DurationUnit
import kotlin.time.toDuration

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


    var backgroundColor: Color? = null
    var textColor: Color? = null

    if (isSystemInDarkTheme()){
        backgroundColor = Color.Black
        textColor = Color.Gray
    } else {
        textColor = Color.Black
        backgroundColor = Color.White
    }

    Box(
        modifier = Modifier.fillMaxSize()

            .background(color = backgroundColor)
    ) {

        LaunchedEffect(Unit) {
            mainViewModel.updateCurrentPlaylistToUi(uiState.playerController)

            if(uiState.playerController?.mediaMetadata?.title.toString() == "null"){
                navController.navigate("MainScreen")
            }
        }

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

                .background(color = backgroundColor)
        ) {
            val boxSize = with(LocalDensity.current) { 0.01.dh.toPx() }
            if (!isSystemInDarkTheme()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(uiState.playerController?.mediaMetadata?.artworkUri.toString())
                        .memoryCachePolicy(CachePolicy.ENABLED)
                        .diskCachePolicy(CachePolicy.ENABLED)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
            Column (
                Modifier
                .padding(top = 0.3.dh)
                .align(Alignment.TopCenter)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color.Transparent, Color.White),
                        start = Offset(0f, 0f), // top left corner
                        end = Offset(1f, boxSize) // bottom right corner
                    )
                )

                    .background(color = backgroundColor)
                ) {

                Column(
                    modifier = Modifier.padding(horizontal = 24.dp)
                        .background(color = backgroundColor)
                ) {
                    Text(
                        text = uiState.playerController?.mediaMetadata?.title.toString(),
                        maxLines = 2,
                        fontSize = 25.sp,
                        overflow = TextOverflow.Ellipsis,
                        color = textColor,
                        modifier = Modifier.padding(5.dp)
                        .wrapContentHeight()
                    )

                    Text(
                        text = uiState.playerController?.mediaMetadata?.artist.toString(),
                        //style = MaterialTheme.typography.subtitle1,
                        //color = MaterialTheme.colors.onBackground,
                        maxLines = 2,
                        fontSize = 20.sp,
                        color = textColor,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.graphicsLayer {
                            alpha = 0.60f
                        })

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {

                        var lenght = uiState.playerController?.duration?.toFloat()

                        if (lenght != null) {
                            if(lenght < 0){
                                lenght = 1F
                            }
                        }

                        Slider(
                            value = uiState.currentSongPosition.toFloat(),
                            modifier = Modifier.fillMaxWidth(),
                            valueRange = 0f..lenght!!,
                            onValueChange = { newPosition ->
                                uiState.playerController?.seekTo(newPosition.toLong())
                            },
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CompositionLocalProvider() {
                            val duration = uiState.currentSongPosition.toLong().toDuration(DurationUnit.MILLISECONDS)
                            val durationString = duration.toComponents { hours, minutes, seconds, _ ->
                                "%02d:%02d:%02d".format(hours, minutes, seconds)
                            }
                            Text(
                                durationString,
                                color = textColor,
                                //style = MaterialTheme.typography.body2
                            )
                        }
                        CompositionLocalProvider() {
                            val duration = uiState.playerController?.duration?.toDuration(DurationUnit.MILLISECONDS)
                            val durationString = duration?.toComponents { hours, minutes, seconds, _ ->
                                "%02d:%02d:%02d".format(hours, minutes, seconds)
                            }
                            if (durationString != null) {
                                Text(
                                    durationString,
                                    color = textColor,
                                    //style = MaterialTheme.typography.body2
                                )
                            }
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Icon(
                            painter = rememberAsyncImagePainter(androidx.media3.ui.R.drawable.exo_styled_controls_previous),
                            contentDescription = "Skip Previous",
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable(onClick = { uiState.playerController?.previous() })
                                .padding(1.dp)
                                .size(32.dp)
                        )
                        val painter = rememberAsyncImagePainter(
                            if (uiState.playerController?.isPlaying == true) {
                                androidx.media3.ui.R.drawable.exo_icon_pause
                            } else {
                                androidx.media3.ui.R.drawable.exo_icon_play
                            }
                        )
                        Icon(
                            painter = painter,
                            contentDescription = "Play",
                            //tint = MaterialTheme.colors.background,
                            modifier = Modifier
                                .clip(CircleShape)
                                //.background(MaterialTheme.colors.onBackground)
                                .clickable(onClick = {
                                    if (uiState.playerController?.isPlaying == true) {
                                        uiState.playerController?.stop()
                                    } else {
                                        uiState.playerController?.play()
                                    }
                                })
                                .size(100.dp)
                                .padding(1.dp)
                        )
                        Icon(
                            painter = rememberAsyncImagePainter(androidx.media3.ui.R.drawable.exo_styled_controls_next),
                            contentDescription = "Skip Next",
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable(onClick = { uiState.playerController?.next() })
                                .padding(12.dp)
                                .size(32.dp)
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)

                        .fillMaxHeight()
                    ) {
                        Text("x1",
                            modifier = Modifier.width(50.dp)
                                .clickable() {
                                    uiState.playerController?.setPlaybackSpeed(
                                        1.0F
                                    )
                            })
                        Text("x1.2",
                            modifier = Modifier.width(50.dp)
                            .clickable() {
                                uiState.playerController?.setPlaybackSpeed(
                                    1.2F
                                )
                            })
                        Text("x1.5",
                            modifier = Modifier.width(50.dp)
                                .clickable() {
                                    uiState.playerController?.setPlaybackSpeed(
                                        1.5F
                                    )
                                })
                        Text("x1.8",
                            modifier = Modifier.width(50.dp)
                                .clickable() {
                                    uiState.playerController?.setPlaybackSpeed(
                                        1.8F
                                    )
                                })
                        Text("x2",
                            modifier = Modifier.width(50.dp)
                                .clickable() {
                                    uiState.playerController?.setPlaybackSpeed(
                                        2F
                                    )
                                })
                        Icon(
                            imageVector = Icons.Rounded.List,
                            contentDescription = "Skip Previous",
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable(onClick = { navController.navigate("PlaylistScreen") })
                                .padding(12.dp)
                                .size(32.dp)
                        )

                    }
                }
            }

        }


    }
}
