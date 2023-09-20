package studio.vadim.predanie.presentation.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import io.appmetrica.analytics.AppMetrica
import studio.vadim.predanie.R
import studio.vadim.predanie.presentation.MainViewModel

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun PostScreen(mainViewModel: MainViewModel, navController: NavHostController, postId: String?) {
    val uiState by mainViewModel.uiState.collectAsState()

    val context = LocalContext.current

    DisposableEffect(postId) {
        onDispose {
            mainViewModel.cleanPostState()
        }
    }

    LaunchedEffect(postId) {
        //Событие статистики
        val eventParametersPlay: MutableMap<String, Any> = HashMap()
        eventParametersPlay["name"] = uiState.postInfo?.title?.rendered.toString()
        AppMetrica.reportEvent("BlogPost", eventParametersPlay)
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
        horizontalAlignment = Alignment.CenterHorizontally,
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
        Icon(
            painter = painterResource(R.drawable.share),
            contentDescription = "Play",
            modifier = Modifier
                .size(30.dp)
                .padding(top = 10.dp)
                .clickable {
                    val type = "text/plain"
                    val subject = uiState.postInfo?.title?.rendered.toString()
                    val extraText = uiState.postInfo?.link
                    val shareWith = ""

                    val intent = Intent(Intent.ACTION_SEND)
                    intent.type = type
                    intent.putExtra(Intent.EXTRA_SUBJECT, subject)
                    intent.putExtra(Intent.EXTRA_TEXT, extraText)

                    ContextCompat.startActivity(
                        context,
                        Intent.createChooser(intent, shareWith),
                        null
                    )
                },
            tint = Color.Black.copy(alpha = 0.5f),
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

        AndroidView(factory = {
            WebView(it).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                webViewClient = object : WebViewClient() {
                    @Deprecated("Deprecated in Java")
                    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                        if (url.contains(".")) {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            view.getContext().startActivity(intent);
                            return true
                        } else {
                            view.loadUrl(url)
                        }
                        return false
                    }
                }
            }
        }, update = {
            val htmlContent =
                "<!DOCTYPE html> <html> <head> </head><meta name= viewport content= width=device-width  initial-scale=1.0 > <style>a{color:black;} img{display: inline;height: auto;max-width: 100%;} video{display: inline;width: 100%;poster=} p{height: auto;width: 100%; font-size: 18px;font-family:serif;} iframe{width: 100%} </style> <body>   ${
                    uiState.postInfo?.content?.rendered?.replace(
                        "\"",
                        ""
                    )
                } </body></html>"

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