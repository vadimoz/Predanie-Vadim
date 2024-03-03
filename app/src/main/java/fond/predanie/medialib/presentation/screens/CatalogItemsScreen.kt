package fund.predanie.medialib.presentation.screens

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import io.appmetrica.analytics.AppMetrica
import fund.predanie.medialib.presentation.MainViewModel

@Composable
fun CatalogItemsScreen(
    mainViewModel: MainViewModel,
    navController: NavHostController,
    catalogId: String?,
    catalogName: String?
) {
    val uiState by mainViewModel.uiState.collectAsState()
    val catalogItemsList = uiState.catalogItemsList?.collectAsLazyPagingItems()

    var textColor: Color? = null
    var backgroundColor: Color? = null

    if (isSystemInDarkTheme()){
        textColor = Color.White
        backgroundColor = Color.Black
    } else {
        textColor = Color(android.graphics.Color.parseColor("#2F2F2F"))
        backgroundColor = Color.White
    }

    LaunchedEffect(catalogId) {
        mainViewModel.getCatalogItemsList(catalogId)

        //Событие статистики
        val eventParametersPlay: MutableMap<String, Any> = HashMap()
        eventParametersPlay["name"] = catalogName.toString()
        AppMetrica.reportEvent("CatalogShow", eventParametersPlay)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center)
                .verticalScroll(rememberScrollState())
        ) {
            Row(modifier = Modifier.padding(top = 20.dp, start = 20.dp, bottom = 20.dp)) {
                Text(
                    modifier = Modifier.padding(top = 8.dp),
                    text = "§",
                    fontSize = 25.sp,
                    color = Color(android.graphics.Color.parseColor("#FFD600"))
                )
                Text(
                    modifier = Modifier.padding(start = 5.dp),
                    text = catalogName.toString(),
                    fontSize = 35.sp,
                    color = textColor
                )
            }

            if (catalogItemsList != null) {
                NonlazyGrid(
                    columns = 3,
                    itemCount = catalogItemsList.itemCount,
                    modifier = Modifier
                        .padding(start = 7.5.dp, end = 7.5.dp)
                ) {
                    catalogItemsList[it]?.let { it1 ->
                        ListRow(
                            model = it1,
                            navController,
                            mainViewModel
                        )
                    }
                }
            }
        }
    }

}