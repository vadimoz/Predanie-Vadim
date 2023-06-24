package studio.vadim.predanie.presentation.screens

import android.text.Html.fromHtml
import android.text.TextUtils
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import androidx.compose.ui.viewinterop.AndroidView
import coil.load
import com.flaviofaria.kenburnsview.KenBurnsView
import com.flaviofaria.kenburnsview.RandomTransitionGenerator
import com.slaviboy.composeunits.dh
import studio.vadim.predanie.presentation.MainViewModel
import studio.vadim.predanie.presentation.screens.accordion.components.AccordionGroup
import studio.vadim.predanie.presentation.screens.accordion.components.AccordionModel

@Composable
fun ItemScreen(
    mainViewModel: MainViewModel, itemId: String?,
    modifier: Modifier = Modifier,
    textModifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current.copy(
        color = Color.White,
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
    val mainImage = uiState.itemInto.data?.img_big

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
                    .background(color = Color.Black)
            ) {
                if (!TextUtils.isEmpty(mainImage)) {

                    val interpolator = AccelerateDecelerateInterpolator()

                    val generator = RandomTransitionGenerator(12000, interpolator)

                    val customView = KenBurnsView(LocalContext.current).also { imageView ->
                        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
                        imageView.load(mainImage)
                    }

                    customView.setTransitionGenerator(generator)

                    AndroidView(
                        factory = { customView },
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                    )
                }

                val boxSize = with(LocalDensity.current) { 0.5.dh.toPx() }
                Column(
                    Modifier
                        .align(Alignment.TopCenter)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(Color.Transparent, Color.Black),
                                start = Offset(0f, 0f), // top left corner
                                end = Offset(1f, boxSize) // bottom right corner
                            )
                        )
                ) {

                    Column(
                        Modifier
                            .padding(top = 0.3.dh)
                    ) {
                        uiState.itemInto.data?.name?.let {
                            Text(
                                text = it,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 28.sp,
                                modifier = Modifier.padding(20.dp)
                            )
                        }

                        uiState.itemInto.data?.desc?.let {

                            var isExpanded by remember { mutableStateOf(false) }
                            var clickable by remember { mutableStateOf(false) }
                            var lastCharIndex by remember { mutableStateOf(0) }
                            val text = fromHtml(uiState.itemInto.data!!.desc)

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
                        val modelTechStocks = AccordionModel(
                            header = "Technology Stocks",
                            rows = mutableListOf(
                                AccordionModel.Row(security = "AAPL", "$328.00"),
                                AccordionModel.Row(security = "GOOGL", "$328.00"),
                                AccordionModel.Row(security = "NFLX", "$198.00"),
                                AccordionModel.Row(security = "META", "$200.00"),
                                AccordionModel.Row(security = "TSLA", "$769.16"),
                            )
                        )

                        val modelEnergyStocks = AccordionModel(
                            header = "Energy Stocks",
                            rows = mutableListOf(
                                AccordionModel.Row(security = "MPC", "$96.56"),
                                AccordionModel.Row(security = "CVX", "179.71"),
                                AccordionModel.Row(security = "DVN", "$77.13"),
                                AccordionModel.Row(security = "XOM", "$98.48"),
                                AccordionModel.Row(security = "XPRO", "$13.86"),
                            )
                        )

                        val modelDividendStocks = AccordionModel(
                            header = "Dividend Stocks",
                            rows = mutableListOf(
                                AccordionModel.Row(security = "JNJ", "$178.83"),
                                AccordionModel.Row(security = "PG", "$148.63"),
                                AccordionModel.Row(security = "KO", "$64.12"),
                                AccordionModel.Row(security = "BAC", "$37.42"),
                                AccordionModel.Row(security = "MA", "$333.33"),
                            )
                        )

                        val group = listOf(modelTechStocks, modelEnergyStocks, modelDividendStocks)

                        AccordionGroup(modifier = Modifier.padding(top = 8.dp), group = group)
                    }
                }
            }

        }
    }
}