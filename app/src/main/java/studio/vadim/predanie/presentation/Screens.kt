package studio.vadim.predanie.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun MainScreen(name: String, onClick: () -> Unit) {

    Column() {
        Text(
            text = "Hello $name!"
        )

        Button(onClick = {
            onClick()
        }) {
            Text(text = "Next Screen")
        }
    }
}

@Composable
fun ListRow(model: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
    ) {
        Text(text = model.toString())
    }
}


@Composable
fun ItemScreen(name: String, mainViewModel: MainViewModel, onClick: () -> Unit) {
    Column() {
        Text(
            text = "Hello $name!"
        )

        Button(onClick = {
            onClick()
        }) {
            Text(text = "Next Screen")
        }


        LazyColumn(){
            mainViewModel.newList.value?.count?.let {
                items(it) { model ->
                    ListRow(model = mainViewModel.newList.value.toString())
                }
            }
        }
    }
}