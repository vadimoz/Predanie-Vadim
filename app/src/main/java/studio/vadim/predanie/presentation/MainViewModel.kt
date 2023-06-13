package studio.vadim.predanie.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import studio.vadim.predanie.domain.models.api.lists.ResponseItemsListModel
import studio.vadim.predanie.domain.usecases.showItems.GetItems
import studio.vadim.predanie.domain.usecases.showLists.GetLists

class MainViewModel(private val apiLists: GetLists,
                    private val apiItems: GetItems) : ViewModel() {

    private var _newList = MutableLiveData<ResponseItemsListModel>()
    var newList: LiveData<ResponseItemsListModel> = _newList

    fun init() {
        viewModelScope.launch {
            _newList.value = apiLists.getListNew("audio,music")
        }
    }
}