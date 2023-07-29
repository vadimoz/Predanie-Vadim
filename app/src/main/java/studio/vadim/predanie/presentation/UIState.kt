package studio.vadim.predanie.presentation

import androidx.media3.session.MediaController
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import studio.vadim.predanie.data.room.DownloadedCompositions
import studio.vadim.predanie.data.room.HistoryCompositions
import studio.vadim.predanie.domain.models.api.items.ResponseAuthorModel
import studio.vadim.predanie.domain.models.api.items.ResponseItemModel
import studio.vadim.predanie.domain.models.api.lists.Compositions
import studio.vadim.predanie.domain.models.api.lists.ResponseCatalogModel
import studio.vadim.predanie.domain.models.api.lists.ResponseGlobalSearchListModel

data class UIState(
    val newList: Flow<PagingData<Compositions>>,
    val musicPopularList: Flow<PagingData<Compositions>>,
    val audioPopularList: Flow<PagingData<Compositions>>,
    val favoritesList: Flow<PagingData<Compositions>>,
    val downloadsList: Flow<PagingData<DownloadedCompositions>>? = null,
    val catalogList: ResponseCatalogModel = ResponseCatalogModel(),
    var searchString: String = "",
    val searchList: ResponseGlobalSearchListModel = ResponseGlobalSearchListModel(),
    val itemInto: ResponseItemModel? = ResponseItemModel(),
    val authorInto: ResponseAuthorModel = ResponseAuthorModel(),
    var catalogItemsList: Flow<PagingData<Compositions>>? = null,
    var playerController: MediaController? = null,
    val historyList: Flow<PagingData<HistoryCompositions>>? = null,
)
