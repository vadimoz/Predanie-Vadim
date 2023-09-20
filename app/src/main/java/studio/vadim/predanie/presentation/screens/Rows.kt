package studio.vadim.predanie.presentation.screens

import android.content.Context
import android.net.Uri
import android.text.Html
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.slaviboy.composeunits.dh
import io.appmetrica.analytics.AppMetrica
import studio.vadim.predanie.R
import studio.vadim.predanie.data.room.DownloadedCompositions
import studio.vadim.predanie.data.room.FavoriteAuthors
import studio.vadim.predanie.data.room.FavoriteCompositions
import studio.vadim.predanie.data.room.FavoriteTracks
import studio.vadim.predanie.data.room.HistoryCompositions
import studio.vadim.predanie.data.room.UserPlaylist
import studio.vadim.predanie.domain.models.api.items.AuthorCompositions
import studio.vadim.predanie.domain.models.api.lists.Categories
import studio.vadim.predanie.domain.models.api.lists.Compositions
import studio.vadim.predanie.domain.models.api.lists.Entities
import studio.vadim.predanie.domain.models.api.lists.ResponceBlogListModel
import studio.vadim.predanie.domain.models.api.lists.VideoData
import studio.vadim.predanie.presentation.MainViewModel


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ListRow(
    model: ResponceBlogListModel,
    navController: NavHostController,
    mainViewModel: MainViewModel
) {
    val uiState by mainViewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .width(250.dp)
            .padding(20.dp)
    ) {

        var image = model.Embedded?.wp_featuredmedia?.get(0)?.source_url
        if (image == null) {
            image = "https://predanie.ru/img/no-image/work_200.png"
        }
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(image)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .diskCachePolicy(CachePolicy.ENABLED)
                .build(),
            contentDescription = null,
            modifier = Modifier
                .clickable {
                    navController.navigate("PostScreen/${model.id}")
                }
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .size(250.dp),
            contentScale = ContentScale.Crop
        )
        Text(
            modifier = Modifier
                .clickable {
                    navController.navigate("PostScreen/${model.id}")
                }
                .padding(top = 10.dp),

            lineHeight = 33.sp,
            fontSize = 28.sp,
            text = model.title?.rendered.toString()
        )
        Text(
            modifier = Modifier
                .clickable {
                    navController.navigate("PostScreen/${model.id}")
                }
                .padding(top = 10.dp),

            lineHeight = 20.sp,
            fontSize = 15.sp,
            text = Html.fromHtml(model.excerpt?.rendered.toString()).toString()
        )
    }
}

@Composable
fun ListRow(model: VideoData, navController: NavHostController, mainViewModel: MainViewModel) {
    val uiState by mainViewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .width(300.dp)
    ) {
        var image = model.attributes?.image
        if (image == null) {
            image = "https://predanie.ru/img/no-image/work_200.png"
        }
        Box() {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(image)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .clickable {
                        val mediaItems = arrayListOf<MediaItem>()

                        mediaItems.add(
                            MediaItem
                                .Builder()
                                .setUri(model.attributes?.url)
                                .setMediaId(model.attributes?.url.toString())
                                .setMediaMetadata(
                                    MediaMetadata
                                        .Builder()
                                        .setArtworkUri(Uri.parse(model.attributes?.image ?: ""))
                                        .setTitle(model.attributes?.title)
                                        .setDisplayTitle(model.attributes?.title)
                                        .build()
                                )
                                .build()
                        )
                        uiState.playerController?.removeMediaItems(0, 100000)
                        uiState.playerController?.addMediaItems(mediaItems)
                        uiState.playerController?.prepare()
                        uiState.playerController?.play()

                        mainViewModel.updateCurrentPlaylistToUi(uiState.playerController)

                        navController.navigate("ProfileScreen/play")

                        //Событие статистики
                        val eventParameters: MutableMap<String, Any> = HashMap()
                        eventParameters["name"] = model.attributes?.title.toString()
                        AppMetrica.reportEvent("VideoClick", eventParameters)
                    }
                    .fillMaxWidth()
                    .padding(20.dp),
                contentScale = ContentScale.Crop
            )
        }
        Text(
            modifier = Modifier
                .clickable {
                    val mediaItems = arrayListOf<MediaItem>()

                    mediaItems.add(
                        MediaItem
                            .Builder()
                            .setUri(model.attributes?.url)
                            .setMediaId(model.attributes?.url.toString())
                            .setMediaMetadata(
                                MediaMetadata
                                    .Builder()
                                    .setArtworkUri(Uri.parse(model.attributes?.image ?: ""))
                                    .setTitle(model.attributes?.title)
                                    .setDisplayTitle(model.attributes?.title)
                                    .build()
                            )
                            .build()
                    )
                    uiState.playerController?.removeMediaItems(0, 100000)
                    uiState.playerController?.addMediaItems(mediaItems)
                    uiState.playerController?.prepare()
                    uiState.playerController?.play()

                    mainViewModel.updateCurrentPlaylistToUi(uiState.playerController)

                    navController.navigate("ProfileScreen/play")

                    //Событие статистики
                    val eventParameters: MutableMap<String, Any> = HashMap()
                    eventParameters["name"] = model.attributes?.title.toString()
                    AppMetrica.reportEvent("VideoClick", eventParameters)
                }
                .padding(5.dp),

            lineHeight = 22.sp,
            text = model.attributes?.title.toString()
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ListRow(
    model: FavoriteAuthors,
    navController: NavHostController,
    mainViewModel: MainViewModel
) {

    Column(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .width(0.13.dh)
    ) {
        var image = model.image
        if (image == null) {
            image = "https://predanie.ru/img/no-image/author_300.png"
        }
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(image)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .diskCachePolicy(CachePolicy.ENABLED)
                .build(),
            contentDescription = null,
            modifier = Modifier
                .combinedClickable(
                    onClick = {
                        navController.navigate("AuthorScreen/${model.uid}")
                    }
                )
                .fillMaxWidth()
                .size(0.13.dh)
                .clip(CircleShape)
                .border(2.dp, Color(0xFFFFD600), CircleShape),
            contentScale = ContentScale.Crop
        )
        Text(
            modifier = Modifier
                .clickable {
                    navController.navigate("AuthorScreen/${model.uid}")
                }
                .padding(5.dp),

            lineHeight = 22.sp,
            text = model.title
        )
    }
}

