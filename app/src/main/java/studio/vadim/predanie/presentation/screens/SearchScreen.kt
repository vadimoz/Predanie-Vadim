package studio.vadim.predanie.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import studio.vadim.predanie.R
import studio.vadim.predanie.presentation.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    mainViewModel: MainViewModel,
    navController: NavHostController,
    query: String?
) {

    val uiState by mainViewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.white))
            .wrapContentSize(Alignment.TopCenter)
            .verticalScroll(rememberScrollState())
    ) {

        val focusRequester = remember { FocusRequester() }
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()

            if (query != "{query}") {
                mainViewModel.setSearchQuery(query)
            }

        }

        TextField(
            value = uiState.searchString,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .focusRequester(focusRequester),
            onValueChange = {
                mainViewModel.searchQueryUpdate(it)
            },
            textStyle = TextStyle(fontSize = 28.sp),
            placeholder = { "Найти" },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.LightGray
            )
        )

        val authorsItems =
            uiState.searchList.entities.filter { s -> s.entity_type == "author" }

        val compositionsItems =
            uiState.searchList.entities.filter { s -> s.entity_type == "composition" }

        Row(modifier = Modifier.padding(top = 20.dp, start = 20.dp, bottom = 5.dp)) {
            Text(
                modifier = Modifier.padding(top = 8.dp),
                text = "§",
                fontSize = 25.sp,
                color = Color(android.graphics.Color.parseColor("#FFD600"))
            )
            Text(
                modifier = Modifier.padding(start = 5.dp),
                text = "Авторы",
                fontSize = 35.sp,
                color = Color(android.graphics.Color.parseColor("#2F2F2F"))
            )

        }

        LazyRow() {
            items(authorsItems.count()) { index ->
                ListAuthorsRow(model = authorsItems[index], navController)
            }
        }

        Row(modifier = Modifier.padding(top = 5.dp, start = 20.dp, bottom = 20.dp)) {
            Text(
                modifier = Modifier.padding(top = 8.dp),
                text = "§",
                fontSize = 25.sp,
                color = Color(android.graphics.Color.parseColor("#FFD600"))
            )
            Text(
                modifier = Modifier.padding(start = 5.dp),
                text = "Произведения",
                fontSize = 35.sp,
                color = Color(android.graphics.Color.parseColor("#2F2F2F"))
            )

        }

        NonlazyGrid(
            columns = 3,
            itemCount = compositionsItems.count(),
            modifier = Modifier
                .padding(start = 7.5.dp, end = 7.5.dp)
        ) {
            ListRow(
                model = compositionsItems[it],
                navController,
                mainViewModel
            )
        }
    }
}