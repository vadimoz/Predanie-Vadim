package studio.vadim.predanie.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import studio.vadim.predanie.presentation.MainViewModel
import studio.vadim.predanie.presentation.navigation.NavigationItem


@Composable
fun HomeScreen(mainViewModel: MainViewModel, navController: NavHostController) {
    val uiState by mainViewModel.uiState.collectAsState()

    val newItems = uiState.newList.collectAsLazyPagingItems()
    val audioPopularList = uiState.audioPopularList.collectAsLazyPagingItems()
    val musicPopularList = uiState.musicPopularList.collectAsLazyPagingItems()
    val favoritesList = uiState.favoritesList.collectAsLazyPagingItems()

    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {

        //Новинки
        Column(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
        ) {
            Text(
                modifier = Modifier.padding(5.dp),
                text = "Новинки медиатеки"
            )
        }

        LazyRow() {
            items(newItems.itemCount) { index ->
                newItems[index]?.let { ListRow(model = it, navController) }
            }
        }

        //Популярное аудио
        Column(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
        ) {
            Text(
                modifier = Modifier.padding(5.dp),
                text = "Популярные материалы"
            )
        }
        LazyRow() {
            items(audioPopularList.itemCount) { index ->
                audioPopularList[index]?.let { ListRow(model = it, navController) }
            }
        }

        //Популярная музыка
        Column(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
        ) {
            Text(
                modifier = Modifier.padding(5.dp),
                text = "Популярная музыка"
            )
        }
        LazyRow() {
            items(musicPopularList.itemCount) { index ->
                musicPopularList[index]?.let { ListRow(model = it, navController) }
            }
        }

        //Рекомендуем
        Column(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
        ) {
            Text(
                modifier = Modifier.padding(5.dp),
                text = "Рекомендуем"
            )
        }
        LazyRow() {
            items(favoritesList.itemCount) { index ->
                favoritesList[index]?.let { ListRow(model = it, navController) }
            }
        }

        @Composable
        fun BottomNavigationBar() {
            val items = listOf(
                NavigationItem.Home,
                NavigationItem.Music,
                NavigationItem.Movies,
                NavigationItem.Books,
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