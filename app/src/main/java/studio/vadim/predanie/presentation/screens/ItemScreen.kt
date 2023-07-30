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
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.slaviboy.composeunits.dh
import studio.vadim.predanie.R
import studio.vadim.predanie.domain.models.api.items.Tracks
import studio.vadim.predanie.presentation.MainViewModel
import studio.vadim.predanie.presentation.screens.accordion.AccordionGroup
import studio.vadim.predanie.presentation.screens.accordion.AccordionModel

@Composable
fun ItemScreen(
    mainViewModel: MainViewModel, itemId: String?,
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

    val context = LocalContext.current

    var playerList = mainViewModel.prepareCompositionForPlayer(uiState.itemInto?.data!!)

    val isFavorite = mainViewModel.isCompositionFavorite(itemId.toString(), context)

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
                        .data(uiState.itemInto!!.data?.img_big)
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

                        uiState.itemInto?.data?.author_name?.let { it1 ->

                            if (it1 != "Без автора") {
                                Surface(
                                    modifier = Modifier.padding(
                                        start = 20.dp,
                                        end = 20.dp,
                                        bottom = 10.dp
                                    ),
                                    color = Color.White,
                                    shape = RoundedCornerShape(8.dp),
                                    tonalElevation = 2.dp
                                ) {
                                    Text(
                                        text = it1,
                                        color = Color.Black,
                                        fontSize = 12.sp,
                                        style = TextStyle(fontFamily = ptsans),
                                        modifier = Modifier
                                            .padding(
                                                start = 5.dp,
                                                end = 5.dp,
                                                bottom = 0.dp
                                            )
                                            .clickable {
                                                navController.navigate("AuthorScreen/${uiState.itemInto?.data?.author_id}")
                                            }
                                    )
                                }
                            }
                        }

                        uiState.itemInto?.data?.name?.let {
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
                                            playerList =
                                                mainViewModel.prepareCompositionForPlayer(uiState.itemInto?.data!!)
                                            uiState.playerController?.setMediaItems(playerList)

                                            //Ставим композицию в историю
                                            mainViewModel.setCompositionToHistory(
                                                itemId,
                                                context = context,
                                                title = uiState.itemInto!!.data?.name.toString(),
                                                image = uiState.itemInto!!.data?.img_big.toString()
                                            )
                                            mainViewModel.loadHistoryCompositions(context)

                                            navController.navigate("ProfileScreen/play")
                                        },
                                    tint = Color.Black.copy(alpha = 0.5f),
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.share),
                                    contentDescription = "Play",
                                    modifier = Modifier.size(20.dp),
                                    tint = Color.Black.copy(alpha = 0.5f),
                                )
                                Icon(
                                    painter = painterResource(R.drawable.playlist_add),
                                    contentDescription = "Play",
                                    modifier = Modifier.size(20.dp),
                                    tint = Color.Black.copy(alpha = 0.5f),
                                )
                                if (!isFavorite) {
                                    Icon(
                                        painter = painterResource(R.drawable.bookmark),
                                        contentDescription = "Fav",
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clickable {
                                                mainViewModel.setCompositionToFavorites(
                                                    itemId = itemId,
                                                    uiState.authorInto.data?.name.toString(),
                                                    image = uiState.authorInto.data?.img.toString(),
                                                    context = context
                                                )
                                                mainViewModel.loadFavorites(context = context)
                                            }
                                            .fillMaxWidth(),
                                        tint = Color.Black.copy(alpha = 0.5f),
                                    )
                                } else {
                                    Icon(
                                        painter = painterResource(R.drawable.bookmark),
                                        contentDescription = "Fav",
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clickable {
                                                mainViewModel.removeCompositionFromFavorite(itemId, context)
                                                mainViewModel.loadFavorites(context = context)
                                            }
                                            .fillMaxWidth(),
                                        tint = Color(android.graphics.Color.parseColor("#FFD600")),
                                    )
                                }
                                Icon(
                                    painter = painterResource(R.drawable.dots),
                                    contentDescription = "Play",
                                    modifier = Modifier.size(20.dp),
                                    tint = Color.Black.copy(alpha = 0.5f),
                                )
                            }
                        }

                        uiState.itemInto?.data?.desc?.let {
                            var isExpanded by remember { mutableStateOf(false) }
                            var clickable by remember { mutableStateOf(false) }
                            var lastCharIndex by remember { mutableStateOf(0) }
                            val text = fromHtml(uiState.itemInto!!.data!!.desc)

                            Box(
                                modifier = Modifier
                                    .clickable(clickable) {
                                        isExpanded = !isExpanded
                                    }
                                    .then(Modifier.padding(20.dp))
                            ) {
                                Text(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .animateContentSize(),
                                    text = buildAnnotatedString {
                                        if (clickable) {
                                            if (isExpanded) {
                                                append(text)
                                                withStyle(style = showLessStyle) {
                                                    append(
                                                        showLessText
                                                    )
                                                }
                                            } else {
                                                val adjustText = text?.substring(
                                                    startIndex = 0,
                                                    endIndex = lastCharIndex
                                                )
                                                    ?.dropLast(showMoreText.length)
                                                    ?.dropLastWhile { Character.isWhitespace(it) || it == '.' }
                                                append(adjustText)
                                                withStyle(style = showMoreStyle) {
                                                    append(
                                                        showMoreText
                                                    )
                                                }
                                            }
                                        } else {
                                            append(text)
                                        }
                                    },
                                    maxLines = if (isExpanded) Int.MAX_VALUE else collapsedMaxLine,
                                    fontStyle = fontStyle,
                                    onTextLayout = { textLayoutResult ->
                                        if (!isExpanded && textLayoutResult.hasVisualOverflow) {
                                            clickable = true
                                            lastCharIndex =
                                                textLayoutResult.getLineEnd(collapsedMaxLine - 1)
                                        }
                                    },
                                    style = style,
                                    textAlign = textAlign
                                )
                            }

                        }

                        var globalItemCount = -1

                        for (part in uiState.itemInto?.data?.parts!!) {
                            val rows = mutableListOf<Tracks>()
                            val accordionItems =
                                uiState.itemInto?.data!!.tracks.filter { s -> s.parent == part.id.toString() }

                            var partCount = -1
                            for (item in accordionItems) {
                                rows.add(item)
                                globalItemCount++
                                partCount++
                            }

                            val parts = AccordionModel(
                                header = part.name.toString(),
                                rows
                            )

                            val group = listOf(parts)
                            AccordionGroup(
                                modifier = Modifier.padding(top = 8.dp),
                                group = group,
                                playerList = playerList,
                                navController = navController,
                                mainViewModel = mainViewModel,
                                globalItemCount = globalItemCount,
                                partCount = partCount,
                                itemId = itemId
                            )
                        }

                        val separateFiles =
                            uiState.itemInto!!.data!!.tracks.filter { s -> s.parent == null }

                        var counter = 1
                        var partCount = -1
                        val rows = mutableListOf<Tracks>()
                        for (item in separateFiles) {
                            rows.add(item)
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