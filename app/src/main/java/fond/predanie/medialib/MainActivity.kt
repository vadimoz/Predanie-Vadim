package fund.predanie.medialib

import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
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
import fund.predanie.medialib.presentation.MainViewModel
import fund.predanie.medialib.presentation.navigation.NavigationItem
import fund.predanie.medialib.presentation.playerService.PlayerService
import fund.predanie.medialib.presentation.screens.AuthorScreen
import fund.predanie.medialib.presentation.screens.CatalogItemsScreen
import fund.predanie.medialib.presentation.screens.CatalogScreen
import fund.predanie.medialib.presentation.screens.FundScreen
import fund.predanie.medialib.presentation.screens.HomeScreen
import fund.predanie.medialib.presentation.screens.ItemScreen
import fund.predanie.medialib.presentation.screens.OfflineItemScreen
import fond.predanie.medialib.presentation.screens.PlayerScreen
import fund.predanie.medialib.presentation.screens.PostScreen
import fund.predanie.medialib.presentation.screens.ProfileScreen
import fund.predanie.medialib.presentation.screens.QuickScreen
import fund.predanie.medialib.presentation.screens.SearchScreen
import fund.predanie.medialib.presentation.screens.SplashScreen
import fund.predanie.medialib.presentation.theme.PredanieTheme


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
    private fun initController() {
        val currentPlaylistFromDB = mainViewModel.getPlaylistFromDB("Main", this)

        playerController.addMediaItems(currentPlaylistFromDB.playlistJson)
        playerController.prepare()
        playerController.pause()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainViewModel.initMainPlaylist(applicationContext)
        mainViewModel.loadDownloadedCompositions(this)
        mainViewModel.loadHistoryCompositions(this)
        mainViewModel.loadFavorites(this)
        mainViewModel.loadSettingsFromStore(this)
        mainViewModel.loadPlaylists(this)

        initSize()
        setContent {
            PredanieTheme() {
                MainScreen()
            }
        }

        if (ContextCompat.checkSelfPermission(
                this,
                POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(POST_NOTIFICATIONS), 1);
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
    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    fun MainScreen() {
        val uiState by mainViewModel.uiState.collectAsState()
        navController = rememberAnimatedNavController()
        val context = LocalContext.current

        Scaffold(
            content = { padding ->
                Box(modifier = Modifier.padding(padding)) {
                    Navigation(navController = navController)
                }
            },
            bottomBar = {
                if ((currentRoute(navController) != NavigationItem.Splash.route) && (currentRoute(
                        navController
                    ) != NavigationItem.Player.route)
                ) {
                    BottomNavigationBar(navController)
                }
            },
        )
        if ((currentRoute(navController) != NavigationItem.Splash.route) && (currentRoute(
                navController
            ) != NavigationItem.Player.route)
        ) {
            if (uiState.mainPlaylist?.playlistJson?.isNotEmpty() == true) {
                Column(
                    Modifier.fillMaxHeight()
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    Row(
                        horizontalArrangement = Arrangement.End, modifier = Modifier
                            .fillMaxWidth()

                            .padding(start = 250.dp)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color(android.graphics.Color.parseColor("#FFD600")),
                                        Color.LightGray
                                    )
                                ), shape = RoundedCornerShape(15.dp, 15.dp, 0.dp, 0.dp)
                            )
                            .clip(shape = RoundedCornerShape(15.dp, 15.dp, 0.dp, 0.dp))
                            .wrapContentWidth()
                    ) {

                        Icon(
                            painter = painterResource(R.drawable.fullscreen),
                            contentDescription = "Fav",
                            modifier = Modifier
                                .size(30.dp)
                                .clickable {
                                    navController.navigate("VideoPlayerScreen")
                                }
                                .fillMaxWidth()
                                .padding(5.dp),
                            tint = Color.White
                        )
                        if (uiState.isPlayerVisible) {
                            Icon(
                                painter = painterResource(R.drawable.down),
                                contentDescription = "Fav",
                                modifier = Modifier
                                    .size(30.dp)
                                    .clickable {
                                        mainViewModel.togglePlayer()
                                    }
                                    .fillMaxWidth()
                                    .padding(5.dp),
                                tint = Color.White
                            )
                        } else {
                            Icon(
                                painter = painterResource(R.drawable.up),
                                contentDescription = "Fav",
                                modifier = Modifier
                                    .size(30.dp)
                                    .clickable {
                                        mainViewModel.togglePlayer()
                                    }
                                    .fillMaxWidth()
                                    .padding(5.dp),
                                tint = Color.White
                            )
                        }
                        Icon(
                            painter = painterResource(R.drawable.delete),
                            contentDescription = "Fav",
                            modifier = Modifier
                                .size(30.dp)
                                .clickable {
                                    mainViewModel.cleanQueue(context)
                                }
                                .fillMaxWidth()
                                .padding(5.dp),
                            tint = Color.White
                        )
                    }
                    if(uiState.isPlayerVisible) {
                        NavigationBar(
                            modifier = Modifier
                                .height(280.dp)
                                .padding(bottom = 80.dp)
                        ) {
                            AndroidView(
                                factory = { context ->
                                    PlayerView(context).apply {
                                        player = uiState.playerController
                                        useController = true
                                        resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                                        controllerAutoShow = true
                                        controllerHideOnTouch = true
                                        setShowPreviousButton(true)
                                        setShowNextButton(true)
                                        setShowRewindButton(true)
                                        defaultArtwork = getResources().getDrawable(R.drawable.artwork)
                                        setShowFastForwardButton(true)
                                        controllerShowTimeoutMs = 0
                                        showController()
                                    }
                                },
                                update = {
                                    it.player = uiState.playerController
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    public fun currentRoute(navController: NavHostController): String? {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        return navBackStackEntry?.destination?.route
    }

    @OptIn(ExperimentalAnimationApi::class)
    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    @Composable
    fun Navigation(navController: NavHostController) {
        var startDestination: String = ""

        //Если клик по нотификейшн-плееру - ставим дефолт скриин - плеер (Мое), если нет интернета - открываем страничку
        //Мое, если все ок, то сплэш
        startDestination = if (getIntent().getStringExtra("player") == "true") {
            NavigationItem.Home.route
        } else if (!mainViewModel.isInternetConnected(this)) {
            NavigationItem.Profile.route
        } else {
            NavigationItem.Home.route
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
            /*composable(
                NavigationItem.Player.route,
                deepLinks = listOf(navDeepLink {
                    uriPattern = "https://predanie.ru/player"
                }),
            ) {
                VideoPlayerScreen(mainViewModel = mainViewModel, navController)
            }*/
            composable(
                NavigationItem.Player.route,
                deepLinks = listOf(navDeepLink {
                    uriPattern = "https://predanie.ru/VideoPlayer"
                }),
            ) {
                PlayerScreen(mainViewModel = mainViewModel, navController, action = null)
            }
            composable(
                NavigationItem.Post.route,
            ) { navBackStackEntry ->
                val postId = navBackStackEntry.arguments?.getString("postId")
                PostScreen(mainViewModel = mainViewModel, navController, postId)
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
            composable(NavigationItem.OfflineItem.route) { navBackStackEntry ->
                val itemId = navBackStackEntry.arguments?.getString("itemId")

                OfflineItemScreen(
                    mainViewModel = mainViewModel,
                    itemId,
                    navController = navController
                )
            }
        }
    }

    @Composable
    fun BottomNavigationBar(navController: NavController) {
        var items = listOf<NavigationItem>()
        if (mainViewModel.isInternetConnected(this)) {
            items = listOf(
                NavigationItem.Home,
                NavigationItem.Catalog,
                NavigationItem.Profile,
                NavigationItem.Search,
                NavigationItem.Fund
            )
        } else {
            items = listOf(
                NavigationItem.Profile,
            )
        }
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
