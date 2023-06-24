package studio.vadim.predanie.presentation.screens.accordion.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.List
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.unit.dp
import studio.vadim.predanie.domain.models.api.items.Tracks
import studio.vadim.predanie.presentation.screens.accordion.theme.*

data class AccordionModel(
    val header: String,
    val rows: MutableList<Tracks>
) {
    data class Row(
        val name: String,
        val price: String
    )
}

@Composable
fun AccordionGroup(modifier: Modifier = Modifier, group: List<AccordionModel>) {
    Column(modifier = modifier) {
        group.forEach {
            Accordion(model = it)
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Accordion(modifier: Modifier = Modifier, model: AccordionModel) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        AccordionHeader(title = model.header, isExpanded = expanded) {
            expanded = !expanded
        }
        AnimatedVisibility(visible = expanded) {
            Surface(
                color = White,
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Gray200),
                tonalElevation = 1.dp,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Column {

                    var counter = 1
                    for (row in model.rows) {
                        AccordionRow(row, counter)
                        Divider(color = Gray200, thickness = 1.dp)
                        counter += 1
                    }
                }
            }
        }
    }
}

@Composable
private fun AccordionHeader(
    title: String = "Header",
    isExpanded: Boolean = false,
    onTapped: () -> Unit = {}
) {
    val degrees = if (isExpanded) 180f else 0f

    Surface(
        color = White,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Gray200),
        tonalElevation = 8.dp,
    ) {
        Row(
            modifier = Modifier
                .clickable { onTapped() }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // style = accordionHeaderStyle,
            Text(title, Modifier.weight(1f), color = Gray600)
            Surface(shape = CircleShape, color = LightBlue900.copy(alpha = 0.6f)) {
                Icon(
                    Icons.Outlined.List,
                    contentDescription = "arrow-down",
                    modifier = Modifier.rotate(degrees),
                    tint = White
                )
            }
        }
    }
}

@Composable
fun AccordionRow(
    model: Tracks,
    index: Int
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(24.dp)
    ) {
        //style = tags

        Text("${index.toString()}.", Modifier.wrapContentWidth().padding(end = 5.dp), color = Gray600)
        Text(model.name.toString(), Modifier.weight(1f), color = MedGray3)
        Surface(color = Green500, shape = RoundedCornerShape(8.dp), tonalElevation = 2.dp) {
            //style = bodyBold
            Text(
                text = model.id.toString(),
                modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
                color = White
            )
        }
    }
}