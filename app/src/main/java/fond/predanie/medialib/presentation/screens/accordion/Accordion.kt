package fund.predanie.medialib.presentation.screens.accordion

import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import io.appmetrica.analytics.AppMetrica
import fund.predanie.medialib.data.room.AppDatabase
import fund.predanie.medialib.domain.models.api.items.Tracks
import fund.predanie.medialib.presentation.MainViewModel
import fund.predanie.medialib.presentation.UIState
import fund.predanie.medialib.presentation.downloadService.DownloadManagerSingleton
import fund.predanie.medialib.presentation.downloadService.PredanieDownloadService
import fund.predanie.medialib.presentation.screens.accordion.theme.*
import fund.predanie.medialib.R


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
    showButtons: Boolean = true,
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
                itemId = itemId,
                showButtons = showButtons
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
    itemId: String,
    showButtons: Boolean?
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
                                itemId = itemId,
                                showButtons = showButtons
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
                        .padding(start = 15.dp)
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
                                itemId,
                                showButtons,
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
    itemId: String,
    showButtons: Boolean?
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

                mainViewModel.updateCurrentPlaylistToUi(uiState.playerController)

                uiState.playerController?.play()
                navController.navigate("PlayerScreen")

                mainViewModel.playerVisible()

                //Событие статистики
                val eventParametersPlay: MutableMap<String, Any> = HashMap()
                eventParametersPlay["Composition"] = model.composition.toString()
                eventParametersPlay["FileName"] = model.name.toString()
                AppMetrica.reportEvent("PlayFile", eventParametersPlay)
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

            if (showButtons == true) {
                Row(modifier = Modifier.width(90.dp)) {

                    var isFavorite by remember {
                        mutableStateOf(
                            mainViewModel.isTrackFavorite(
                                model.url.toString(),
                                context
                            )
                        )
                    }

                    val color =
                        if (isFavorite) (Color(android.graphics.Color.parseColor("#FFD600"))) else (Color(
                            android.graphics.Color.parseColor("#000000")
                        ))

                    if (!isFavorite) {
                        Icon(
                            painter = painterResource(R.drawable.bookmark),
                            contentDescription = "Fav",
                            modifier = Modifier
                                .size(30.dp)
                                .clickable {
                                    mainViewModel.setTrackToFavorites(
                                        itemId = itemId, title = model.name.toString(),
                                        compositionid = itemId, uri = model.url.toString(), context
                                    )
                                    isFavorite = !isFavorite

                                    //Событие статистики
                                    val eventParametersPlay: MutableMap<String, Any> = HashMap()
                                    eventParametersPlay["name"] = model.name.toString()
                                    AppMetrica.reportEvent("SetFavorite", eventParametersPlay)
                                }
                                .fillMaxWidth(),
                            tint = color
                        )
                    } else {
                        Icon(
                            painter = painterResource(R.drawable.bookmark),
                            contentDescription = "Fav",
                            modifier = Modifier
                                .size(30.dp)
                                .clickable {
                                    mainViewModel.removeTrackFromFavorite(
                                        model.url.toString(),
                                        context
                                    )
                                    isFavorite = !isFavorite
                                }
                                .fillMaxWidth(),
                            tint = color
                        )
                    }

                    var isDownloaded by remember {
                        mutableStateOf(
                            DownloadManagerSingleton.getInstance(
                                context = context
                            ).downloadIndex.getDownload(
                                "${itemId}_${model.url}"
                            )?.state == 3
                        )
                    }

                    if (mainViewModel.isInternetConnected(context)) {
                        val downloadColor =
                            if (isDownloaded) (Color(android.graphics.Color.parseColor("#FFD600"))) else (Color(
                                android.graphics.Color.parseColor("#000000")
                            ))

                        if (!isDownloaded) {
                            Icon(
                                painter = painterResource(R.drawable.download),
                                contentDescription = "Fav",
                                modifier = Modifier
                                    .size(30.dp)
                                    .clickable {
                                        val downloadRequest = DownloadRequest
                                            .Builder(
                                                "${itemId}_${model.url.toString()}",
                                                Uri.parse(model.url)
                                            )
                                            .build()

                                        DownloadService.sendAddDownload(
                                            context,
                                            PredanieDownloadService::class.java,
                                            downloadRequest,
                                            /* foreground = */ false
                                        )
                                        mainViewModel.loadDownloadedCompositions(context)
                                        isDownloaded = !isDownloaded
                                        Toast
                                            .makeText(
                                                context, "Загружается",
                                                Toast.LENGTH_SHORT
                                            )
                                            .show()
                                    }
                                    .fillMaxWidth(),
                                tint = downloadColor
                            )
                        } else {
                            Icon(
                                painter = painterResource(R.drawable.download),
                                contentDescription = "Fav",
                                modifier = Modifier
                                    .size(30.dp)
                                    .clickable {
                                        DownloadService.sendRemoveDownload(
                                            context,
                                            PredanieDownloadService::class.java,
                                            "${itemId}_${model.url}",
                                            /* foreground = */ false
                                        )
                                        mainViewModel.loadDownloadedCompositions(context)
                                        isDownloaded = !isDownloaded
                                        Toast
                                            .makeText(
                                                context, "Удалено из загрузок",
                                                Toast.LENGTH_SHORT
                                            )
                                            .show()
                                    }
                                    .fillMaxWidth(),
                                tint = downloadColor
                            )
                        }

                        Icon(
                            painter = painterResource(R.drawable.add),
                            contentDescription = "Добавить в очередь",
                            modifier = Modifier
                                .size(30.dp)
                                .clickable {
                                    mainViewModel.addToQueue(model, context)
                                }
                                .fillMaxWidth(),
                            tint = Color.Black
                        )
                    }
                }
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