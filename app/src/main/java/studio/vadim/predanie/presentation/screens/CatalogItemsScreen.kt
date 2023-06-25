package studio.vadim.predanie.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import studio.vadim.predanie.R
import studio.vadim.predanie.presentation.MainViewModel

@Composable
fun CatalogItemsScreen(
    mainViewModel: MainViewModel,
    navController: NavHostController,
    catalogId: String?
) {
    val uiState by mainViewModel.uiState.collectAsState()
    val catalogItemsList = uiState.catalogItemsList?.collectAsLazyPagingItems()

    LaunchedEffect(catalogId) {
        mainViewModel.getCatalogItemsList(catalogId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.white))
            .wrapContentSize(Alignment.Center)
    ) {
        LazyVerticalGrid(columns = GridCells.Adaptive(128.dp)) {
            if (catalogItemsList != null) {
                items(catalogItemsList.itemCount) { index ->
                    catalogItemsList[index]?.let { ListRow(model = it, navController) }
                }
            }
        }
    }
}