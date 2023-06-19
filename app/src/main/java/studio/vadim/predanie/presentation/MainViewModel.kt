package studio.vadim.predanie.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import studio.vadim.predanie.domain.usecases.showItems.GetItems
import studio.vadim.predanie.domain.usecases.showLists.GetLists


class MainViewModel(private val apiLists: GetLists,
                    private val apiItems: GetItems) : ViewModel() {

    private val PAGE_SIZE = 5

    private val _uiState = MutableStateFlow(UIState())
    val uiState: StateFlow<UIState> = _uiState.asStateFlow()

    val compositionsPager = Pager(PagingConfig(pageSize = PAGE_SIZE)) {
        CompositionsPagingSource(apiLists)
    }.flow.cachedIn(viewModelScope)

    fun init() {
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