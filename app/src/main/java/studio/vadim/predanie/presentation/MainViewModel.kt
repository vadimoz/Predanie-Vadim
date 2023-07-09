package studio.vadim.predanie.presentation

import android.text.TextUtils
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.session.MediaController
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import studio.vadim.predanie.domain.models.api.items.ResponseAuthorModel
import studio.vadim.predanie.domain.models.api.items.ResponseItemModel
import studio.vadim.predanie.domain.usecases.showItems.GetItems
import studio.vadim.predanie.domain.usecases.showLists.GetLists


class MainViewModel(private val apiLists: GetLists,
                    private val apiItems: GetItems) : ViewModel() {

    val newList = Pager(PagingConfig(pageSize = 15)) {
        CompositionsPagingSource(apiLists, type = "new", 0)
    }.flow.cachedIn(viewModelScope)

    val audioPopularList = Pager(PagingConfig(pageSize = 15)) {
        CompositionsPagingSource(apiLists, "audioPopular", 0)
    }.flow.cachedIn(viewModelScope)

    val musicPopularList = Pager(PagingConfig(pageSize = 15)) {
        CompositionsPagingSource(apiLists, "musicPopular", 0)
    }.flow.cachedIn(viewModelScope)

    val favoritesList = Pager(PagingConfig(pageSize = 15)) {
        CompositionsPagingSource(apiLists, "favorites", 0)
    }.flow.cachedIn(viewModelScope)


    private val _uiState = MutableStateFlow(UIState(newList, audioPopularList = audioPopularList,
    musicPopularList = musicPopularList, favoritesList = favoritesList))

    val uiState: StateFlow<UIState> = _uiState.asStateFlow()

    init{
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(
                    catalogList = apiLists.getCatalogList(),
                )
            }
        }
    }

    fun getItemInfo(id: Int) {
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(
                    itemInto = apiItems.getItem(id),
                )
            }
        }
    }
    fun getAuthorInfo(id: Int) {
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(
                    authorInto = apiItems.getAuthor(id),
                )
            }
        }
    }
    fun cleanAuthorState() {
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(
                    authorInto = ResponseAuthorModel(),
                )
            }
        }
    }
    fun cleanItemState() {
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(
                    itemInto = ResponseItemModel(),
                )
            }
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
    fun getCatalogItemsList(catalogId: String?) {
        if (catalogId != null) {
            viewModelScope.launch {
                val list = Pager(PagingConfig(pageSize = 15)) {
                            CompositionsPagingSource(
                                apiLists,
                                "catalogItems",
                                catalogId = catalogId.toInt()
                            )
                        }.flow.cachedIn(viewModelScope)

                _uiState.value.catalogItemsList = list
            }
        }
    }

    fun setSearchQuery(query: String?) {

        if (!TextUtils.isEmpty(query)) {
            viewModelScope.launch {
                _uiState.update { currentState ->
                    currentState.copy(
                        searchList = apiLists.getGlobalSearchList(query.toString())
                    )
                }
            }
        }
    }

    fun setPlayerInstance(playerService: MediaController) {
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(
                    playerController = playerService
                )
            }
        }
    }
}