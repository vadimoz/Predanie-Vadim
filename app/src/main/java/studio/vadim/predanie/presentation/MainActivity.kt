package studio.vadim.predanie.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import studio.vadim.predanie.presentation.theme.PredanieTheme

class MainActivity : ComponentActivity() {

    val mainViewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel.init()

        setContent {
            val navController = rememberNavController()

            PredanieTheme {
                NavHost(
                    navController = navController,
                    startDestination = "MainScreen"
                ) {
                    composable("MainScreen") {
                        MainScreen(name = "MainScreen", mainViewModel, onClick = {
                            navController.navigate("ItemScreen")
                        })
                    }
                    composable("ItemScreen") {
                        ItemScreen(name = "New", onClick = {
                            navController.navigate("MainScreen")
                        })
                    }
                }
            }
        }
    }
}