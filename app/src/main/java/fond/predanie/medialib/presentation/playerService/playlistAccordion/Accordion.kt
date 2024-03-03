package fond.predanie.medialib.presentation.playerService.playlistAccordion

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaItem
import androidx.navigation.NavHostController
import fund.predanie.medialib.data.room.AppDatabase
import fund.predanie.medialib.presentation.MainViewModel
import fund.predanie.medialib.presentation.UIState
import fund.predanie.medialib.presentation.screens.accordion.theme.Gray200
import fund.predanie.medialib.presentation.screens.accordion.theme.Gray600
import fund.predanie.medialib.presentation.screens.accordion.theme.Green500
import fund.predanie.medialib.presentation.screens.accordion.theme.*
import fund.predanie.medialib.R

data class PlaylistAccordionModel(
    val header: String,
    val rows: MutableList<MediaItem>
) {
    data class Row(
        val name: String,
        val price: String
    )
}

@Composable
fun PlaylistAccordionGroup(
    modifier: Modifier = Modifier,
    group: List<PlaylistAccordionModel>,
    exp: Boolean = false,
    playerList: ArrayList<MediaItem>,
    navController: NavHostController,
    mainViewModel: MainViewModel,
    globalItemCount: Int,
    partCount: Int,
) {
    val uiState by mainViewModel.uiState.collectAsState()



    val context = LocalContext.current

    Column(modifier = modifier) {
        group.forEach {
            PlaylistAccordion(
                model = it,
                exp = exp,
                playerList = playerList,
                navController = navController,
                mainViewModel = mainViewModel,
                uiState = uiState,
                globalItemCount = globalItemCount,
                partCount = partCount
            )
        }
    }
}

