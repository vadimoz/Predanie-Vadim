package studio.vadim.predanie.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import studio.vadim.predanie.domain.usecases.showLists.PredefinedItemsToList

class MainViewModel(private val predefinedLists: PredefinedItemsToList) : ViewModel() {

    fun start(){
        viewModelScope.launch {
            val i = predefinedLists.getListNew("music")
            Log.d("start", i.compositions[1].toString())
        }
    }
}