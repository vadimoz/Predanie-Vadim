package studio.vadim.predanie.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.rememberAsyncImagePainter
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import studio.vadim.predanie.R
import studio.vadim.predanie.domain.models.api.lists.Categories
import studio.vadim.predanie.domain.models.api.lists.Compositions
import studio.vadim.predanie.domain.models.api.lists.Entities

@Composable
fun SplashScreen(mainViewModel: MainViewModel, navController: NavHostController) {
    val uiState by mainViewModel.uiState.collectAsState()

    val newItems = uiState.newList.collectAsLazyPagingItems()
    val audioPopularList = uiState.audioPopularList.collectAsLazyPagingItems()
    val musicPopularList = uiState.musicPopularList.collectAsLazyPagingItems()
    val favoritesList = uiState.favoritesList.collectAsLazyPagingItems()

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.rain))
        val logoAnimationState =
            animateLottieCompositionAsState(composition = composition)
        LottieAnimation(
            composition = composition,
            progress = { logoAnimationState.progress },
            modifier = Modifier
                .align(Alignment.Center),
            contentScale = ContentScale.Crop,
        )

        Image(painterResource(R.drawable.logo),"Logo",
            modifier = Modifier
             .width(150.dp)
            .align(Alignment.BottomCenter)
        )

        if (logoAnimationState.isAtEnd && logoAnimationState.isPlaying) {
            navController.navigate(NavigationItem.Home.route)
        }
    }
}
@Composable
fun HomeScreen(mainViewModel: MainViewModel, onClick: () -> Unit) {
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
                newItems[index]?.let { ListRow(model = it) }
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
                audioPopularList[index]?.let { ListRow(model = it) }
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
                musicPopularList[index]?.let { ListRow(model = it) }
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
                favoritesList[index]?.let { ListRow(model = it) }
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

@Composable
fun ListRow(model: Compositions) {

    Column(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .width(130.dp)
            .height(250.dp)
    ) {
        Image(
            painter = rememberAsyncImagePainter(model.img_s),
            contentDescription = null,
            modifier = Modifier
                .size(190.dp)
                .fillMaxWidth()
                .padding(5.dp),
            contentScale = ContentScale.Crop,

            )
        Text(
            modifier = Modifier.padding(5.dp),
            text = model.name.toString()
        )
    }
}


@Composable
fun ListRow(model: Entities) {
    Column(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .width(130.dp)
            .height(250.dp)
    ) {
        Image(
            painter = rememberAsyncImagePainter(model.img_s),
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
fun CatalogListRow(model: Categories) {
    Column(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
    ) {
        Text(
            modifier = Modifier.padding(5.dp),
            text = model.name.toString()
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CatalogScreen(mainViewModel: MainViewModel) {
    val uiState by mainViewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.white))
            .wrapContentSize(Alignment.Center)
    ) {
        LazyColumn() {
            items(uiState.catalogList.categories.count()) { index ->
                if (uiState.catalogList.categories[index].id_parent == 1) { // id_parent == 1 это базовые разделы
                    CatalogListRow(model = uiState.catalogList.categories[index])

                    FlowRow(modifier = Modifier
                        .wrapContentSize(Alignment.Center)) {
                        uiState.catalogList.categories[index].categories.forEach(){
                            Text (it.name.toString())
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchScreen(mainViewModel: MainViewModel) {
    val uiState by mainViewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.white))
            .wrapContentSize(Alignment.Center)
    ) {
        TextField(
            uiState.searchString,
            {

                mainViewModel.searchQueryUpdate(it)
            },
            textStyle = TextStyle(fontSize =  28.sp),
            placeholder = { "Найти..." }
        )
        LazyVerticalGrid(columns = GridCells.Adaptive(128.dp)) {
            items(uiState.searchList.entities.count()) { index ->
                    ListRow(model = uiState.searchList.entities[index])
            }
        }
    }
}

@Composable
fun BooksScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.white))
            .wrapContentSize(Alignment.Center)
    ) {
        Text(
            text = "Books View",
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center,
            fontSize = 25.sp
        )
    }
}

@Composable
fun ProfileScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.white))
            .wrapContentSize(Alignment.Center)
    ) {
        Text(
            text = "Profile View",
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center,
            fontSize = 25.sp
        )
    }
}