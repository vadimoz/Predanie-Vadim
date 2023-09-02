package studio.vadim.predanie.presentation

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.exoplayer.offline.Download
import androidx.media3.exoplayer.offline.DownloadManager
import androidx.media3.exoplayer.offline.DownloadRequest
import androidx.media3.exoplayer.offline.DownloadService
import androidx.media3.session.MediaController
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import studio.vadim.predanie.data.room.AppDatabase
import studio.vadim.predanie.data.room.FavoriteAuthors
import studio.vadim.predanie.data.room.FavoriteCompositions
import studio.vadim.predanie.data.room.FavoriteTracks
import studio.vadim.predanie.data.room.HistoryCompositions
import studio.vadim.predanie.data.room.MainPlaylist
import studio.vadim.predanie.data.room.UserPlaylist
import studio.vadim.predanie.domain.models.api.items.DataItem
import studio.vadim.predanie.domain.models.api.items.ResponseAuthorModel
import studio.vadim.predanie.domain.models.api.items.ResponseItemModel
import studio.vadim.predanie.domain.models.api.items.Tracks
import studio.vadim.predanie.domain.usecases.showItems.GetItems
import studio.vadim.predanie.domain.usecases.showLists.GetLists
import studio.vadim.predanie.presentation.downloadService.DownloadManagerSingleton
import studio.vadim.predanie.presentation.downloadService.PredanieDownloadService
import studio.vadim.predanie.presentation.pagination.BlogPagingSource
import studio.vadim.predanie.presentation.pagination.CompositionsPagingSource
import studio.vadim.predanie.presentation.pagination.DownloadsPagingSource
import studio.vadim.predanie.presentation.pagination.FavAuthorsPagingSource
import studio.vadim.predanie.presentation.pagination.FavCompositionsPagingSource
import studio.vadim.predanie.presentation.pagination.FavTracksPagingSource
import studio.vadim.predanie.presentation.pagination.HistoryPagingSource
import studio.vadim.predanie.presentation.pagination.PlaylistsPagingSource
import studio.vadim.predanie.presentation.pagination.SpecialPagingSource
import java.lang.Exception


