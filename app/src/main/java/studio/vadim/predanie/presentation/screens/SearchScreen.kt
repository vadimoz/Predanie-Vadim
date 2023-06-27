package studio.vadim.predanie.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.navigation.NavHostController
import studio.vadim.predanie.R
import studio.vadim.predanie.presentation.MainViewModel

@Composable
fun SearchScreen(mainViewModel: MainViewModel,
                 navController: NavHostController) {

    val uiState by mainViewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.white))
            .wrapContentSize(Alignment.Center)
            .verticalScroll(rememberScrollState())
    ) {
        TextField(
            uiState.searchString,
            {
                mainViewModel.searchQueryUpdate(it)
            },
            textStyle = TextStyle(fontSize = 28.sp),
            placeholder = { "Найти..." }
        )

        val authorsItems =
            uiState.searchList.entities.filter { s -> s.entity_type == "author" }

        val compositionsItems =
            uiState.searchList.entities.filter { s -> s.entity_type == "composition" }

        LazyRow() {
            items(authorsItems.count()) { index ->
                ListAuthorsRow(model = authorsItems[index], navController)
            }
        }

        NonlazyGrid(
            columns = 3,
            itemCount = compositionsItems.count(),
            modifier = Modifier
                .padding(start = 7.5.dp, end = 7.5.dp)
        ) {
            ListRow(
                model = compositionsItems[it],
                navController
            )
        }
    }
}