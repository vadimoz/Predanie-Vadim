package fund.predanie.medialib.presentation.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import fund.predanie.medialib.presentation.MainViewModel
import fund.predanie.medialib.presentation.navigation.NavigationItem

@Composable
fun QuickScreen(mainViewModel: MainViewModel, navController: NavHostController) {
    navController.navigate("ProfileScreen/play") {
        popUpTo(NavigationItem.QuickSplash.route){
            inclusive = true
        }
    }
}