@Composable
fun PlaylistAccordion(
    modifier: Modifier = Modifier,
    model: PlaylistAccordionModel,
    exp: Boolean = false,
    playerList: ArrayList<MediaItem>,
    navController: NavHostController,
    mainViewModel: MainViewModel,
    uiState: UIState,
    globalItemCount: Int,
    partCount: Int
) {
    var expanded by remember { mutableStateOf(false) }

    //Без подкатегории
    Column(modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        if (exp) {
            AnimatedVisibility(visible = true) {
                Surface(
                    color = White,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Gray200),
                    tonalElevation = 1.dp,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    LazyColumn {
                        items(model.rows.count()) { index ->
                            PlaylistAccordionRow(
                                model.rows[index],
                                index+1,
                                playerList = playerList,
                                navController = navController,
                                mainViewModel = mainViewModel,
                                uiState = uiState,
                                globalItemIndex = globalItemCount,
                                partCount
                            )
                        }
                    }
                }
            }
        } else {
            //С подкатегорией
            PlaylistAccordionHeader(title = model.header, isExpanded = expanded) {
                expanded = !expanded
            }

            AnimatedVisibility(visible = expanded) {
                Surface(
                    color = White,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Gray200),
                    tonalElevation = 1.dp,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    LazyColumn {
                        items(model.rows.count()) { index ->
                            PlaylistAccordionRow(
                                model.rows[index],
                                index+1,
                                playerList = playerList,
                                navController = navController,
                                mainViewModel = mainViewModel,
                                uiState = uiState,
                                globalItemIndex = globalItemCount,
                                partCount
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PlaylistAccordionHeader(
    title: String = "Header",
    isExpanded: Boolean = false,
    onTapped: () -> Unit = {}
) {
    val degrees = if (isExpanded) 180f else 0f

    var textColor: Color? = null
    var backgroundColor: Color? = null

    if (isSystemInDarkTheme()){
        textColor = Color.White
        backgroundColor = Color.Black
    } else {
        textColor = Color(android.graphics.Color.parseColor("#2F2F2F"))
        backgroundColor = Color.White
    }

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Gray200),
        tonalElevation = 8.dp,
    ) {
        Row(
            modifier = Modifier
                .clickable { onTapped() }
                .padding(16.dp)
                .background(backgroundColor),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // style = accordionHeaderStyle,
            Text(title, Modifier.weight(1f), color = textColor)
            Surface(shape = CircleShape, color = Color.DarkGray) {
                Icon(
                    painter = painterResource(R.drawable.double_arrow),
                    contentDescription = "arrow-down",
                    modifier = Modifier.rotate(degrees),
                    tint = White
                )
            }
        }
    }
}

@Composable
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
fun PlaylistAccordionRow(
    model: MediaItem,
    index: Int,
    playerList: ArrayList<MediaItem>,
    navController: NavHostController,
    mainViewModel: MainViewModel,
    uiState: UIState,
    globalItemIndex: Int,
    partCount: Int
) {
    val itemPosition = AppDatabase.getInstance(LocalContext.current).filePositionDao()
        .getPositionByFileId(model.mediaId)?.position

    var textColor: Color? = null
    var backgroundColor: Color? = null

    if (isSystemInDarkTheme()){
        textColor = Color.White
        backgroundColor = Color.Black
    } else {
        textColor = Color(android.graphics.Color.parseColor("#2F2F2F"))
        backgroundColor = Color.White
    }

    Column(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .background(backgroundColor)
            .clickable {
                if (itemPosition != null) {
                    uiState.playerController?.seekTo(
                        globalItemIndex - partCount + index - 1,
                        itemPosition
                    )
                } else {
                    uiState.playerController?.seekTo(globalItemIndex - partCount + index - 1, 0)
                }
                uiState.playerController?.play()
            },
    ) {
        val context = LocalContext.current
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(15.dp)
        ) {
            //style = tags

                Text(
                    "${index.toString()}.",
                    Modifier
                        .wrapContentWidth()
                        .padding(end = 5.dp), color = textColor
                )

                Text(model.mediaMetadata.title.toString(), Modifier.weight(1f), color = textColor)

            Icon(
                painter = painterResource(R.drawable.up),
                contentDescription = "Fav",
                modifier = Modifier
                    .size(25.dp)
                    .clickable {
                        if (index - 1 != 0) {
                            uiState.playerController?.moveMediaItem(index - 1, index - 2)
                            mainViewModel.updateCurrentPlaylistToUi(uiState.playerController)
                        }
                    }
                    .fillMaxWidth(),
                tint = textColor
            )

            Icon(
                painter = painterResource(R.drawable.down),
                contentDescription = "Fav",
                modifier = Modifier
                    .size(25.dp)
                    .clickable {
                        uiState.playerController?.moveMediaItem(index - 1, index)
                        mainViewModel.updateCurrentPlaylistToUi(uiState.playerController)
                    }
                    .fillMaxWidth(),
                tint = textColor
            )

            Icon(
                painter = painterResource(R.drawable.remove),
                contentDescription = "Delete from playlist",
                modifier = Modifier
                    .size(25.dp)
                    .clickable {
                        uiState.playerController?.removeMediaItem(index - 1)
                        mainViewModel.updateCurrentPlaylistToUi(uiState.playerController)
                    }
                    .fillMaxWidth(),
                tint = textColor
            )
        }

        Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp, end = 10.dp, top = 0.dp)) {
            Text("Открыть произведение", fontSize = 10.sp, modifier = Modifier.clickable {
                navController.navigate("ItemScreen/${model.mediaMetadata.compilation}")
            })
            if(model.mediaMetadata.artist != null) {
                Text(" / ", fontSize = 10.sp)
                Text(
                    model.mediaMetadata.artist.toString(),
                    fontSize = 10.sp,
                    maxLines = 1,
                    modifier = Modifier.width(150.dp)
                        .background(backgroundColor)
                )
            }
        }

        val timeQuery = AppDatabase.getInstance(LocalContext.current).filePositionDao()
            .getPositionByFileId(model.mediaId)

        if (timeQuery != null) {
            if (timeQuery.finished) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth(),
                    progress = 1.toFloat(),
                    color = Green500
                )
            } else {
                val completedTime = timeQuery.position.toFloat()
                val duration = timeQuery.filelength.toFloat()
                val progress = completedTime.div(duration)

                LinearProgressIndicator(
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth(), progress = progress, color = Green500
                )
            }
        }
    }
}