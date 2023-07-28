package studio.vadim.predanie


import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navDeepLink
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.slaviboy.composeunits.initSize
import org.koin.androidx.viewmodel.ext.android.viewModel
import studio.vadim.predanie.data.room.AppDatabase
import studio.vadim.predanie.presentation.MainViewModel
import studio.vadim.predanie.presentation.PlayerService.PlayerService
import studio.vadim.predanie.presentation.navigation.NavigationItem
import studio.vadim.predanie.presentation.screens.AuthorScreen
import studio.vadim.predanie.presentation.screens.CatalogItemsScreen
import studio.vadim.predanie.presentation.screens.CatalogScreen
import studio.vadim.predanie.presentation.screens.FundScreen
import studio.vadim.predanie.presentation.screens.HomeScreen
import studio.vadim.predanie.presentation.screens.ItemScreen
import studio.vadim.predanie.presentation.screens.PlayerScreen
import studio.vadim.predanie.presentation.screens.ProfileScreen
import studio.vadim.predanie.presentation.screens.QuickScreen
import studio.vadim.predanie.presentation.screens.SearchScreen
import studio.vadim.predanie.presentation.screens.SplashScreen
import studio.vadim.predanie.presentation.theme.PredanieTheme


class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModel()

    private lateinit var controllerFuture: ListenableFuture<MediaController>
    private lateinit var playerController: MediaController

    private lateinit var navController: NavHostController


    override fun onStart() {
        super.onStart()
        val sessionToken = SessionToken(this, ComponentName(this, PlayerService::class.java))
        controllerFuture = MediaController.Builder(this, sessionToken).buildAsync()

        controllerFuture.addListener(
            {
                playerController = controllerFuture.get()
                mainViewModel.setPlayerInstance(playerController)
                initController()
            },
            MoreExecutors.directExecutor()
        )
    }

    override fun onStop() {
        super.onStop()
        MediaController.releaseFuture(controllerFuture)
    }

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    private fun  initController() {
        val currentPlaylistFromDB =
            AppDatabase.getInstance(applicationContext).mainPlaylistDao().findByName("Main")
        playerController.addMediaItems(currentPlaylistFromDB.playlistJson)
        playerController.prepare()
        playerController.pause()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel.initAppDb(applicationContext)

        initSize()
        setContent {
            PredanieTheme() {
                MainScreen()
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)

        //Интент на открытие плеера из нотификации
        if (getIntent().getStringExtra("player") == "true") {
            navController.navigate("ProfileScreen/noplay")
        }
    }

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
    @Composable
    fun MainScreen() {

        navController = rememberAnimatedNavController()
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
        var startDestination: String = ""

        if (getIntent().getStringExtra("player") == "true") {
            startDestination = NavigationItem.QuickSplash.route
        } else {
            startDestination = NavigationItem.Splash.route
        }
        AnimatedNavHost(navController, startDestination = startDestination) {
            composable(
                NavigationItem.Splash.route,
            ) {
                SplashScreen(mainViewModel = mainViewModel, navController)
            }
            composable(
                NavigationItem.Fund.route,
            ) {
                FundScreen()
            }
            composable(
                NavigationItem.Player.route,
                deepLinks = listOf(navDeepLink {
                    uriPattern = "https://predanie.ru/player"
                }),
            ) {
                PlayerScreen(mainViewModel = mainViewModel, navController)
            }
            composable(
                NavigationItem.Profile.route,
            ) { navBackStackEntry ->
                val play = navBackStackEntry.arguments?.getString("play")
                ProfileScreen(mainViewModel = mainViewModel, navController, action = play)
            }
            composable(
                NavigationItem.Home.route,
                /*enterTransition = {
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
                },*/
            ) {
                HomeScreen(mainViewModel = mainViewModel, navController)
            }
            composable(NavigationItem.Catalog.route) {
                CatalogScreen(
                    mainViewModel = mainViewModel,
                    navController = navController
                )
            }
            composable(NavigationItem.QuickSplash.route) {
                QuickScreen(
                    mainViewModel = mainViewModel,
                    navController = navController
                )
            }
            composable(NavigationItem.Search.route) { navBackStackEntry ->
                val query = navBackStackEntry.arguments?.getString("query")
                SearchScreen(
                    mainViewModel = mainViewModel,
                    navController,
                    query
                )
            }
            composable(NavigationItem.CatalogItems.route) { navBackStackEntry ->
                val catalogId = navBackStackEntry.arguments?.getString("catalogId")
                val catalogName = navBackStackEntry.arguments?.getString("catalogName")
                CatalogItemsScreen(
                    mainViewModel = mainViewModel,
                    navController = navController,
                    catalogId,
                    catalogName
                )
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
                    itemId,
                    navController = navController
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
            NavigationItem.Catalog,
            NavigationItem.Profile,
            NavigationItem.Search,
            NavigationItem.Fund
        )
        NavigationBar(
            contentColor = Color.White, containerColor = Color.White
        ) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            items.forEach { item ->
                NavigationBarItem(
                    icon = {
                        Icon(
                            painterResource(id = item.icon),
                            contentDescription = item.title,
                            modifier = Modifier.size(25.dp)
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
                            restoreState = false
                        }
                    },
                    colors = androidx.compose.material3.NavigationBarItemDefaults
                        .colors(
                            unselectedIconColor = Color.DarkGray,
                            selectedIconColor = Color(android.graphics.Color.parseColor("#FFD600")),
                            indicatorColor = Color.White,
                        )
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
