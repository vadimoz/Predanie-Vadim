package fund.predanie.medialib.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import fund.predanie.medialib.R
import fund.predanie.medialib.presentation.MainViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CatalogScreen(mainViewModel: MainViewModel, navController: NavHostController) {
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

                    FlowRow(
                        modifier = Modifier
                            .wrapContentSize(Alignment.Center)
                    ) {

                        uiState.catalogList.categories[index].categories.forEach() {
                            Row(modifier = Modifier.padding(12.dp)) {
                                var layout by remember { mutableStateOf<TextLayoutResult?>(null) }

                                Text(
                                    it.name.toString(),
                                    onTextLayout = {
                                        layout = it
                                    },
                                    modifier = Modifier
                                        .clickable {
                                            navController.navigate("CatalogItemsScreen/${it.id_category}/${it.name}")
                                        }
                                        .drawBehind {

                                            layout?.let {
                                                val thickness = 5f
                                                val dashPath = 0f
                                                val spacingExtra = 1f
                                                val offsetY = 5f

                                                for (i in 0 until it.lineCount) {
                                                    drawPath(
                                                        path = Path().apply {
                                                            moveTo(
                                                                it.getLineLeft(i),
                                                                it.getLineBottom(i) - spacingExtra + offsetY
                                                            )
                                                            lineTo(
                                                                it.getLineRight(i),
                                                                it.getLineBottom(i) - spacingExtra + offsetY
                                                            )
                                                        },
                                                        Color(android.graphics.Color.parseColor("#FFD600")),
                                                        style = Stroke(
                                                            width = thickness,
                                                            pathEffect = PathEffect.dashPathEffect(
                                                                floatArrayOf(dashPath, dashPath),
                                                                0f
                                                            )
                                                        )
                                                    )
                                                }
                                            }
                                        },
                                    fontSize = 20.sp
                                )
                            }


                            //В апи ошибка - ищем еще раз туже категорию и только тогда найдем ее подчиненных. Категории задвоены
                            val subCats =
                                uiState.catalogList.categories.filter { s -> s.id_category == it.id_category }

                            subCats.forEach() { sub ->
                                sub.categories.forEach(){
                                    Row(modifier = Modifier.padding(top = 0.dp, start = 20.dp).fillMaxWidth()) {
                                        var layout by remember { mutableStateOf<TextLayoutResult?>(null) }

                                        Text(
                                            it.name.toString(),
                                            onTextLayout = {
                                                layout = it
                                            },
                                            modifier = Modifier
                                                .clickable {
                                                    navController.navigate("CatalogItemsScreen/${it.id_category}/${it.name}")
                                                }
                                                .padding(bottom = 15.dp)
                                                .drawBehind {

                                                    layout?.let {
                                                        val thickness = 2f
                                                        val dashPath = 0f
                                                        val spacingExtra = 1f
                                                        val offsetY = 5f

                                                        for (i in 0 until it.lineCount) {
                                                            drawPath(
                                                                path = Path().apply {
                                                                    moveTo(
                                                                        it.getLineLeft(i),
                                                                        it.getLineBottom(i) - spacingExtra + offsetY
                                                                    )
                                                                    lineTo(
                                                                        it.getLineRight(i),
                                                                        it.getLineBottom(i) - spacingExtra + offsetY
                                                                    )
                                                                },
                                                                Color(android.graphics.Color.parseColor("#FFD600")),
                                                                style = Stroke(
                                                                    width = thickness,
                                                                    pathEffect = PathEffect.dashPathEffect(
                                                                        floatArrayOf(dashPath, dashPath),
                                                                        0f
                                                                    )
                                                                )
                                                            )
                                                        }
                                                    }
                                                },
                                            fontSize = 15.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
