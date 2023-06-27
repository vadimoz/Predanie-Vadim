package studio.vadim.predanie.presentation.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import studio.vadim.predanie.domain.models.api.items.AuthorCompositions
import studio.vadim.predanie.domain.models.api.lists.Categories
import studio.vadim.predanie.domain.models.api.lists.Compositions
import studio.vadim.predanie.domain.models.api.lists.Entities

@Composable
fun ListRow(model: Compositions, navController: NavHostController) {
    Column(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .width(130.dp)
            .height(250.dp)
    ) {
        AsyncImage(
            model = model.img_s,
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
            text = model.name.toString()
        )

        Text(
            modifier = Modifier
                .clickable {
                    navController.navigate("AuthorScreen/67398")
                }
                .padding(5.dp),
            text = model.author_name.toString()
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
            .height(250.dp)
    ) {
        Image(
            painter = rememberAsyncImagePainter(model.img_s),
            contentDescription = null,
            modifier = Modifier
                .size(190.dp)
                .fillMaxWidth()
                .padding(5.dp)
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