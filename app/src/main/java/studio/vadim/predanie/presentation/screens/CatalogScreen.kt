package studio.vadim.predanie.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import studio.vadim.predanie.R
import studio.vadim.predanie.presentation.MainViewModel

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

                    FlowRow(
                        modifier = Modifier
                            .wrapContentSize(Alignment.Center)
                    ) {
                        uiState.catalogList.categories[index].categories.forEach() {
                            Text(it.name.toString())
                        }
                    }
                }
            }
        }
    }
}
