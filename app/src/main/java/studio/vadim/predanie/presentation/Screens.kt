package studio.vadim.predanie.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import studio.vadim.predanie.R
import studio.vadim.predanie.domain.models.api.lists.Compositions
@Composable
fun HomeScreen(mainViewModel: MainViewModel, onClick: () -> Unit) {
    val uiState by mainViewModel.uiState.collectAsState()

    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        /*Text(
            text = "Hello $name!"
        )

        Button(onClick = {
            onClick()
        }) {
            Text(text = "Next Screen")
        }*/

        //Новинки
        Column(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
        ) {
            Text(
                modifier = Modifier.padding(5.dp),
                text = "Новинки медиатеки")
        }

        LazyRow() {
            items(uiState.newList.compositions.count()) { index ->
                ListRow(model = uiState.newList.compositions[index])
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
                text = "Популярные материалы")
        }
        LazyRow() {
            items(uiState.newList.compositions.count()) { index ->
                ListRow(model = uiState.audioPopularList.compositions[index])
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
                text = "Популярная музыка")
        }
        LazyRow() {
            items(uiState.newList.compositions.count()) { index ->
                ListRow(model = uiState.musicPopularList.compositions[index])
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
                text = "Рекомендуем")
        }
        LazyRow() {
            items(uiState.newList.compositions.count()) { index ->
                ListRow(model = uiState.favoritesList.compositions[index])
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
                        icon = { Icon(painterResource(id = item.icon), contentDescription = item.title) },
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
            modifier = Modifier.size(190.dp).fillMaxWidth().padding(5.dp),
            contentScale = ContentScale.Crop,

            )
        Text(
            modifier = Modifier.padding(5.dp),
            text = model.name.toString())
    }
}

@Composable
fun MusicScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.white))
            .wrapContentSize(Alignment.Center)
    ) {
        Text(
            text = "Music View",
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center,
            fontSize = 25.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MusicScreenPreview() {
    MusicScreen()
}

@Composable
fun MoviesScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.white))
            .wrapContentSize(Alignment.Center)
    ) {
        Text(
            text = "Movies View",
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center,
            fontSize = 25.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MoviesScreenPreview() {
    MoviesScreen()
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

@Preview(showBackground = true)
@Composable
fun BooksScreenPreview() {
    BooksScreen()
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

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen()
}