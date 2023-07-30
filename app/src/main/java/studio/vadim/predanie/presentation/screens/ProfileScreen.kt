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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import studio.vadim.predanie.presentation.DownloadsPagingSource
import studio.vadim.predanie.presentation.MainViewModel
import studio.vadim.predanie.presentation.downloadService.DownloadManagerSingleton
import studio.vadim.predanie.presentation.playerService.playlistAccordion.PlaylistAccordionGroup
import studio.vadim.predanie.presentation.playerService.playlistAccordion.PlaylistAccordionModel
import studio.vadim.predanie.presentation.navigation.NavigationItem


@SuppressLint("ServiceCast")
@Composable
fun ProfileScreen(mainViewModel: MainViewModel, navController: NavHostController, action: String?) {
    val context = LocalContext.current
    val uiState by mainViewModel.uiState.collectAsState()

    val newItems = uiState.newList.collectAsLazyPagingItems()
    val favoritesList = uiState.favoritesList.collectAsLazyPagingItems()
    val historyList = uiState.historyList?.collectAsLazyPagingItems()
    val downloadsList = uiState.downloadsList?.collectAsLazyPagingItems()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

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

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
        ) {

            //
            val currentPlaylistFromDB =
                AppDatabase.getInstance(LocalContext.current).mainPlaylistDao().findByName("Main")

            if (currentPlaylistFromDB.playlistJson[0].mediaMetadata.title != "null") {
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
                    newItems[index]?.let { ListRow(model = it, navController) }
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

            @Composable
            fun BottomNavigationBar() {
                val items = listOf(
                    NavigationItem.Home,
                    NavigationItem.Catalog,
                    NavigationItem.Search,
                    NavigationItem.CatalogItems,
                    NavigationItem.Profile
                )
                NavigationBar(
                    contentColor = androidx.compose.ui.graphics.Color.White
                ) {
                    items.forEach { item ->
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    painterResource(id = item.icon),
                                    contentDescription = item.title
                                )
                            },
                            label = { Text(text = item.title) },
                            alwaysShowLabel = false,
                            selected = false,
                            onClick = {
                                /* Add code later */
                            }
                        )
                    }
                }
            }

            @Composable
            fun BottomNavigationBarPreview() {
                BottomNavigationBar()
            }
        }
    }
}