package studio.vadim.predanie.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import studio.vadim.predanie.domain.usecases.NewItemsToList
import studio.vadim.predanie.domain.usecases.PopularItemsToList

class MainViewModel(private val popularList: PopularItemsToList,
                    private val newList: NewItemsToList) : ViewModel() {

    fun start(){
        viewModelScope.launch {
            val i = newList.execute()
            Log.d("start", i.compositions[3].toString())
        }
    }
}