@Composable
fun ListRow(
    model: FavoriteCompositions,
    navController: NavHostController,
    mainViewModel: MainViewModel
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .width(130.dp)
            .height(300.dp)
    ) {
        var image = model.image
        if (image == "") {
            image = "https://predanie.ru/img/no-image/work_200.png"
        }
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(image)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .diskCachePolicy(CachePolicy.ENABLED)
                .build(),
            contentDescription = null,
            modifier = Modifier
                .clickable {
                    //Ставим композицию в историю и перезагружаем историю
                    mainViewModel.setCompositionToHistory(
                        model.uid.toString(),
                        context = context,
                        title = model.title,
                        image = model.image.toString()
                    )

                    navController.navigate("ItemScreen/${model.uid}")
                }
                .size(190.dp)
                .fillMaxWidth()
                .padding(5.dp)
                .clip(RoundedCornerShape(5.dp)),
            contentScale = ContentScale.Crop
        )
        Text(
            modifier = Modifier
                .clickable {
                    navController.navigate("ItemScreen/${model.uid}")
                }
                .padding(5.dp),

            lineHeight = 22.sp,
            text = model.title
        )
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ListRow(model: UserPlaylist, navController: NavHostController, mainViewModel: MainViewModel) {
    val uiState by mainViewModel.uiState.collectAsState()

    val context = LocalContext.current

    val showDeletePlaylistDialog = remember { mutableStateOf(false) }
    val showPlayPlaylistDialog = remember { mutableStateOf(false) }
    val deletePlaylist = remember { mutableStateOf("") }
    val playPlaylist = remember { mutableStateOf("") }
    val playPlaylistJson = remember { mutableStateOf(ArrayList<MediaItem>()) }

    if (showDeletePlaylistDialog.value) {
        PlaylistAlertDialog(showDeletePlaylistDialog, deletePlaylist, mainViewModel, context)
    }

    if (showPlayPlaylistDialog.value) {
        ShowPlaylistAlertDialog(
            showPlayPlaylistDialog, playPlaylist, mainViewModel, model = playPlaylistJson,
            context = context
        )
    }

    Column(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .width(120.dp)
            .height(120.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.playlist),
            contentDescription = "playlist",
            modifier = Modifier
                .size(55.dp)
                .combinedClickable(
                    onClick = {
                        showPlayPlaylistDialog.value = true
                        playPlaylist.value = model.playlistName
                        playPlaylistJson.value = model.playlistJson

                        /*uiState.playerController?.removeMediaItems(0, 100000)
                        uiState.playerController?.addMediaItems(model.playlistJson)
                        uiState.playerController?.prepare()
                        uiState.playerController?.play()*/

                        mainViewModel.updateCurrentPlaylistToUi(uiState.playerController)
                    },
                    onLongClick = {
                        deletePlaylist.value = model.playlistName
                        showDeletePlaylistDialog.value = true
                    }
                ),
            tint = Color(android.graphics.Color.parseColor("#FFD600")),
        )
        Text(
            modifier = Modifier
                .combinedClickable(
                    onClick = {
                        uiState.playerController?.removeMediaItems(0, 100000)
                        uiState.playerController?.addMediaItems(model.playlistJson)
                        uiState.playerController?.prepare()
                        uiState.playerController?.play()

                        mainViewModel.updateCurrentPlaylistToUi(uiState.playerController)
                    },
                    onLongClick = {
                        deletePlaylist.value = model.playlistName
                        showDeletePlaylistDialog.value = true
                    }
                )
                .padding(5.dp),

            lineHeight = 15.sp,
            fontSize = 12.sp,
            text = model.playlistName
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ListRow(model: FavoriteTracks, navController: NavHostController, mainViewModel: MainViewModel) {
    val uiState by mainViewModel.uiState.collectAsState()

    val context = LocalContext.current

    val showDeleteDialog = remember { mutableStateOf(false) }
    val deleteItem = remember { mutableStateOf("") }

    if (showDeleteDialog.value) {
        SimpleAlertDialog(showDeleteDialog, deleteItem, mainViewModel, context)
    }

    Column(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .width(120.dp)
            .height(120.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.audiofile),
            contentDescription = "file",
            modifier = Modifier
                .size(50.dp)
                .combinedClickable(
                    onClick = {
                        val mediaItems = arrayListOf<MediaItem>()

                        mediaItems.add(
                            MediaItem
                                .Builder()
                                .setUri(model.uri)
                                .setMediaId(model.uid.toString())
                                .setMediaMetadata(
                                    MediaMetadata
                                        .Builder()
                                        .setTitle(model.title)
                                        .setDisplayTitle(model.title)
                                        .build()
                                )
                                .build()
                        )
                        uiState.playerController?.removeMediaItems(0, 100000)
                        uiState.playerController?.addMediaItems(mediaItems)
                        uiState.playerController?.prepare()
                        uiState.playerController?.play()

                        mainViewModel.updateCurrentPlaylistToUi(uiState.playerController)
                    },
                    onLongClick = {
                        deleteItem.value = model.uri
                        showDeleteDialog.value = true
                    }
                ),
            tint = Color(android.graphics.Color.parseColor("#FFD600")),
        )
        Text(
            modifier = Modifier
                .combinedClickable(
                    onClick = {
                        //Добавить сюда проигрывание файла (отложенный трэк)

                        val mediaItems = arrayListOf<MediaItem>()

                        mediaItems.add(
                            MediaItem
                                .Builder()
                                .setUri(model.uri)
                                .setMediaId(model.uid.toString())
                                .setMediaMetadata(
                                    MediaMetadata
                                        .Builder()
                                        .setTitle(model.title)
                                        .setDisplayTitle(model.title)
                                        .build()
                                )
                                .build()
                        )
                        uiState.playerController?.removeMediaItems(0, 100000)
                        uiState.playerController?.addMediaItems(mediaItems)
                        uiState.playerController?.prepare()
                        uiState.playerController?.play()

                        mainViewModel.updateCurrentPlaylistToUi(uiState.playerController)
                    },
                    onLongClick = {
                        deleteItem.value = model.uri
                        showDeleteDialog.value = true
                    }
                )
                .padding(5.dp),

            lineHeight = 15.sp,
            fontSize = 12.sp,
            text = model.title
        )
    }
}

@Composable
fun ListRow(model: HistoryCompositions, navController: NavHostController) {
    Column(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .width(130.dp)
            .height(300.dp)
    ) {
        var image = model.image
        if (image == "") {
            image = "https://predanie.ru/img/no-image/work_200.png"
        }
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(image)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .diskCachePolicy(CachePolicy.ENABLED)
                .build(),
            contentDescription = null,
            modifier = Modifier
                .clickable {
                    navController.navigate("ItemScreen/${model.uid}")
                }
                .size(190.dp)
                .fillMaxWidth()
                .padding(5.dp)
                .clip(RoundedCornerShape(5.dp)),
            contentScale = ContentScale.Crop
        )
        Text(
            modifier = Modifier
                .clickable {
                    navController.navigate("ItemScreen/${model.uid}")
                }
                .padding(5.dp),

            lineHeight = 22.sp,
            text = model.title
        )
    }
}

