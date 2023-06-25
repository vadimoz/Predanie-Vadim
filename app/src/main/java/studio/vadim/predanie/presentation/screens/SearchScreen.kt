package studio.vadim.predanie.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import studio.vadim.predanie.R
import studio.vadim.predanie.presentation.MainViewModel

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
            textStyle = TextStyle(fontSize = 28.sp),
            placeholder = { "Найти..." }
        )
        LazyVerticalGrid(columns = GridCells.Adaptive(128.dp)) {
            items(uiState.searchList.entities.count()) { index ->
                ListRow(model = uiState.searchList.entities[index])
            }
        }
    }
}