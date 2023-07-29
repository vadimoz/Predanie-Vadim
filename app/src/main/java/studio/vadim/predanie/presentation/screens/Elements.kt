package studio.vadim.predanie.presentation.screens

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import studio.vadim.predanie.data.room.DownloadedCompositions
import studio.vadim.predanie.domain.models.api.items.AuthorCompositions
import studio.vadim.predanie.domain.models.api.lists.Categories
import studio.vadim.predanie.domain.models.api.lists.Compositions
import studio.vadim.predanie.domain.models.api.lists.Entities
@Composable
fun ListRow(model: DownloadedCompositions, navController: NavHostController) {
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
fun ListRow(model: Compositions, navController: NavHostController) {
    Column(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .width(130.dp)
            .height(300.dp)
    ) {
        if (model.author_name.toString() != "Без автора") {
            Text(
                modifier = Modifier
                    .height(32.dp)
                    .padding(end = 10.dp)
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
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(model.img_s)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .diskCachePolicy(CachePolicy.ENABLED)
                .build(),
            contentDescription = null,
            modifier = Modifier
                .clickable {
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
fun ListRow(model: Entities, navController: NavHostController) {
    Column(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .width(130.dp)
            .height(250.dp)
            .clickable {
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
fun ListRow(model: AuthorCompositions, navController: NavHostController) {
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