@Composable
fun ListRow(
    model: DownloadedCompositions,
    navController: NavHostController,
    mainViewModel: MainViewModel
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .width(130.dp)
            .height(300.dp)
    ) {
        var image = model.image
        if (image == "") {
            image = "https://predanie.ru/img/no-image/work_200.png"
        }
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(image)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .diskCachePolicy(CachePolicy.ENABLED)
                .build(),
            contentDescription = null,
            modifier = Modifier
                .clickable {
                    //Ставим композицию в историю и перезагружаем историю
                    mainViewModel.setCompositionToHistory(
                        model.uid.toString(),
                        context = context,
                        title = model.title,
                        image = model.image.toString()
                    )

                    navController.navigate("OfflineItemScreen/${model.uid}")
                }
                .size(190.dp)
                .fillMaxWidth()
                .padding(5.dp)
                .clip(RoundedCornerShape(5.dp)),
            contentScale = ContentScale.Crop
        )
        Text(
            modifier = Modifier
                .clickable {
                    navController.navigate("OfflineItemScreen/${model.uid}")
                }
                .padding(5.dp),

            lineHeight = 22.sp,
            text = model.title
        )
    }
}

@Composable
fun ListRow(model: Compositions, navController: NavHostController, mainViewModel: MainViewModel) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .width(130.dp)
            .height(340.dp)
    ) {
        var image = model.img_s
        if (image == null) {
            image = "https://predanie.ru/img/no-image/work_200.png"
        }
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(image)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .diskCachePolicy(CachePolicy.ENABLED)
                .build(),
            contentDescription = null,
            modifier = Modifier
                .clickable {
                    //Ставим композицию в историю и перезагружаем историю
                    mainViewModel.setCompositionToHistory(
                        model.id.toString(),
                        context = context,
                        title = model.name.toString(),
                        image = model.img_s.toString()
                    )

                    navController.navigate("ItemScreen/${model.id}")
                }
                .size(190.dp)
                .fillMaxWidth()
                .padding(5.dp)
                .clip(RoundedCornerShape(5.dp)),
            contentScale = ContentScale.Crop
        )
        if (model.author_name.toString() != "Без автора") {
            Text(
                modifier = Modifier
                    .height(32.dp)
                    .padding(end = 5.dp)
                    .clickable {
                        navController.navigate("SearchScreen/${model.author_name}")
                    },
                fontSize = 10.sp,
                textAlign = TextAlign.End,
                lineHeight = 10.sp,
                text = model.author_name.toString()
            )
        } else {
            Text(
                modifier = Modifier
                    .height(30.dp)
                    .padding(end = 10.dp),
                fontSize = 10.sp,
                textAlign = TextAlign.End,
                lineHeight = 10.sp,
                text = ""
            )
        }
        Text(
            modifier = Modifier
                .clickable {
                    navController.navigate("ItemScreen/${model.id}")
                }
                .padding(5.dp),

            lineHeight = 21.sp,
            text = model.name.toString()
        )
    }
}

