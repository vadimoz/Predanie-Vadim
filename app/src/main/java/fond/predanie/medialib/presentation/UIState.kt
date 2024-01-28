package fund.predanie.medialib.presentation

import androidx.media3.session.MediaController
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import fund.predanie.medialib.data.room.DownloadedCompositions
import fund.predanie.medialib.data.room.FavoriteAuthors
import fund.predanie.medialib.data.room.FavoriteCompositions
import fund.predanie.medialib.data.room.FavoriteTracks
import fund.predanie.medialib.data.room.HistoryCompositions
import fund.predanie.medialib.data.room.MainPlaylist
import fund.predanie.medialib.data.room.UserPlaylist
import fund.predanie.medialib.domain.models.api.items.ResponseAuthorModel
import fund.predanie.medialib.domain.models.api.items.ResponseItemModel
import fund.predanie.medialib.domain.models.api.items.ResponsePostModel
import fund.predanie.medialib.domain.models.api.lists.Compositions
import fund.predanie.medialib.domain.models.api.lists.ResponceBlogListModel
import fund.predanie.medialib.domain.models.api.lists.ResponseCatalogModel
import fund.predanie.medialib.domain.models.api.lists.ResponseGlobalSearchListModel
import fund.predanie.medialib.domain.models.api.lists.VideoData

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
    val playlistsList: Flow<PagingData<UserPlaylist>>? = null,
    val favAuthorsList: Flow<PagingData<FavoriteAuthors>>? = null,
    val favCompositionsList: Flow<PagingData<FavoriteCompositions>>? = null,
    val special: Flow<PagingData<VideoData>>? = null,
    var mainPlaylist: MainPlaylist? = null,
    //settings
    var goToNext: Boolean = true,
    var isPlayerVisible: Boolean = false,
    var percentToFileReady: Int = 95
)
