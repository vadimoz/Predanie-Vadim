package studio.vadim.predanie.presentation.screens.accordion

import android.net.Uri
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
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
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.offline.DownloadRequest
import androidx.media3.exoplayer.offline.DownloadService
import androidx.navigation.NavHostController
import studio.vadim.predanie.R
import studio.vadim.predanie.data.room.AppDatabase
import studio.vadim.predanie.domain.models.api.items.Tracks
import studio.vadim.predanie.presentation.downloadService.PredanieDownloadService
import studio.vadim.predanie.presentation.MainViewModel
import studio.vadim.predanie.presentation.UIState
import studio.vadim.predanie.presentation.screens.accordion.theme.Gray200
import studio.vadim.predanie.presentation.screens.accordion.theme.Gray600
import studio.vadim.predanie.presentation.screens.accordion.theme.Green500
import studio.vadim.predanie.presentation.screens.accordion.theme.*

data class AccordionModel(
    val header: String,
    val rows: MutableList<Tracks>
) {
    data class Row(
        val name: String,
        val price: String
    )
}

@Composable
fun AccordionGroup(
    modifier: Modifier = Modifier,
    group: List<AccordionModel>,
    exp: Boolean = false,
    playerList: ArrayList<MediaItem>,
    navController: NavHostController,
    mainViewModel: MainViewModel,
    globalItemCount: Int,
    partCount: Int,
    itemId: String,
) {
    val uiState by mainViewModel.uiState.collectAsState()

    Column(modifier = modifier) {
        group.forEach {
            Accordion(
                model = it,
                exp = exp,
                playerList = playerList,
                navController = navController,
                mainViewModel = mainViewModel,
                uiState = uiState,
                globalItemCount = globalItemCount,
                partCount = partCount,
                itemId = itemId
            )
        }
    }
}

@Composable
fun Accordion(
    modifier: Modifier = Modifier,
    model: AccordionModel,
    exp: Boolean = false,
    playerList: ArrayList<MediaItem>,
    navController: NavHostController,
    mainViewModel: MainViewModel,
    uiState: UIState,
    globalItemCount: Int,
    partCount: Int,
    itemId: String
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
                    Column {

                        var counter = 1
                        for (row in model.rows) {
                            AccordionRow(
                                row,
                                counter,
                                playerList = playerList,
                                navController = navController,
                                mainViewModel = mainViewModel,
                                uiState = uiState,
                                globalItemIndex = globalItemCount,
                                partCount,
                                itemId = itemId
                            )
                            Divider(color = Gray200, thickness = 1.dp)
                            counter += 1
                        }
                    }
                }
            }
        } else {
            //С подкатегорией
            AccordionHeader(title = model.header, isExpanded = expanded) {
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
                    Column {
                        var counter = 1
                        for (row in model.rows) {
                            AccordionRow(
                                row,
                                counter,
                                playerList = playerList,
                                navController = navController,
                                mainViewModel = mainViewModel,
                                uiState = uiState,
                                globalItemIndex = globalItemCount,
                                partCount = partCount,
                                itemId
                            )
                            Divider(color = Gray200, thickness = 1.dp)
                            counter += 1
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AccordionHeader(
    title: String = "Header",
    isExpanded: Boolean = false,
    onTapped: () -> Unit = {}
) {
    val degrees = if (isExpanded) 180f else 0f

    Surface(
        color = White,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Gray200),
        tonalElevation = 8.dp,
    ) {
        Row(
            modifier = Modifier
                .clickable { onTapped() }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // style = accordionHeaderStyle,
            Text(title, Modifier.weight(1f), color = Gray600)
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
fun AccordionRow(
    model: Tracks,
    index: Int,
    playerList: ArrayList<MediaItem>,
    navController: NavHostController,
    mainViewModel: MainViewModel,
    uiState: UIState,
    globalItemIndex: Int,
    partCount: Int,
    itemId: String
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .clickable {
                val itemPosition = AppDatabase
                    .getInstance(context)
                    .filePositionDao()
                    .getPositionByFileId(model.id)?.position

                uiState.playerController?.setMediaItems(playerList)
                if (itemPosition != null) {
                    uiState.playerController?.seekTo(
                        globalItemIndex - partCount + index - 1,
                        itemPosition
                    )
                } else {
                    uiState.playerController?.seekTo(globalItemIndex - partCount + index - 1, 0)
                }
                navController.navigate("ProfileScreen/play")
            },
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(15.dp)
        ) {
            //style = tags

            Text(
                "${index.toString()}.",
                Modifier
                    .wrapContentWidth()
                    .padding(end = 5.dp), color = Gray600
            )
            Text(model.name.toString(), Modifier.weight(1f), color = Gray600)

            Column(
                modifier = Modifier
                    .wrapContentHeight()
            ) {
                //style = bodyBold

                if (model.time != null) {
                    val seconds: Int = model.time as Int
                    val S = seconds % 60
                    var H = seconds / 60
                    val M = H % 60
                    H = H / 60

                    Text(
                        text = "$H:$M:$S",
                        modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
                        color = Color.Black
                    )
                }

                Text(text = "Скачать", modifier = Modifier
                    .clickable {
                        val downloadRequest = DownloadRequest.Builder("${itemId}_${model.url.toString()}", Uri.parse(model.url)).build()
                        Log.d("downloadRequest" , downloadRequest.uri.toString())

                        DownloadService.sendAddDownload(
                            context,
                            PredanieDownloadService::class.java,
                            downloadRequest,
                            /* foreground = */ false
                        )
                    })

                /*Surface(color = Green500, shape = RoundedCornerShape(8.dp), tonalElevation = 2.dp) {
                    Text(
                        text = "Запустить",
                        modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
                        color = White
                    )
                }*/
            }
        }

        val timeQuery = AppDatabase.getInstance(LocalContext.current).filePositionDao()
            .getPositionByFileId(model.id)

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