@Composable
fun ListAuthorsRow(model: Entities, navController: NavHostController) {
    Column(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .width(130.dp)
            .height(250.dp)
            .padding(top = 20.dp)
            .clickable {
                navController.navigate("AuthorScreen/${model.id}")
            }
    ) {
        var image = model.img
        if (image == null) {
            image = "https://predanie.ru/img/no-image/author_300.png"
        }
        Image(
            painter = rememberAsyncImagePainter(image),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(100.dp)
                .padding(5.dp)
                .clip(CircleShape)
                .border(2.dp, Color(0xFFFFD600), CircleShape)
        )
        Text(
            modifier = Modifier.padding(5.dp),
            text = model.name.toString()
        )
    }
}

@Composable
fun ListRow(model: Entities, navController: NavHostController, mainViewModel: MainViewModel) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .width(130.dp)
            .height(250.dp)
            .clickable {
                //Ставим композицию в историю и перезагружаем историю
                mainViewModel.setCompositionToHistory(
                    model.id.toString(),
                    context = context,
                    title = model.name.toString(),
                    image = model.img.toString()
                )

                navController.navigate("ItemScreen/${model.id}")
            }
    ) {
        var image = model.img_s
        if (image == null) {
            image = "https://predanie.ru/img/no-image/work_200.png"
        }
        Image(
            painter = rememberAsyncImagePainter(image),
            contentDescription = null,
            modifier = Modifier
                .size(190.dp)
                .fillMaxWidth()
                .padding(5.dp),
        )
        Text(
            modifier = Modifier.padding(5.dp),
            text = model.name.toString()
        )
    }
}

