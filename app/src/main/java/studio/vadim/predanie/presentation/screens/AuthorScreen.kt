package studio.vadim.predanie.presentation.screens

import android.text.Html
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LocalTextStyle
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.slaviboy.composeunits.dh
import studio.vadim.predanie.presentation.MainViewModel

@Composable
fun AuthorScreen(
    mainViewModel: MainViewModel, authorId: String?,
    modifier: Modifier = Modifier,
    navController: NavHostController,
    textModifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current.copy(
        color = Color.Black,
        fontSize = 18.sp,
        fontFamily = FontFamily.Serif
    ),
    fontStyle: FontStyle? = null,
    collapsedMaxLine: Int = 4,
    showMoreText: String = " ... Развернуть",
    showMoreStyle: SpanStyle = SpanStyle(
        fontWeight = FontWeight.W500,
        fontSize = 16.sp,
        color = Color.LightGray
    ),
    showLessText: String = " Свернуть",
    showLessStyle: SpanStyle = showMoreStyle,
    textAlign: TextAlign? = null
) {
    val uiState by mainViewModel.uiState.collectAsState()

    DisposableEffect(authorId) {
        onDispose {
            mainViewModel.cleanAuthorState()
        }
    }

    if (authorId != null) {
        mainViewModel.getAuthorInfo(authorId.toInt())

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
                    .background(color = Color.White)
                    .padding(20.dp)
            ) {
                AsyncImage(
                    model = uiState.authorInto.data?.img,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(0.3.dh)
                        .clip(CircleShape)
                        .border(2.dp, Color.LightGray, CircleShape)
                        .align(Alignment.TopCenter)
                )

                val boxSize = with(LocalDensity.current) { 0.5.dh.toPx() }
                Column(
                    Modifier
                        .align(Alignment.TopCenter)
                ) {

                    Column(
                        Modifier
                            .padding(top = 0.3.dh)
                    ) {
                        Text(
                            text = uiState.authorInto.data?.name.toString(),
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 36.sp,
                            modifier = Modifier.padding(20.dp)
                        )
                        uiState.authorInto.data?.desc?.let {

                            var isExpanded by remember { mutableStateOf(false) }
                            var clickable by remember { mutableStateOf(false) }
                            var lastCharIndex by remember { mutableStateOf(0) }
                            val text = Html.fromHtml(uiState.authorInto.data?.desc.toString())

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

                        if (uiState.authorInto.data?.compositions != null) {
                            NonlazyGrid(
                                columns = 3,
                                itemCount = uiState.authorInto.data?.compositions!!.count(),
                                modifier = Modifier
                                    .padding(start = 7.5.dp, end = 7.5.dp)
                            ) {
                                ListRow(
                                    model = uiState.authorInto.data?.compositions!![it],
                                    navController
                                )
                            }
                        }
                    }
                }
            }

        }
    }
}