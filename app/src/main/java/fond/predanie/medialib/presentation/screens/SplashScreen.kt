package fund.predanie.medialib.presentation.screens

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import fund.predanie.medialib.R
import fund.predanie.medialib.presentation.MainViewModel
import fund.predanie.medialib.presentation.navigation.NavigationItem


@Composable
fun SplashScreen(mainViewModel: MainViewModel, navController: NavHostController) {
    val uiState by mainViewModel.uiState.collectAsState()

    val newItems = uiState.newList.collectAsLazyPagingItems()
    val audioPopularList = uiState.audioPopularList.collectAsLazyPagingItems()
    val musicPopularList = uiState.musicPopularList.collectAsLazyPagingItems()
    val favoritesList = uiState.favoritesList.collectAsLazyPagingItems()

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.rain))
        val logoAnimationState =
            animateLottieCompositionAsState(composition = composition)
        LottieAnimation(
            composition = composition,
            progress = { logoAnimationState.progress },
            modifier = Modifier
                .align(Alignment.Center),
            contentScale = ContentScale.Crop,
        )

        Image(
            painterResource(R.drawable.logo), "Logo",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .size(500.dp)
        )

        if (logoAnimationState.isAtEnd && logoAnimationState.isPlaying) {
            navController.navigate(NavigationItem.Home.route) {
                popUpTo(NavigationItem.Splash.route){
                    inclusive = true
                }
            }
        }
    }
}

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}