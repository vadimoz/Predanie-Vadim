package studio.vadim.predanie.presentation.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import studio.vadim.predanie.presentation.MainViewModel
import studio.vadim.predanie.presentation.navigation.NavigationItem


@Composable
fun QuickScreen(mainViewModel: MainViewModel, navController: NavHostController) {
    navController.navigate(NavigationItem.Player.route) {
        popUpTo(NavigationItem.QuickSplash.route){
            inclusive = true
        }
    }
}