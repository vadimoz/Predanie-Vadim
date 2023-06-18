package studio.vadim.predanie.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import studio.vadim.predanie.domain.usecases.showItems.GetItems
import studio.vadim.predanie.domain.usecases.showLists.GetLists

class MainViewModel(private val apiLists: GetLists,
                    private val apiItems: GetItems) : ViewModel() {

    private val _uiState = MutableStateFlow(UIState())
    val uiState: StateFlow<UIState> = _uiState.asStateFlow()

    fun init() {
        viewModelScope.launch {
            _uiState.value = UIState(newList = apiLists.getListNew("audio"),
                musicPopularList = apiLists.getListPopular("music"),
                audioPopularList = apiLists.getListPopular("audio"),
                favoritesList = apiLists.getListFavorites("audio,music"),
                catalogList = apiLists.getCatalogList(),
                searchList = apiLists.getGlobalSearchList(_uiState.value.searchString))
        }
    }

    fun searchQueryUpdate(query: String){
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(
                    searchString = query
                )
            }

            _uiState.update { currentState ->
                currentState.copy(
                    searchList = apiLists.getGlobalSearchList(_uiState.value.searchString)
                )
            }
        }
    }
}