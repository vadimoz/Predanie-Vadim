package studio.vadim.predanie.presentation

import androidx.media3.session.MediaController
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import studio.vadim.predanie.data.room.DownloadedCompositions
import studio.vadim.predanie.data.room.FavoriteAuthors
import studio.vadim.predanie.data.room.FavoriteCompositions
import studio.vadim.predanie.data.room.FavoriteTracks
import studio.vadim.predanie.data.room.HistoryCompositions
import studio.vadim.predanie.domain.models.api.items.ResponseAuthorModel
import studio.vadim.predanie.domain.models.api.items.ResponseItemModel
import studio.vadim.predanie.domain.models.api.items.ResponsePostModel
import studio.vadim.predanie.domain.models.api.lists.Compositions
import studio.vadim.predanie.domain.models.api.lists.ResponceBlogListModel
import studio.vadim.predanie.domain.models.api.lists.ResponseCatalogModel
import studio.vadim.predanie.domain.models.api.lists.ResponseGlobalSearchListModel
import studio.vadim.predanie.domain.models.api.lists.VideoData

data class UIState(
    val newList: Flow<PagingData<Compositions>>,
    val musicPopularList: Flow<PagingData<Compositions>>,
    val audioPopularList: Flow<PagingData<Compositions>>,
    val favoritesList: Flow<PagingData<Compositions>>,
    val blogList: Flow<PagingData<ResponceBlogListModel>>,
    val downloadsList: Flow<PagingData<DownloadedCompositions>>? = null,
    val catalogList: ResponseCatalogModel = ResponseCatalogModel(),
    var searchString: String = "",
    val searchList: ResponseGlobalSearchListModel = ResponseGlobalSearchListModel(),
    val itemInto: ResponseItemModel? = ResponseItemModel(),
    val postInfo: ResponsePostModel? = null,
    val authorInto: ResponseAuthorModel = ResponseAuthorModel(),
    var catalogItemsList: Flow<PagingData<Compositions>>? = null,
    var playerController: MediaController? = null,
    val historyList: Flow<PagingData<HistoryCompositions>>? = null,
    val favTracksList: Flow<PagingData<FavoriteTracks>>? = null,
    val favAuthorsList: Flow<PagingData<FavoriteAuthors>>? = null,
    val favCompositionsList: Flow<PagingData<FavoriteCompositions>>? = null,
    val special: Flow<PagingData<VideoData>>? = null,
)
