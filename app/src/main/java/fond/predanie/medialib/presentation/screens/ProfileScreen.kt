package fund.predanie.medialib.presentation.screens

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.media3.common.MediaItem
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import fund.predanie.medialib.R
import fund.predanie.medialib.presentation.MainViewModel
import fond.predanie.medialib.presentation.playerService.playlistAccordion.PlaylistAccordionGroup
import fond.predanie.medialib.presentation.playerService.playlistAccordion.PlaylistAccordionModel

@OptIn(ExperimentalFoundationApi::class)
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@SuppressLint("ServiceCast", "UnrememberedMutableState")
@Composable
fun ProfileScreen(mainViewModel: MainViewModel, navController: NavHostController, action: String?) {
    val context = LocalContext.current
    val uiState by mainViewModel.uiState.collectAsState()

    val historyList = uiState.historyList?.collectAsLazyPagingItems()
    val downloadsList = uiState.downloadsList?.collectAsLazyPagingItems()
    val favAuthorsList = uiState.favAuthorsList?.collectAsLazyPagingItems()
    val favCompositionsList = uiState.favCompositionsList?.collectAsLazyPagingItems()
    val favTracksList = uiState.favTracksList?.collectAsLazyPagingItems()
    val playlistsList = uiState.playlistsList?.collectAsLazyPagingItems()

    val currentPlaylistFromDB = uiState.mainPlaylist

    val showDeleteDialog = remember { mutableStateOf(false) }

    if (showDeleteDialog.value) {
        RemoveDownloadsDialog(showDeleteDialog, mainViewModel, context)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        LaunchedEffect(Unit) {
            mainViewModel.updateCurrentPlaylistToUi(uiState.playerController)

            mainViewModel.loadHistoryCompositions(context)

            if (action == "play") {
                uiState.playerController?.play()
            }
        }

        Column(
            modifier = Modifier
            //.verticalScroll(rememberScrollState())
        ) {

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

            /*Column(
                Modifier
                    .fillMaxSize()
                    .height(300.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                //verticalArrangement = Arrangement.Center
            ) {

                AndroidView(
                    factory = { context ->
                        PlayerView(context).apply {
                            player = uiState.playerController
                            controllerHideOnTouch = true
                            setShowPreviousButton(false)
                            setShowNextButton(false)
                            setShowRewindButton(false)
                            setShowVrButton(false)
                            setShowFastForwardButton(false)
                            controllerAutoShow = false
                            controllerShowTimeoutMs = 0
                            showController()
                        }
                    },
                    update = {
                        it.player = uiState.playerController
                    }
                )
            }*/

            //Очередь воспроизведения
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
            ) {

                /*Icon(
                    painter = painterResource(R.drawable.fullscreen),
                    contentDescription = "Fullscreen",
                    modifier = Modifier
                        .size(24.dp)
                        .padding(top = 10.dp)
                        .clickable {
                            navController.navigate("PlayerScreen")
                        }
                        .fillMaxWidth(),
                    tint = Color(android.graphics.Color.parseColor("#000000")),
                )*/

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

            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                if (mainViewModel.isInternetConnected(context)) {
                    Column(
                        modifier = Modifier
                            .wrapContentHeight()
                            .fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(
                                top = 20.dp,
                                start = 20.dp,
                                bottom = 20.dp
                            )
                        ) {
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
                    //Отложенные
                    Column(
                        modifier = Modifier
                            .wrapContentHeight()
                            .fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(
                                top = 20.dp,
                                start = 20.dp,
                                bottom = 20.dp
                            )
                        ) {
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
                                favAuthorsList[index]?.let {
                                    ListRow(
                                        model = it,
                                        navController,
                                        mainViewModel
                                    )
                                }
                            }
                        }
                    }

                    LazyRow() {
                        if (favCompositionsList != null) {
                            items(favCompositionsList.itemCount) { index ->
                                favCompositionsList[index]?.let {
                                    ListRow(
                                        model = it,
                                        navController,
                                        mainViewModel
                                    )
                                }
                            }
                        }
                    }

                    LazyRow() {
                        if (favTracksList != null) {
                            items(favTracksList.itemCount) { index ->
                                favTracksList[index]?.let {
                                    ListRow(
                                        model = it,
                                        navController,
                                        mainViewModel
                                    )
                                }
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
                        if (downloadsList != null) {
                            if (downloadsList.itemCount != 0) {
                                Icon(
                                    painter = painterResource(R.drawable.trash),
                                    contentDescription = "file",
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clickable {
                                            //Удаляем все загрузки
                                            showDeleteDialog.value = true
                                        },
                                    tint = Color(android.graphics.Color.parseColor("#FFD600")),
                                )
                            }
                        }
                    }
                }

                LazyRow() {
                    if (downloadsList != null) {
                        items(downloadsList.itemCount) { index ->
                            downloadsList[index]?.let {
                                ListRow(model = it, navController, mainViewModel)
                            }
                        }
                    }
                }

                if (downloadsList != null) {
                    if (downloadsList.itemCount == 0) {
                        Text(
                            "Здесь появятся Ваши загрузки",
                            fontSize = 12.sp,
                            modifier = Modifier.padding(25.dp)
                        )
                    }
                }

                if (mainViewModel.isInternetConnected(context)) {
                    //Плейлисты
                    Column(
                        modifier = Modifier
                            .wrapContentHeight()
                            .fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(
                                top = 20.dp,
                                start = 20.dp,
                                bottom = 20.dp
                            )
                        ) {
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

                        LazyRow() {
                            if (playlistsList != null) {
                                items(playlistsList.itemCount) { index ->
                                    playlistsList[index]?.let {
                                        ListRow(
                                            model = it,
                                            navController,
                                            mainViewModel
                                        )
                                    }
                                }
                            }
                        }

                        var dialogOpen by remember {
                            mutableStateOf(false)
                        }

                        if (dialogOpen) {
                            Dialog(onDismissRequest = {
                                dialogOpen = false
                            }) {
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentHeight(),
                                    shape = RoundedCornerShape(size = 10.dp)
                                ) {

                                    Column(
                                        modifier = Modifier
                                            .padding(all = 16.dp)
                                            .verticalScroll(rememberScrollState()),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                    ) {
                                        Text(text = "Добавить Очередь воспроизведения в новый плейлист:")

                                        var playlistName by rememberSaveable { mutableStateOf("") }
                                        TextField(
                                            value = playlistName,
                                            onValueChange = {
                                                playlistName = it
                                            },
                                            label = { Text("Название нового плейлиста") }
                                        )

                                        Button(
                                            modifier = Modifier.padding(10.dp), onClick = {
                                                mainViewModel.setCurrentPlaylistToDb(
                                                    uiState.playerController,
                                                    context,
                                                    playlistName
                                                )
                                                mainViewModel.loadPlaylists(context)
                                                dialogOpen = false
                                            }, colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(
                                                    android.graphics.Color.parseColor(
                                                        "#ffcd00"
                                                    )
                                                ),
                                                contentColor = Color.White
                                            )
                                        ) {
                                            Text("Создать", fontSize = 13.sp)
                                        }

                                        val currentPlaylists =
                                            mainViewModel.getAllPlaylists(context)

                                        if (currentPlaylists.isNotEmpty()) {
                                            Text(
                                                modifier = Modifier.padding(10.dp),
                                                text = "Добавить в существующий плейлист (все треки будут заменены):"
                                            )
                                            Column() {
                                                currentPlaylists.forEach() {
                                                    Text(
                                                        modifier = Modifier
                                                            .padding(20.dp)
                                                            .clickable {
                                                                mainViewModel.setCurrentPlaylistToDb(
                                                                    uiState.playerController,
                                                                    context,
                                                                    it.playlistName
                                                                )
                                                                mainViewModel.loadPlaylists(context)
                                                                dialogOpen = false
                                                            }, text = it.playlistName
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = {
                                    dialogOpen = true
                                }, colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(android.graphics.Color.parseColor("#ffcd00")),
                                    contentColor = Color.White
                                )
                            ) {
                                Text(
                                    "Сохранить очередь воспроизведения в плейлист",
                                    fontSize = 13.sp
                                )
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

                    Row(modifier = Modifier.padding(top = 20.dp, start = 20.dp, bottom = 20.dp)) {
                        Checkbox(
                            checked = uiState.goToNext,
                            onCheckedChange = {
                                mainViewModel.setGoToNextSettings(it, context)
                            }
                        )
                        Text(
                            modifier = Modifier.padding(start = 5.dp),
                            text = "Пропускать в плеере треки, которые я уже слушал",
                            fontSize = 20.sp,
                            color = Color(android.graphics.Color.parseColor("#2F2F2F"))
                        )
                    }

                    Row(modifier = Modifier.padding(top = 20.dp, start = 20.dp, bottom = 20.dp)) {

                        var expanded by remember { mutableStateOf(false) }
                        Box {
                            IconButton(onClick = { expanded = true }) {
                                Icon(Icons.Default.MoreVert, contentDescription = "Показать меню")
                            }
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                DropdownMenuItem(onClick = {
                                    mainViewModel.setPercentToFileReady(90, context)
                                    expanded = false
                                }, text = {
                                    Text("90%")
                                })
                                DropdownMenuItem(onClick = {
                                    mainViewModel.setPercentToFileReady(95, context)
                                    expanded = false
                                }, text = {
                                    Text("95%")
                                })
                                DropdownMenuItem(onClick = {
                                    mainViewModel.setPercentToFileReady(100, context)
                                    expanded = false
                                }, text = {
                                    Text("100%")
                                })
                            }
                        }
                        Text(
                            modifier = Modifier.padding(start = 5.dp),
                            text = "Помечать трек прослушанным по достижении ${uiState.percentToFileReady}%",
                            fontSize = 20.sp,
                            color = Color(android.graphics.Color.parseColor("#2F2F2F"))
                        )
                    }
                }
            }


        }
    }
}

@Composable
fun RemoveDownloadsDialog(
    showDeleteDialog: MutableState<Boolean>,
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

                //Удаляем все загрузки - базу и кэш
                mainViewModel.removeAllDownloads(context)
            })
            { Text(text = "Удалить") }
        },
        dismissButton = {
            TextButton(onClick = { showDeleteDialog.value = false })
            { Text(text = "Отменить") }
        },
        title = { Text(text = "Удалить все загрузки?") },
        text = { Text(text = "") }
    )
}