package studio.vadim.predanie.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import studio.vadim.predanie.domain.usecases.showItems.GetItems
import studio.vadim.predanie.domain.usecases.showLists.GetLists

class MainViewModel(private val apiLists: GetLists,
                    private val apiItems: GetItems) : ViewModel() {

    fun start(){
        viewModelScope.launch {
            val i = apiLists.getAuthorsLetterList("Б")
            Log.d("start", i.toString())
        }
    }
}