package fund.predanie.medialib.presentation.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import fund.predanie.medialib.R
import fund.predanie.medialib.presentation.MainViewModel
import fund.predanie.medialib.presentation.navigation.NavigationItem


@Composable
fun HomeScreen(mainViewModel: MainViewModel, navController: NavHostController) {
    val uiState by mainViewModel.uiState.collectAsState()

    val newItems = uiState.newList.collectAsLazyPagingItems()
    val audioPopularList = uiState.audioPopularList.collectAsLazyPagingItems()
    val musicPopularList = uiState.musicPopularList.collectAsLazyPagingItems()
    val favoritesList = uiState.favoritesList.collectAsLazyPagingItems()
    val specialList = uiState.special?.collectAsLazyPagingItems()
    val blogList = uiState.blogList.collectAsLazyPagingItems()


    LazyColumn() {
        item {
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

                Column(
                    modifier = Modifier
                ) {

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
                                text = "Новинки медиатеки",
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
                                text = "Рекомендуем",
                                fontSize = 35.sp,
                                color = Color(android.graphics.Color.parseColor("#2F2F2F"))
                            )

                        }
                    }
                    LazyRow() {
                        items(favoritesList.itemCount) { index ->
                            favoritesList[index]?.let { ListRow(model = it, navController, mainViewModel) }
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
                                text = "Популярные аудио",
                                fontSize = 35.sp,
                                color = Color(android.graphics.Color.parseColor("#2F2F2F"))
                            )

                        }
                    }
                    LazyRow() {
                        items(audioPopularList.itemCount) { index ->
                            audioPopularList[index]?.let { ListRow(model = it, navController, mainViewModel) }
                        }
                    }

                    //Популярная музыка
                    Column(
                        modifier = Modifier
                            .wrapContentHeight()
                            .fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(top = 20.dp, start = 20.dp, bottom = 20.dp)
                        ) {
                            Text(
                                modifier = Modifier.padding(top = 8.dp),
                                text = "§",
                                fontSize = 25.sp,
                                color = Color(android.graphics.Color.parseColor("#FFD600"))
                            )
                            Text(
                                modifier = Modifier.padding(start = 5.dp),
                                text = "Популярная музыка",
                                fontSize = 35.sp,
                                color = Color(android.graphics.Color.parseColor("#2F2F2F"))
                            )

                        }
                    }
                    LazyRow() {
                        items(musicPopularList.itemCount) { index ->
                            musicPopularList[index]?.let { ListRow(model = it, navController, mainViewModel) }
                        }
                    }

                    //Спецпроект - видео
                    if (specialList != null) {
                        if(specialList.itemCount > 0) {
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
                                        text = "Спецпроект - видео",
                                        fontSize = 35.sp,
                                        color = Color(android.graphics.Color.parseColor("#2F2F2F"))
                                    )
                                }
                            }
                            Text(
                                modifier = Modifier.padding(start = 5.dp),
                                text = "",
                                fontSize = 15.sp,
                                color = Color(android.graphics.Color.parseColor("#000000"))
                            )
                            LazyRow() {
                                items(specialList.itemCount) { index ->
                                    specialList[index]?.let {
                                        ListRow(
                                            model = it,
                                            navController,
                                            mainViewModel = mainViewModel
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp)
                        .align(alignment = Alignment.CenterHorizontally)){
                        val context = LocalContext.current
                        val intent = remember { Intent(Intent.ACTION_VIEW, Uri.parse("https://vadim.studio/")) }

                        Image(
                            painterResource(R.drawable.vadim), "Logo",
                            modifier = Modifier
                                .width(180.dp)
                                .align(alignment = Alignment.CenterHorizontally)
                                .clickable {
                                    context.startActivity(intent)
                                }
                        )
                    }

                    //Блог
                    Column(
                        modifier = Modifier
                            .wrapContentHeight()
                            .fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(top = 20.dp, start = 20.dp, bottom = 20.dp)
                        ) {
                            Text(
                                modifier = Modifier.padding(top = 8.dp),
                                text = "§",
                                fontSize = 25.sp,
                                color = Color(android.graphics.Color.parseColor("#FFD600"))
                            )
                            Text(
                                modifier = Modifier.padding(start = 5.dp),
                                text = "Живое Предание",
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
                            contentColor = Color.White
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

        items(blogList.itemCount) { index ->
            blogList[index]?.let { ListRow(model = it, navController, mainViewModel = mainViewModel) }
        }
    }
}