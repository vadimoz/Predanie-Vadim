package studio.vadim.predanie.presentation.screens.accordion.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val quickSandFontFamily = FontFamily(
)

// Set of Material typography styles to start with
val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    //defaultFontFamily = fontFamily
    /* Other default text styles to override
    button = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.W500,
        fontSize = 14.sp
    ),
    caption = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    )
    */
)

val tags = Typography.displayLarge.copy(fontFamily = quickSandFontFamily, fontWeight = FontWeight.Bold)

val bodyBold = Typography.displayLarge.copy(fontWeight = FontWeight.Bold)

val accordionHeaderStyle = Typography.displayLarge.copy(fontWeight = FontWeight.Bold)