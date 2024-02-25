package fund.predanie.medialib

import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navDeepLink
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.slaviboy.composeunits.initSize
import fond.predanie.medialib.presentation.screens.PlayerScreen
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
import fund.predanie.medialib.presentation.screens.PostScreen
import fund.predanie.medialib.presentation.screens.ProfileScreen
import fund.predanie.medialib.presentation.screens.QuickScreen
import fund.predanie.medialib.presentation.screens.SearchScreen
import fund.predanie.medialib.presentation.screens.SplashScreen
import fund.predanie.medialib.presentation.theme.PredanieTheme
import org.koin.androidx.viewmodel.ext.android.viewModel


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

        playerController.addListener(
            object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    if (isPlaying) {
                        //обновляем в плеере тайтл и автора
                        mainViewModel.updatePlayerInfo()

                    } else {
                        // Not playing because playback is paused, ended, suppressed, or the player
                        // is buffering, stopped or failed. Check player.playWhenReady,
                        // player.playbackState, player.playbackSuppressionReason and
                        // player.playerError for details.
                    }
                }
            }
        )

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
                    BottomPlayerBar(
                        modifier = Modifier
                            .align(Alignment.BottomCenter),
                        playerState = uiState.playerController,
                        //onBarClick = { navController.navigate(NavigationItem.Profile) }
                    )
                }
            },
            bottomBar = {
                /*if ((currentRoute(navController) != NavigationItem.Splash.route) && (currentRoute(
                        navController
                    ) != NavigationItem.Player.route)
                ) {*/
                BottomNavigationBar(navController)
                //}
            },
        )
    }

    @Composable
    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    fun BottomPlayerBar(
        modifier: Modifier = Modifier,
        //onEvent: (HomeEvent) -> Unit,
        playerState: MediaController?,
        //onBarClick: () -> Unit
    ) {
        val uiState by mainViewModel.uiState.collectAsState()
        AnimatedVisibility(
            visible = true,
            modifier = modifier
        ) {

            if (uiState.isPlayerVisible || uiState.mainPlaylist?.playlistJson?.isNotEmpty() == true) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        /*.pointerInput(Unit) {
                            detectDragGestures(
                                onDragEnd = {
                                    when {
                                        offsetX > 0 -> {
                                            onEvent(HomeEvent.SkipToPreviousSong)
                                        }

                                        offsetX < 0 -> {
                                            onEvent(HomeEvent.SkipToNextSong)
                                        }
                                    }
                                },
                                onDrag = { change, dragAmount ->
                                    change.consume()
                                    val (x, _) = dragAmount
                                    offsetX = x
                                }
                            )
                        }*/
                        .background(
                            if (!isSystemInDarkTheme()) {
                                Color.LightGray
                            } else Color.DarkGray
                        ),
                ) {
                    HomeBottomBarItem(
                        /*song = song,
                        onEvent = onEvent,
                        playerState = playerState,
                        onBarClick = onBarClick*/
                    )
                }

            }
        }
    }

    @Composable
    fun HomeBottomBarItem(
    ) {
        Box(
            modifier = Modifier
                .height(64.dp)
                .clickable(onClick = { navController.navigate("PlayerScreen") })

        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = rememberAsyncImagePainter("https://predanie.ru/assets/img/logo.png"),
                    contentDescription = "song",//song.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(48.dp)
                        .offset(16.dp)
                )
                Column(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(vertical = 8.dp, horizontal = 32.dp),
                ) {
                    Text(
                        "song",//song.title,
                        //style = MaterialTheme.typography.body2,
                        //color = MaterialTheme.colors.onBackground,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )

                    Text(
                        "song",//song.title,
                        //style = MaterialTheme.typography.body2,
                        //color = MaterialTheme.colors.onBackground,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .graphicsLayer {
                                alpha = 0.60f
                            }

                    )
                }
                /*val painter = rememberAsyncImagePainter(
                    if (playerState == PlayerState.PLAYING) {
                        R.drawable.ic_round_pause
                    } else {
                        R.drawable.ic_round_play_arrow
                    }
                )*/

                /*Image(
                    painter = painter,
                    contentDescription = "Music",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .size(48.dp)
                        .clickable(
                            interactionSource = remember {
                                MutableInteractionSource()
                            },
                            indication = rememberRipple(
                                bounded = false,
                                radius = 24.dp
                            )
                        ) {
                            if (playerState == PlayerState.PLAYING) {
                                onEvent(HomeEvent.PauseSong)
                            } else {
                                onEvent(HomeEvent.ResumeSong)
                            }
                        },
                )*/

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
