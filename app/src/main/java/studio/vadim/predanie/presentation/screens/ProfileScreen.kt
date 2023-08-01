package studio.vadim.predanie.presentation.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.ui.PlayerView
import androidx.navigation.NavHostController
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.compose.collectAsLazyPagingItems
import studio.vadim.predanie.data.room.AppDatabase
import studio.vadim.predanie.presentation.MainViewModel
import studio.vadim.predanie.presentation.downloadService.DownloadManagerSingleton
import studio.vadim.predanie.presentation.playerService.playlistAccordion.PlaylistAccordionGroup
import studio.vadim.predanie.presentation.playerService.playlistAccordion.PlaylistAccordionModel
import studio.vadim.predanie.presentation.navigation.NavigationItem

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@SuppressLint("ServiceCast", "UnrememberedMutableState")
@Composable
fun  ProfileScreen(mainViewModel: MainViewModel, navController: NavHostController, action: String?) {
    val context = LocalContext.current
    val uiState by mainViewModel.uiState.collectAsState()

    val newItems = uiState.newList.collectAsLazyPagingItems()
    val historyList = uiState.historyList?.collectAsLazyPagingItems()
    val downloadsList = uiState.downloadsList?.collectAsLazyPagingItems()
    val special = uiState.special?.collectAsLazyPagingItems()

    val currentPlaylistFromDB = mutableStateOf(AppDatabase.getInstance(context).mainPlaylistDao().findByName("Main"))

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        LaunchedEffect(Unit) {
            currentPlaylistFromDB.value = AppDatabase.getInstance(context).mainPlaylistDao().findByName("Main")

            if (action == "play") {
                uiState.playerController?.play()
            }
        }

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
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

            if (currentPlaylistFromDB.value.playlistJson[0].mediaMetadata.title != "null") {
                Column(
                    Modifier
                        .fillMaxSize()
                        .height(300.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
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

                        for (item in currentPlaylistFromDB.value.playlistJson) {
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
                            playerList = currentPlaylistFromDB.value.playlistJson,
                            navController = navController,
                            mainViewModel = mainViewModel,
                            globalItemCount = currentPlaylistFromDB.value.playlistJson.count(),
                            partCount = currentPlaylistFromDB.value.playlistJson.count()
                        )
                    }
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
                //Отложенные
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
                            text = "Мои отложенные",
                            fontSize = 35.sp,
                            color = Color(android.graphics.Color.parseColor("#2F2F2F"))
                        )

                    }
                }

                LazyRow() {
                    items(newItems.itemCount) { index ->
                        newItems[index]?.let { ListRow(model = it, navController, mainViewModel) }
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
                            ListRow(model = it, navController, mainViewModel)
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