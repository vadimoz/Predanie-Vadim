package studio.vadim.predanie.presentation.screens

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.ui.PlayerView
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import studio.vadim.predanie.presentation.MainViewModel
import studio.vadim.predanie.presentation.navigation.NavigationItem

@Composable
fun ProfileScreen(mainViewModel: MainViewModel, navController: NavHostController, action: String?) {
    val uiState by mainViewModel.uiState.collectAsState()

    val newItems = uiState.newList.collectAsLazyPagingItems()
    val audioPopularList = uiState.audioPopularList.collectAsLazyPagingItems()
    val musicPopularList = uiState.musicPopularList.collectAsLazyPagingItems()
    val favoritesList = uiState.favoritesList.collectAsLazyPagingItems()

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

        LaunchedEffect(Unit){
            if(action == "play"){
                uiState.playerController?.play()
            }
        }

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
        ) {

            Column(
                Modifier.fillMaxSize()
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
                        text = "Ваши настройки",
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
                        text = "Ваши плейлисты",
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

            //Рекомендуем
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
                        text = "Ваша история",
                        fontSize = 35.sp,
                        color = Color(android.graphics.Color.parseColor("#2F2F2F"))
                    )

                }
            }
            LazyRow() {
                items(favoritesList.itemCount) { index ->
                    favoritesList[index]?.let { ListRow(model = it, navController) }
                }
            }
            //Новинки
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
                        text = "Ваши отложенные",
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


            @Composable
            fun BottomNavigationBarPreview() {
                BottomNavigationBar()
            }
        }
    }
}