package studio.vadim.predanie.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.slaviboy.composeunits.initSize
import org.koin.androidx.viewmodel.ext.android.viewModel
import studio.vadim.predanie.R
import studio.vadim.predanie.presentation.navigation.NavigationItem
import studio.vadim.predanie.presentation.screens.AuthorScreen
import studio.vadim.predanie.presentation.screens.CatalogItemsScreen
import studio.vadim.predanie.presentation.screens.CatalogScreen
import studio.vadim.predanie.presentation.screens.HomeScreen
import studio.vadim.predanie.presentation.screens.ItemScreen
import studio.vadim.predanie.presentation.screens.SearchScreen
import studio.vadim.predanie.presentation.screens.SplashScreen

class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initSize()

        setContent {
            MainScreen()
        }
    }

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
    @Composable
    fun MainScreen() {
        val navController = rememberAnimatedNavController()
        Scaffold(
            content = { padding ->
                Box(modifier = Modifier.padding(padding)) {
                    Navigation(navController = navController)
                }
            },
            bottomBar = {
                if (currentRoute(navController) != NavigationItem.Splash.route) {
                    BottomNavigationBar(navController)
                }
            },
        )
    }

    @Composable
    public fun currentRoute(navController: NavHostController): String? {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        return navBackStackEntry?.destination?.route
    }

    @OptIn(ExperimentalAnimationApi::class)
    @Composable
    fun Navigation(navController: NavHostController) {
        AnimatedNavHost(navController, startDestination = NavigationItem.Splash.route) {
            composable(
                NavigationItem.Splash.route,
            ) {
                SplashScreen(mainViewModel = mainViewModel, navController)
            }
            composable(
                NavigationItem.Home.route,
                enterTransition = {
                    slideIntoContainer(
                        AnimatedContentScope.SlideDirection.Up,
                        animationSpec = tween(1000)
                    )
                },
                exitTransition = {
                    slideOutOfContainer(
                        AnimatedContentScope.SlideDirection.Down,
                        animationSpec = tween(1000)
                    )
                },
            ) {
                HomeScreen(mainViewModel = mainViewModel, navController)
            }
            composable(NavigationItem.Music.route) {
                CatalogScreen(mainViewModel = mainViewModel)
            }
            composable(NavigationItem.Movies.route) {
                SearchScreen(mainViewModel = mainViewModel)
            }
            composable(NavigationItem.Books.route) {
                CatalogItemsScreen()
            }
            composable(NavigationItem.Author.route) { navBackStackEntry ->
                val authorId = navBackStackEntry.arguments?.getString("authorId")
                AuthorScreen(
                    mainViewModel = mainViewModel,
                    authorId,
                    navController = navController
                )
            }
            composable(NavigationItem.Item.route) { navBackStackEntry ->
                val itemId = navBackStackEntry.arguments?.getString("itemId")

                ItemScreen(
                    mainViewModel = mainViewModel,
                    itemId
                )
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TopBar() {
        TopAppBar(
            title = { Text(text = stringResource(R.string.app_name), fontSize = 18.sp) },
        )
    }

    @Composable
    fun BottomNavigationBar(navController: NavController) {
        val items = listOf(
            NavigationItem.Home,
            NavigationItem.Music,
            NavigationItem.Movies,
            NavigationItem.Books,
            NavigationItem.Profile
        )
        NavigationBar(
            contentColor = Color.White
        ) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            items.forEach { item ->
                NavigationBarItem(
                    icon = {
                        Icon(
                            painterResource(id = item.icon),
                            contentDescription = item.title
                        )
                    },
                    label = { Text(text = item.title) },
                    alwaysShowLabel = true,
                    selected = currentRoute == item.route,
                    onClick = {
                        navController.navigate(item.route) {
                            // Pop up to the start destination of the graph to
                            // avoid building up a large stack of destinations
                            // on the back stack as users select items
                            navController.graph.startDestinationRoute?.let { route ->
                                popUpTo(route) {
                                    saveState = true
                                }
                            }
                            // Avoid multiple copies of the same destination when
                            // reselecting the same item
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            restoreState = true
                        }
                    }
                )
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun BottomNavigationBarPreview() {
        // BottomNavigationBar()
    }
}
