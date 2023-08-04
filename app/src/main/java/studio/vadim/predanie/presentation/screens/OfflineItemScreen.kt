package studio.vadim.predanie.presentation.screens

import android.text.Html.fromHtml
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaItem
import androidx.media3.extractor.mp4.Track
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.slaviboy.composeunits.dh
import studio.vadim.predanie.R
import studio.vadim.predanie.data.room.AppDatabase
import studio.vadim.predanie.domain.models.api.items.Tracks
import studio.vadim.predanie.presentation.MainViewModel
import studio.vadim.predanie.presentation.navigation.NavigationItem
import studio.vadim.predanie.presentation.screens.accordion.AccordionGroup
import studio.vadim.predanie.presentation.screens.accordion.AccordionModel

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
fun  OfflineItemScreen(
    mainViewModel: MainViewModel,
    itemId: String?,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    textModifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current.copy(
        color = Color.Black,
        fontSize = 18.sp,
    ),
    fontStyle: FontStyle? = null,
    collapsedMaxLine: Int = 4,
    showMoreText: String = " ... Развернуть",
    showMoreStyle: SpanStyle = SpanStyle(
        fontWeight = FontWeight.W500,
        fontSize = 16.sp,
        color = Color.Black
    ),
    showLessText: String = " Свернуть",
    showLessStyle: SpanStyle = showMoreStyle,
    textAlign: TextAlign? = null
) {
    val uiState by mainViewModel.uiState.collectAsState()

    val composition = AppDatabase.getInstance(LocalContext.current).downloadedCompositionsDao().findById(itemId.toString())

    //Уходим на главную, если в базе нет элемента
    /*if(composition == null){
        navController.navigate(NavigationItem.Home.route) {
            popUpTo(NavigationItem.OfflineItem.route){
                inclusive = true
            }
        }
    }*/

    val playerList = composition.playlistJson

    val ptsans = FontFamily(
        Font(R.raw.ptsans),
    )

    DisposableEffect(itemId) {
        onDispose {
            mainViewModel.cleanItemState()
        }
    }

    if (itemId != null) {
        mainViewModel.getItemInfo(itemId.toInt())
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {
                val matrix = ColorMatrix()
                matrix.setToSaturation(0F)

                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(composition.image)
                        .memoryCachePolicy(CachePolicy.ENABLED)
                        .diskCachePolicy(CachePolicy.ENABLED)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                    //colorFilter = ColorFilter.colorMatrix(matrix),
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxSize()
                )

                val boxSize = with(LocalDensity.current) { 0.5.dh.toPx() }
                Column(
                    Modifier
                        .align(Alignment.TopCenter)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(Color.Transparent, Color.White),
                                start = Offset(0f, 0f), // top left corner
                                end = Offset(1f, boxSize) // bottom right corner
                            )
                        )
                ) {

                    Column(
                        Modifier
                            .padding(top = 0.3.dh)
                    ) {

                        composition.title.let {
                            Surface(
                                modifier = Modifier.padding(
                                    start = 20.dp,
                                    end = 20.dp,
                                    bottom = 0.dp
                                ),
                                color = Color.White,
                                shape = RoundedCornerShape(8.dp),
                                tonalElevation = 2.dp
                            ) {
                                Text(
                                    text = it,
                                    color = Color.DarkGray,
                                    fontSize = 36.sp,
                                    style = TextStyle(fontFamily = ptsans),
                                    modifier = Modifier.padding(
                                        start = 20.dp,
                                        end = 20.dp,
                                        top = 0.dp
                                    )
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {

                                Icon(
                                    painter = painterResource(R.drawable.playall),
                                    contentDescription = "Play",
                                    modifier = Modifier
                                        .size(128.dp)
                                        .clickable {
                                            uiState.playerController?.setMediaItems(playerList)
                                            navController.navigate("ProfileScreen/play")
                                        },
                                    tint = Color.Black.copy(alpha = 0.5f),
                                )
                            }
                        }

                        var globalItemCount = -1

                        var counter = 1
                        var partCount = -1
                        val rows = mutableListOf<Tracks>()

                        for (item in playerList) {
                            rows.add(Tracks(id = item.mediaId, name = item.mediaMetadata.title.toString(), url = item.mediaMetadata.description.toString()))
                            counter += 1
                            globalItemCount++
                            partCount++
                        }

                        val parts = AccordionModel(
                            header = "",
                            rows
                        )

                        val group = listOf(parts)
                        AccordionGroup(
                            modifier = Modifier.padding(top = 8.dp),
                            group = group,
                            exp = true,
                            playerList = playerList,
                            navController = navController,
                            mainViewModel = mainViewModel,
                            globalItemCount = globalItemCount,
                            partCount = partCount,
                            itemId = itemId
                        )
                    }
                }
            }

        }
    }
}