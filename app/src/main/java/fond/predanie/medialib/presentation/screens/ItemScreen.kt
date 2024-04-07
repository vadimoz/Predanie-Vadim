package fund.predanie.medialib.presentation.screens

import android.content.Context
import android.content.Intent
import android.text.Html.fromHtml
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
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
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.slaviboy.composeunits.dh
import io.appmetrica.analytics.AppMetrica
import fund.predanie.medialib.R
import fund.predanie.medialib.domain.models.api.items.Tracks
import fund.predanie.medialib.presentation.MainViewModel
import fund.predanie.medialib.presentation.screens.accordion.AccordionGroup
import fund.predanie.medialib.presentation.screens.accordion.AccordionModel
import io.appmetrica.analytics.impl.id

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

    var textColor: Color? = null
    var backgroundColor: Color? = null

    if (isSystemInDarkTheme()){
        textColor = Color.White
        backgroundColor = Color.Black
    } else {
        textColor = Color(android.graphics.Color.parseColor("#2F2F2F"))
        backgroundColor = Color.White
    }

    LaunchedEffect(itemId) {
        if (itemId != null) {
            mainViewModel.getItemInfo(itemId.toInt())
        }

        //Событие статистики
        val eventParameters: MutableMap<String, Any> = HashMap()
        eventParameters["name"] = uiState.itemInto!!.data?.name.toString()
        AppMetrica.reportEvent("Item", eventParameters)
    }

    DisposableEffect(itemId) {
        onDispose {
            mainViewModel.cleanItemState()
        }
    }

    if (itemId != null) {
        var paddingWithPlayer = 0
        if(uiState.playerController?.isPlaying == true) {
            paddingWithPlayer = 70
        }
        LazyColumn(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(bottom = paddingWithPlayer.dp)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                ) {
                    val matrix = ColorMatrix()
                    matrix.setToSaturation(0F)

                    var image = uiState.itemInto!!.data?.img_big
                    if(image == null){
                        image = "https://predanie.ru/img/no-image/work_200.png"
                    }

                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(image)
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
                                    colors = listOf(Color.Transparent, backgroundColor),
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
                                        color = backgroundColor,
                                        shape = RoundedCornerShape(8.dp),
                                        tonalElevation = 2.dp
                                    ) {
                                        Text(
                                            text = it1,
                                            color = textColor,
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
                                    color = backgroundColor,
                                    shape = RoundedCornerShape(8.dp),
                                    tonalElevation = 2.dp
                                ) {
                                    Text(
                                        text = it,
                                        color = textColor,
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
                                                    mainViewModel.prepareCompositionForPlayer(
                                                        uiState.itemInto?.data!!
                                                    )
                                                //Задаем вопрос пользователю - продолжить ли прослушивание
                                                //с последнего файла воспроизведенного в данном произведении
                                                //если с последнего - смотрим для композиции последний файл
                                                //по таймстампу ищем его в очереди playerList и идем на него
                                                //по порядковому номеру

                                                //Если произведение не проигрывалось (в табрице файлов нет записей)
                                                //запускаем просто play

                                                /*Log.d("COMPOSITION: ",
                                                    uiState.itemInto?.data!!.id.toString()
                                                )*/


                                                uiState.playerController?.setMediaItems(playerList)
                                                if(mainViewModel.checkCompositionPlayed(uiState.itemInto?.data!!.id.toString(), context) != null){
                                                    //Здесь композиция в которой что-то уже слушалось
                                                    //Выдаем запрос на проигрывание с начала или с файла на котором в последний раз остановились
                                                    //для начала просто проигрываем на файле где в последний раз остановились

                                                    val lastFilePlayer = mainViewModel.setPlayerToLastCompositionFile(uiState.itemInto?.data!!.id.toString(), context)

                                                    var countPlayerIndex = 0

                                                    for (item in playerList) {
                                                        if (lastFilePlayer != null) {
                                                            if(item.mediaId == lastFilePlayer.fileid){
                                                                uiState.playerController?.seekTo(countPlayerIndex, mainViewModel.getPlaylistFromDB("Main", context).playlistTime)
                                                                uiState.playerController?.play()
                                                                Toast.makeText(
                                                                    context, "Воспроизведение продолжено с файла на котором Вы остановились",
                                                                    Toast.LENGTH_LONG
                                                                ).show()
                                                            }
                                                        }

                                                        countPlayerIndex +=1
                                                    }

                                                } else {
                                                    uiState.playerController?.play()
                                                }
                                                navController.navigate("PlayerScreen")

                                                mainViewModel.playerVisible()

                                                //Событие статистики
                                                val eventParametersPlay: MutableMap<String, Any> = HashMap()
                                                eventParametersPlay["name"] = uiState.itemInto?.data!!.name.toString()
                                                AppMetrica.reportEvent("PlayAll", eventParametersPlay)
                                            },
                                        tint = textColor,
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
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clickable {
                                                val type = "text/plain"
                                                val subject = uiState.itemInto!!.data?.name
                                                val extraText = uiState.itemInto!!.data?.share_url
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
                                        tint = textColor
                                        // Color.Black.copy(alpha = 0.5f),
                                    )
                                    Icon(
                                        painter = painterResource(R.drawable.restart),
                                        contentDescription = "restart",
                                        modifier = Modifier.size(20.dp)
                                            .clickable {
                                                uiState.itemInto!!.data?.id?.let { it1 ->
                                                    mainViewModel.deleteCompositionPositions(
                                                        it1, context)
                                                }
                                            },
                                        tint = textColor,
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
                                                        uiState.itemInto!!.data?.name.toString(),
                                                        image = uiState.itemInto!!.data?.img_big.toString(),
                                                        context = context
                                                    )
                                                    mainViewModel.loadFavorites(context = context)
                                                }
                                                .fillMaxWidth(),
                                            tint = textColor,
                                        )
                                    } else {
                                        Icon(
                                            painter = painterResource(R.drawable.bookmark),
                                            contentDescription = "Fav",
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clickable {
                                                    mainViewModel.removeCompositionFromFavorite(
                                                        itemId,
                                                        context
                                                    )
                                                    mainViewModel.loadFavorites(context = context)
                                                }
                                                .fillMaxWidth(),
                                            tint = textColor,
                                        )
                                    }
                                    val showDownloadDialog = remember { mutableStateOf(false) }

                                    if (showDownloadDialog.value) {
                                        DownloadDialog(
                                            showDownloadDialog,
                                            playerList,
                                            mainViewModel = mainViewModel,
                                            context = context,
                                            itemId = itemId
                                        )
                                    }

                                    Icon(
                                        painter = painterResource(R.drawable.download),
                                        contentDescription = "DownloadAll",
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clickable {
                                                //Загружаем все файлы
                                                showDownloadDialog.value = true
                                            },
                                        tint = textColor,
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
                                        textAlign = textAlign,
                                        color = textColor
                                    )
                                }

                            }

                        }
                    }
                }

            }

            var globalItemCount = -1

            //файлы в частях
            item {
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
                        itemId = itemId,
                        showButtons = true
                    )
                }
            }

            //просто файлы
            item {
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
                    itemId = itemId,
                    showButtons = true
                )
            }
        }
    }
}

@Composable
fun DownloadDialog(
    showDownloadDialog: MutableState<Boolean>,
    playerList: ArrayList<MediaItem>,
    itemId: String?,
    mainViewModel: MainViewModel,
    context: Context
) {
    AlertDialog(
        onDismissRequest = {
            showDownloadDialog.value = false
        },
        confirmButton = {
            TextButton(onClick = {
                showDownloadDialog.value = false

                //Загружаем все файлы
                if (itemId != null) {
                    mainViewModel.downloadAll(playerList, context = context, itemId = itemId)
                }
                mainViewModel.loadDownloadedCompositions(context)
            })
            { Text(text = "Загрузить") }
        },
        dismissButton = {
            TextButton(onClick = { showDownloadDialog.value = false })
            { Text(text = "Отменить") }
        },
        title = { Text(text = "Загрузить все файлы?") },
        text = { Text(text = "") }
    )
}
