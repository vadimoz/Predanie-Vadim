package studio.vadim.predanie.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import studio.vadim.predanie.data.ApiRepositoryImpl
import studio.vadim.predanie.domain.usecases.ShowPopularList
import studio.vadim.predanie.presentation.theme.PredanieTheme

class MainActivity : ComponentActivity() {
    protected val scope = CoroutineScope(Dispatchers.Main)
    fun executeCase(spl: ShowPopularList){
        scope.launch {
            spl.execute()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val api = ApiRepositoryImpl()

        //Будет в ViewModel
        val spl = ShowPopularList(api)

        executeCase(spl)

        setContent {
            PredanieTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PredanieTheme {
        Greeting("Android")
    }
}