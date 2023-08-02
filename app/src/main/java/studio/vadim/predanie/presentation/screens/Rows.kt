package studio.vadim.predanie.presentation.screens

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.slaviboy.composeunits.dh
import studio.vadim.predanie.data.room.DownloadedCompositions
import studio.vadim.predanie.data.room.FavoriteAuthors
import studio.vadim.predanie.data.room.FavoriteCompositions
import studio.vadim.predanie.data.room.FavoriteTracks
import studio.vadim.predanie.data.room.HistoryCompositions
import studio.vadim.predanie.domain.models.api.items.AuthorCompositions
import studio.vadim.predanie.domain.models.api.lists.Categories
import studio.vadim.predanie.domain.models.api.lists.Compositions
import studio.vadim.predanie.domain.models.api.lists.Entities
import studio.vadim.predanie.domain.models.api.lists.VideoData
import studio.vadim.predanie.presentation.MainViewModel

@Composable
fun ListRow(model: VideoData, navController: NavHostController, mainViewModel: MainViewModel) {
    val uiState by mainViewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .width(0.13.dh)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(model.attributes?.image)
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

                    navController.navigate("ProfileScreen/play")
                }
                .fillMaxWidth()
                .size(0.13.dh),
            contentScale = ContentScale.Crop
        )
        Text(
            modifier = Modifier
                .clickable {
                    navController.navigate("ProfileScreen/play")
                }
                .padding(5.dp),

            lineHeight = 22.sp,
            text = model.attributes?.title.toString()
        )
    }
}

@Composable
fun ListRow(model: FavoriteAuthors, navController: NavHostController) {
    Column(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .width(0.13.dh)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(model.image)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .diskCachePolicy(CachePolicy.ENABLED)
                .build(),
            contentDescription = null,
            modifier = Modifier
                .clickable {
                    navController.navigate("AuthorScreen/${model.uid}")
                }
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
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(model.image)
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

@Composable
fun ListRow(model: FavoriteTracks, navController: NavHostController) {
    Column(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .width(130.dp)
            .height(300.dp)
    ) {
        /*AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(model.image)
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
        )*/
        Text(
            modifier = Modifier
                .clickable {
                    navController.navigate("ItemScreen/${model.uid}")
                    //Добавить сюда проигрывание файла (отложенный трэк)
                }
                .padding(5.dp),

            lineHeight = 22.sp,
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
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(model.image)
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
fun ListRow(model: DownloadedCompositions, navController: NavHostController, mainViewModel: MainViewModel) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .width(130.dp)
            .height(300.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(model.image)
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
            .height(300.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(model.img_s)
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
        Image(
            painter = rememberAsyncImagePainter(model.img),
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
fun ListRow(model: AuthorCompositions, navController: NavHostController, mainViewModel: MainViewModel) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .width(130.dp)
            .height(300.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(model.img_s)
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