class MainViewModel(
    private val apiLists: GetLists,
    private val apiItems: GetItems
) : ViewModel() {

    private lateinit var dbInstance: AppDatabase

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

    val special = Pager(PagingConfig(pageSize = 15)) {
        SpecialPagingSource(apiLists, "special")
    }.flow.cachedIn(viewModelScope)

    val blogList = Pager(PagingConfig(pageSize = 5)) {
        BlogPagingSource(apiLists, "blog")
    }.flow.cachedIn(viewModelScope)

    private val _uiState = MutableStateFlow(
        UIState(
            newList, audioPopularList = audioPopularList,
            musicPopularList = musicPopularList, favoritesList = favoritesList,
            special = special, blogList = blogList
        )
    )

    val uiState: StateFlow<UIState> = _uiState.asStateFlow()

    init {
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


    fun getPostInfo(postId: String) {
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(
                    postInfo = apiItems.getPost(postId),
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

    fun cleanPostState() {
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(
                    postInfo = null
                )
            }
        }
    }

    fun searchQueryUpdate(query: String) {
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

    fun prepareCompositionForPlayer(data: DataItem): ArrayList<MediaItem> {
        val mediaItems = arrayListOf<MediaItem>()

        for (part in data.parts) {
            val accordionItems =
                data.tracks.filter { s -> s.parent == part.id.toString() }

            for (it in accordionItems) {
                mediaItems.add(
                    MediaItem.Builder()
                        .setUri(it.url)
                        .setMediaId(it.id.toString())
                        .setTag(it.name)
                        .setMediaMetadata(
                            MediaMetadata.Builder()
                                .setArtworkUri(Uri.parse(data.img_medium))
                                .setTitle(it.name)
                                .setDescription(it.url)
                                .setDisplayTitle(it.name)
                                .setTrackNumber(it.id?.toInt()) //file id
                                .setCompilation(data.id.toString())
                                .build()
                        )
                        .build()
                )
            }
        }

        val separateFiles =
            data.tracks.filter { s -> s.parent == null }

        for (it in separateFiles) {
            mediaItems.add(
                MediaItem.Builder()
                    .setUri(it.url)
                    .setMediaId(it.id.toString())
                    .setMediaMetadata(
                        MediaMetadata.Builder()
                            .setDisplayTitle(it.name)
                            .setArtworkUri(Uri.parse(data.img_big.toString()))
                            .setDescription(it.url)
                            .setCompilation(data.id.toString())
                            .setTrackNumber(it.id?.toInt())
                            .setTitle(it.name)
                            .build()
                    )
                    .build()
            )
        }

        return mediaItems
    }

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    fun initMainPlaylist(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            dbInstance = initDb(context)
            try {
                dbInstance.mainPlaylistDao()
                    .insertAll(
                        MainPlaylist(
                            playlistName = "Main",
                            playlistTime = 0,
                            playlistJson = arrayListOf(MediaItem.fromUri("")),
                            playlistFile = 0,
                        )
                    )
            } catch (e: Throwable) {
                Log.d("initMainPlaylist", e.message.toString())
            }
        }
    }

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    private fun initDb(context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java, "PredanieDB"
        ).fallbackToDestructiveMigration().build()
    }

    fun loadDownloadedCompositions(context: Context) {
        val downloadsList = Pager(PagingConfig(pageSize = 15)) {
            DownloadsPagingSource("downloads", context)
        }.flow.cachedIn(viewModelScope)

        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(
                    downloadsList = downloadsList
                )
            }
        }
    }

    fun loadHistoryCompositions(context: Context) {
        viewModelScope.launch {
            val historyList = Pager(PagingConfig(pageSize = 15)) {
                HistoryPagingSource("history", context)
            }.flow.cachedIn(viewModelScope)

            _uiState.update { currentState ->
                currentState.copy(
                    historyList = historyList
                )
            }
        }
    }

    //Загружаем сразу 3 ленты Отложенных (при старте активити и при обновляем при добавлении элемента в compose)
    fun loadFavorites(context: Context) {
        val favCompositionsList = Pager(PagingConfig(pageSize = 15)) {
            FavCompositionsPagingSource("favCompositions", context)
        }.flow.cachedIn(viewModelScope)

        val favAuthorsList = Pager(PagingConfig(pageSize = 15)) {
            FavAuthorsPagingSource("favAuthors", context)
        }.flow.cachedIn(viewModelScope)

        val favTracksList = Pager(PagingConfig(pageSize = 15)) {
            FavTracksPagingSource("favTracks", context)
        }.flow.cachedIn(viewModelScope)

        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(
                    favCompositionsList = favCompositionsList,
                    favAuthorsList = favAuthorsList,
                    favTracksList = favTracksList
                )
            }
        }
    }

    fun loadPlaylists(context: Context) {

        val playlists = Pager(PagingConfig(pageSize = 15)) {
            PlaylistsPagingSource("playlists", context)
        }.flow.cachedIn(viewModelScope)

        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(
                    playlistsList = playlists,
                )
            }
        }
    }

    //Ставим композицию в Отложенные
    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    fun setCompositionToFavorites(itemId: String, title: String, image: String, context: Context) {
        viewModelScope.launch {
            AppDatabase.getInstance(context).favoriteCompositionsDao().insert(
                FavoriteCompositions(
                    uid = itemId.toInt(),
                    lastPlayTimestamp = System.currentTimeMillis(),
                    title = title,
                    image = image
                )
            )
            Toast.makeText(
                context, "Материал добавлен в Избранное",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    //Ставим Автора в Отложенные
    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    fun setAuthorToFavorites(itemId: String, title: String, image: String, context: Context) {
        viewModelScope.launch {
            AppDatabase.getInstance(context).favoriteAuthorsDao().insert(
                FavoriteAuthors(
                    uid = itemId.toInt(),
                    lastPlayTimestamp = System.currentTimeMillis(),
                    title = title,
                    image = image
                )
            )
            Toast.makeText(
                context, "Автор добавлен в Избранное",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    //Ставим Трек в Отложенные
    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    fun setTrackToFavorites(
        itemId: String,
        title: String,
        compositionid: String,
        uri: String,
        context: Context
    ) {
        viewModelScope.launch {
            AppDatabase.getInstance(context).favoriteTracksDao().insertTrack(
                FavoriteTracks(
                    lastPlayTimestamp = System.currentTimeMillis(),
                    title = title,
                    compositionid = itemId,
                    uri = uri
                )
            )
            Toast.makeText(
                context, "Файл добавлен в Избранное",
                Toast.LENGTH_SHORT
            ).show()
            loadFavorites(context)
        }
    }

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    fun isAuthorFavorite(itemId: String, context: Context): Boolean {
        val author = AppDatabase.getInstance(context).favoriteAuthorsDao().getById(itemId)
        return author != null
    }

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    fun removeAuthorFromFavorite(itemId: String, context: Context) {
        AppDatabase.getInstance(context).favoriteAuthorsDao().deleteById(itemId)
        Toast.makeText(
            context, "Удалено из Избранного",
            Toast.LENGTH_SHORT
        ).show()
        loadFavorites(context)
    }


    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    fun removeAllDownloads(context: Context) {
        DownloadService.sendRemoveAllDownloads(
            context,
            PredanieDownloadService::class.java,
            /* foreground= */ false
        )

        AppDatabase.getInstance(context).downloadedCompositionsDao().removeAll()

        Toast.makeText(
            context, "Все загрузки удалены",
            Toast.LENGTH_SHORT
        ).show()
        loadDownloadedCompositions(context)
    }

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    fun isCompositionFavorite(itemId: String, context: Context): Boolean {
        val composition = AppDatabase.getInstance(context).favoriteCompositionsDao().getById(itemId)
        return composition != null
    }

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    fun removeCompositionFromFavorite(itemId: String, context: Context) {
        AppDatabase.getInstance(context).favoriteCompositionsDao().deleteById(itemId)
        Toast.makeText(
            context, "Удалено из Избранного",
            Toast.LENGTH_SHORT
        ).show()
        loadFavorites(context)
    }

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    fun isTrackFavorite(uri: String, context: Context): Boolean {
        val track = AppDatabase.getInstance(context).favoriteTracksDao().getByUrl(uri)
        return track != null
    }

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    fun removeTrackFromFavorite(uri: String, context: Context) {
        AppDatabase.getInstance(context).favoriteTracksDao().deleteByUri(uri)
        Toast.makeText(
            context, "Удалено из Избранного",
            Toast.LENGTH_SHORT
        ).show()
        loadFavorites(context)
    }

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    fun removePlaylist(name: String, context: Context) {
        AppDatabase.getInstance(context).userPlaylistDao().deleteByName(name)
        Toast.makeText(
            context, "Плейлист удален!",
            Toast.LENGTH_SHORT
        ).show()
        loadPlaylists(context)
    }

    fun isInternetConnected(context: Context): Boolean {
        val cm =
            context.getSystemService(ComponentActivity.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cm.activeNetwork != null && cm.getNetworkCapabilities(cm.activeNetwork) != null
        } else {
            cm.activeNetworkInfo != null && cm.activeNetworkInfo!!.isConnectedOrConnecting
        }
    }

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    fun setCompositionToHistory(itemId: String, title: String, image: String, context: Context) {
        viewModelScope.launch {
            AppDatabase.getInstance(context).historyCompositionsDao().insert(
                HistoryCompositions(
                    uid = itemId.toInt(),
                    lastPlayTimestamp = System.currentTimeMillis(),
                    title = title,
                    image = image
                )
            )
            loadHistoryCompositions(context)
        }
    }

    fun loadSettingsFromStore(context: Context) {
        viewModelScope.launch {
            val settingsPrefs: SharedPreferences = context.getSharedPreferences(
                "settings", Context.MODE_PRIVATE
            )
            _uiState.update { currentState ->
                currentState.copy(

                    //Беру значения, если нет, то ставлю дефолтные
                    goToNext = settingsPrefs.getBoolean("goToNext", true),
                    percentToFileReady = settingsPrefs.getInt("percentToFileReady", 95)
                )
            }
        }
    }

    fun setGoToNextSettings(it: Boolean, context: Context) {
        viewModelScope.launch {
            val settingsPrefs: SharedPreferences = context.getSharedPreferences(
                "settings", Context.MODE_PRIVATE
            )

            settingsPrefs.edit().putBoolean("goToNext", it).apply()

            _uiState.update { currentState ->
                currentState.copy(
                    goToNext = it,
                )
            }
        }
    }

    fun setPercentToFileReady(i: Int, context: Context) {
        viewModelScope.launch {
            val settingsPrefs: SharedPreferences = context.getSharedPreferences(
                "settings", Context.MODE_PRIVATE
            )

            settingsPrefs.edit().putInt("percentToFileReady", i).apply()

            _uiState.update { currentState ->
                currentState.copy(
                    percentToFileReady = i,
                )
            }
        }
    }

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    fun getPlaylistFromDB(name: String, context: Context): MainPlaylist {
        val playlist = AppDatabase.getInstance(context).mainPlaylistDao().findByName(name)
        setPlaylistToState(playlist)
        return AppDatabase.getInstance(context).mainPlaylistDao().findByName(name)
    }

    private fun setPlaylistToState(playlist: MainPlaylist) {
        _uiState.update { currentState ->
            currentState.copy(
                mainPlaylist = playlist,
            )
        }
    }

    fun updateCurrentPlaylistToUi(player: MediaController?) {
        val playlistArray = arrayListOf<MediaItem>()

        if (player != null) {
            repeat(player.mediaItemCount) {
                playlistArray.add(player.getMediaItemAt(it))
            }
        }

        _uiState.update { currentState ->
            currentState.copy(
                mainPlaylist = MainPlaylist(
                    uid = uiState.value.mainPlaylist?.uid ?: 0,
                    playlistName = "Main",
                    playlistTime = uiState.value.mainPlaylist?.playlistTime
                        ?: 0,
                    playlistFile = uiState.value.mainPlaylist?.playlistFile?.toInt() ?: 0,
                    playlistJson = playlistArray
                )
            )
        }
    }

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    fun getAllPlaylists(context: Context): List<UserPlaylist> {
        return AppDatabase.getInstance(context).userPlaylistDao().getAll()
    }

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    fun setCurrentPlaylistToDb(player: MediaController?, context: Context, playlistName: String) {
        val playlistArray = arrayListOf<MediaItem>()

        if (player != null) {
            repeat(player.mediaItemCount) {
                val mediaItem = player.getMediaItemAt(it)

                //Ставим uri через другое поле, т.к. есть баг, в котором не передается uri через getMediaItemAt
                val item =
                    MediaItem.Builder()
                        .setUri(mediaItem.mediaMetadata.description.toString())
                        .setMediaId(mediaItem.mediaId)
                        .setTag(mediaItem.mediaMetadata.title)
                        .setMediaMetadata(
                            MediaMetadata.Builder()
                                .setArtworkUri(Uri.parse(mediaItem.mediaMetadata.artworkUri.toString()))
                                .setTitle(mediaItem.mediaMetadata.title)
                                .setDescription(mediaItem.mediaMetadata.description.toString())
                                .setDisplayTitle(mediaItem.mediaMetadata.title)
                                .setTrackNumber(mediaItem.mediaId.toInt()) //file id
                                .setCompilation((mediaItem.mediaId))
                                .build()
                        )
                        .build()

                playlistArray.add(item)
            }
        }

        AppDatabase.getInstance(context).userPlaylistDao()
            .insertPlaylist(UserPlaylist(playlistName = playlistName, playlistJson = playlistArray))
    }

    fun addToQueue(model: Tracks, context: Context) {
        val item =
            MediaItem.Builder()
                .setUri(model.url)
                .setMediaId(model.id.toString())
                .setTag(model.name)
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(model.name)
                        .setDescription(model.url)
                        .setDisplayTitle(model.name)
                        .setTrackNumber(model.id?.toInt()) //file id
                        .setCompilation((model.id))
                        .build()
                )
                .build()

        uiState.value.playerController?.addMediaItem(item)
        updateCurrentPlaylistToUi(uiState.value.playerController)

        Toast.makeText(
            context, "Файл добавлен в очередь воспроизведения",
            Toast.LENGTH_SHORT
        ).show()
    }

    fun cleanQueue(context: Context) {
        uiState.value.playerController?.removeMediaItems(0, 100000)
        updateCurrentPlaylistToUi(uiState.value.playerController)

        Toast.makeText(
            context, "Очередь воспроизведения очищена!",
            Toast.LENGTH_SHORT
        ).show()
    }

    fun togglePlayer() {
        _uiState.update { currentState ->
            currentState.copy(
                isPlayerVisible = !uiState.value.isPlayerVisible,
            )
        }
    }

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    fun downloadAll(playerList: ArrayList<MediaItem>, itemId: String, context: Context) {
        playerList.forEach {
            val downloadRequest = DownloadRequest
                .Builder(
                    "${itemId}_${it.mediaMetadata.description.toString()}",
                    Uri.parse(it.mediaMetadata.description.toString())
                )
                .build()

            DownloadService.sendAddDownload(
                context,
                PredanieDownloadService::class.java,
                downloadRequest,
                /* foreground = */ false
            )
        }

        val dm = DownloadManagerSingleton.getInstance(context)

        Toast.makeText(
            context, "Файлы скачиваются...",
            Toast.LENGTH_SHORT
        ).show()
    }
}