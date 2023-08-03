package studio.vadim.predanie.presentation.screens

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.text.Html
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.ui.PlayerView
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import studio.vadim.predanie.presentation.MainViewModel

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun PostScreen(mainViewModel: MainViewModel, navController: NavHostController, postId: String?) {
    val uiState by mainViewModel.uiState.collectAsState()

    DisposableEffect(postId) {
        onDispose {
            mainViewModel.cleanPostState()
        }
    }

    if (postId != null) {
        mainViewModel.getPostInfo(postId)
    }

    Column(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .width(250.dp)
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(uiState.postInfo?._embedded?.wp_featuredmedia?.get(0)?.source_url)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .diskCachePolicy(CachePolicy.ENABLED)
                .build(),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .size(250.dp),
            contentScale = ContentScale.Crop
        )
        Text(
            modifier = Modifier
                .padding(top = 10.dp),

            lineHeight = 33.sp,
            fontSize = 28.sp,
            text = uiState.postInfo?.title?.rendered.toString()
        )
        /*Text(
            modifier = Modifier
                .padding(top = 10.dp),

            lineHeight = 27.sp,
            fontSize = 20.sp,
            text = Html.fromHtml(uiState.postInfo?.content?.rendered.toString()).toString()
        )*/

        val mUrl = "https://www.geeksforgeeks.org"

        AndroidView(factory = {
            WebView(it).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                webViewClient = WebViewClient()
            }
        }, update = {
            val htmlContent =
                "<!DOCTYPE html> <html> <head> </head><meta name= viewport content= width=device-width  initial-scale=1.0 > <style>img{display: inline;height: auto;max-width: 100%;} video{display: inline;width: 100%;poster=} p{height: auto;width: 100%; font-size: 18px;font-family:serif;} iframe{width: 100%} </style> <body>   ${uiState.postInfo?.content?.rendered?.replace("\"","")} </body></html>"

            it.loadDataWithBaseURL(
                null,
                htmlContent,
                "text/html; charset=utf-8",
                "UTF-8",
                null
            )

            it.settings.apply {
                javaScriptEnabled = true
                loadWithOverviewMode = true
                useWideViewPort = true
            }
        })
    }
}