@Composable
fun NonlazyGrid(
    columns: Int,
    itemCount: Int,
    modifier: Modifier = Modifier,
    content: @Composable() (Int) -> Unit
) {
    Column(modifier = modifier) {
        var rows = (itemCount / columns)
        if (itemCount.mod(columns) > 0) {
            rows += 1
        }

        for (rowId in 0 until rows) {
            val firstIndex = rowId * columns

            Row {
                for (columnId in 0 until columns) {
                    val index = firstIndex + columnId
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        if (index < itemCount) {
                            content(index)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ListRow(
    model: AuthorCompositions,
    navController: NavHostController,
    mainViewModel: MainViewModel
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .width(130.dp)
            .height(300.dp)
    ) {
        var image = model.img_s
        if (image == null) {
            image = "https://predanie.ru/img/no-image/work_200.png"
        }
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(image)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .diskCachePolicy(CachePolicy.ENABLED)
                .build(),
            contentDescription = null,
            modifier = Modifier
                .clickable {
                    //Ставим композицию в историю и перезагружаем историю
                    mainViewModel.setCompositionToHistory(
                        model.id.toString(),
                        context = context,
                        title = model.name.toString(),
                        image = model.img_s.toString()
                    )

                    navController.navigate("ItemScreen/${model.id}")
                }
                .size(190.dp)
                .fillMaxWidth()
                .padding(5.dp)
                .clip(RoundedCornerShape(5.dp)),
            contentScale = ContentScale.Crop
        )
        Text(
            modifier = Modifier
                .clickable {
                    navController.navigate("ItemScreen/${model.id}")
                }
                .padding(5.dp),

            lineHeight = 22.sp,
            text = model.name.toString()
        )
    }
}

@Composable
fun CatalogListRow(model: Categories) {
    Column(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Row() {
            Text(
                modifier = Modifier.padding(top = 8.dp),
                text = "§",
                fontSize = 25.sp,
                color = Color(android.graphics.Color.parseColor("#FFD600"))
            )

            Text(
                modifier = Modifier.padding(5.dp),
                text = model.name.toString(),
                fontSize = 30.sp,
            )
        }
    }
}

@Composable
fun PlaylistAlertDialog(
    showDeleteDialog: MutableState<Boolean>,
    deleteItem: MutableState<String>,
    mainViewModel: MainViewModel,
    context: Context
) {
    AlertDialog(
        onDismissRequest = {
            showDeleteDialog.value = false
        },
        confirmButton = {
            TextButton(onClick = {
                showDeleteDialog.value = false
                mainViewModel.removePlaylist(deleteItem.value, context = context)
            })
            { Text(text = "Удалить") }
        },
        dismissButton = {
            TextButton(onClick = { showDeleteDialog.value = false })
            { Text(text = "Отменить") }
        },
        title = { Text(text = "Удалить плейлист?") },
        text = { Text(text = "") }
    )
}

@Composable
fun ShowPlaylistAlertDialog(
    showDialog: MutableState<Boolean>,
    deleteItem: MutableState<String>,
    mainViewModel: MainViewModel,
    context: Context,
    model: MutableState<ArrayList<MediaItem>>
) {
    val uiState by mainViewModel.uiState.collectAsState()

    AlertDialog(
        onDismissRequest = {
            showDialog.value = false
        },
        confirmButton = {
            TextButton(onClick = {
                //Воспроизвожу плейлист
                showDialog.value = false

                uiState.playerController?.removeMediaItems(0, 100000)
                uiState.playerController?.addMediaItems(model.value)
                uiState.playerController?.prepare()
                uiState.playerController?.play()

                mainViewModel.updateCurrentPlaylistToUi(uiState.playerController)
            })
            { Text(text = "Воспроизвести") }
        },
        dismissButton = {
            TextButton(onClick = { showDialog.value = false })
            { Text(text = "Отменить") }
        },
        title = { Text(text = "Воспроизвести плейлист?") },
        text = {
            Column(modifier = Modifier
                .verticalScroll(rememberScrollState())) {
                model.value.forEach {
                    Text(text = it.mediaMetadata.title.toString(), fontSize = 12.sp, maxLines = 1)
                }
            }
        }
    )
}

@Composable
fun SimpleAlertDialog(
    showDeleteDialog: MutableState<Boolean>,
    deleteItem: MutableState<String>,
    mainViewModel: MainViewModel,
    context: Context
) {
    AlertDialog(
        onDismissRequest = {
            showDeleteDialog.value = false
        },
        confirmButton = {
            TextButton(onClick = {
                showDeleteDialog.value = false
                mainViewModel.removeTrackFromFavorite(deleteItem.value, context = context)
            })
            { Text(text = "Удалить") }
        },
        dismissButton = {
            TextButton(onClick = { showDeleteDialog.value = false })
            { Text(text = "Отменить") }
        },
        title = { Text(text = "Удалить элемент?") },
        text = { Text(text = "") }
    )
}