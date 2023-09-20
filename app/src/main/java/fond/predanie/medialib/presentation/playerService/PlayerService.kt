package fund.predanie.medialib.presentation.playerService

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.session.CommandButton
import androidx.media3.session.MediaNotification
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSession.MediaItemsWithStartPosition
import androidx.media3.session.MediaSessionService
import androidx.media3.session.MediaStyleNotificationHelper
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import fund.predanie.medialib.MainActivity
import fund.predanie.medialib.R
import fund.predanie.medialib.data.room.AppDatabase
import fund.predanie.medialib.data.room.FilePosition
import fund.predanie.medialib.data.room.MainPlaylist
import fund.predanie.medialib.presentation.MediaInfo
import fund.predanie.medialib.presentation.downloadService.PlayerCacheSingleton
import java.io.File


class PlayerService : MediaSessionService(), MediaSession.Callback {

    private var mediaSession: MediaSession? = null
    private lateinit var player: ExoPlayer
    private lateinit var nBuilder: NotificationCompat.Builder
    private lateinit var notificationManager: NotificationManager

    private lateinit var dbInstance: AppDatabase
    private lateinit var currentPlaylistFromDB: MainPlaylist

    private var mediaInfo: MediaInfo = MediaInfo("", "", 0, 0)

    var savePlayerPositionJob: Job? = null
    var savePlayerPlaylistJob: Job? = null
    private var playlistPosition: Long = 0
    private var playlistIndex: Int = 0
    private var currentPositionByFileId: String = ""

    private lateinit var settingsPrefs: SharedPreferences

    private fun getDownloadDirectory(context: Context): File? {
        var downloadDirectory = context.getExternalFilesDir(null)
        if (downloadDirectory == null) {
            downloadDirectory = context.filesDir
        }
        return downloadDirectory
    }

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    override fun onCreate() {
        super.onCreate()

        settingsPrefs = this.getSharedPreferences(
            "settings", Context.MODE_PRIVATE
        )

        this.setMediaNotificationProvider(object : MediaNotification.Provider {
            override fun createNotification(
                mediaSession: MediaSession,
                customLayout: ImmutableList<CommandButton>,
                actionFactory: MediaNotification.ActionFactory,
                onNotificationChangedCallback: MediaNotification.Provider.Callback
            ): MediaNotification {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                    createNotification(mediaSession)
                }
                else  {
                    createNotificationOldAndroid(mediaSession)
                }

                return MediaNotification(1, nBuilder.build())
            }

            override fun handleCustomCommand(
                session: MediaSession,
                action: String,
                extras: Bundle
            ): Boolean {
                TODO("Not yet implemented")
            }
        })

        dbInstance = AppDatabase.getInstance(this)

        val downloadCache = PlayerCacheSingleton.getInstance(this)
        val dataSourceFactory = DefaultHttpDataSource.Factory()

        val cacheDataSourceFactory: DataSource.Factory =
            CacheDataSource.Factory()
                .setCache(downloadCache)
                .setUpstreamDataSourceFactory(dataSourceFactory)
                .setCacheWriteDataSinkFactory(null) // Disable writing.


        player = ExoPlayer.Builder(this)
            .setMediaSourceFactory(
                DefaultMediaSourceFactory(this).setDataSourceFactory(cacheDataSourceFactory)
            ).build()
            .also { player ->
                //exoPlayer settings
            }

        val customCallback = CustomMediaSessionCallback()
        mediaSession = MediaSession.Builder(this, player).setCallback(customCallback).build()

        player.addListener(object : Player.Listener {
            override fun onTimelineChanged(timeline: Timeline, reason: Int) {
                val playlistArray = arrayListOf<MediaItem>()

                repeat(player.mediaItemCount) {
                    playlistArray.add(player.getMediaItemAt(it))
                }

                saveCurrentPlaylistToDB(playlistArray) //store main playlist
                super.onTimelineChanged(timeline, reason)
            }

            override fun onPlaylistMetadataChanged(mediaMetadata: MediaMetadata) {
                Log.d("Playlist changer", "Playlist changer")
                super.onPlaylistMetadataChanged(mediaMetadata)
            }

            //Срабатывает при открытии нового файла - ставим тут последнюю позицию
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                Log.d("Transition reason", reason.toString())
                if (mediaItem != null) {
                    val isFinished = isFinishedByFileId(
                        mediaItem.mediaId,
                        mediaItem.mediaMetadata.compilation.toString()
                    )

                    //Будем пропускать файлы, которые уже прослушаны
                    if (isFinished == true && settingsPrefs.getBoolean("goToNext", true)) {
                        player.seekToNextMediaItem()
                    }
                }

                dbInstance.filePositionDao()
                    .getPositionByFileIdAndCompositionId(
                        player.mediaMetadata.trackNumber.toString(),
                        player.mediaMetadata.compilation.toString()
                    )
                    ?.let { player.seekTo(it.position) }
                //super.onMediaItemTransition(mediaItem, reason)
            }

            override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {
                super.onPlaybackParametersChanged(playbackParameters)
            }

            //Сохраняем текущий плейлист
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                val scope = MainScope()
                val scopePlaylist = MainScope()
                val playlistArray = arrayListOf<MediaItem>()

                CoroutineScope(Dispatchers.IO).launch {
                    dbInstance.mainPlaylistDao()
                        .updateCurrentTimers(getPlaylistPosition(), getPlaylistItemIndex(), 1)
                }

                repeat(player.mediaItemCount) {
                    playlistArray.add(player.getMediaItemAt(it))
                }
                saveCurrentPlaylistToDB(playlistArray)

                if (isPlaying) {
                    savePlayerPlaylistJob = scopePlaylist.launch {
                        while (true) {
                            saveCurrentPlaylistToDB(playlistArray) //store main playlist
                            delay(60000)
                        }
                    }

                    savePlayerPositionJob = scope.launch {
                        while (true) {
                            saveCurrentPositionToPlaylistDB(1) //store main playlist
                            saveCurrentPositionOfMediaFileToDB()
                            delay(1000)
                        }
                    }
                }
                if (!isPlaying) {
                    savePlayerPositionJob?.cancel()
                    savePlayerPositionJob = null

                    savePlayerPlaylistJob?.cancel()
                    savePlayerPlaylistJob = null
                }
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotification(session: MediaSession) {
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(
            NotificationChannel(
                "1",
                "Плеер",
                NotificationManager.IMPORTANCE_LOW
            )
        )

        nBuilder = NotificationCompat.Builder(this, "1")
            .setSmallIcon(R.drawable.predanie)
            .setContentTitle("")
            .setContentText("")
            .setContentIntent(createPendingIntent("https://predanie.ru/player"))
            .setStyle(MediaStyleNotificationHelper.MediaStyle(session))
    }

    fun saveCurrentPositionToPlaylistDB(playlistId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            dbInstance.mainPlaylistDao()
                .updateCurrentTimers(getPlaylistPosition(), getPlaylistItemIndex(), playlistId)
        }
    }

    fun saveCurrentPositionOfMediaFileToDB() {
        //Использую fileid, значение в ms и completed как знак завершенности
        CoroutineScope(Dispatchers.IO).launch {
            val mInfo = getMediaInfo()
            var isFinished = false
            var position = mInfo.currentPlaylistPosition

            val percent = settingsPrefs.getInt("percentToFileReady", 95).toFloat() / 100

            if (mInfo.currentPlaylistPosition > (mInfo.currentMediaItemDuration * percent)) {
                isFinished = true
                position = 0
            }
            dbInstance.filePositionDao()
                .insertOrUpdate(
                    FilePosition(
                        fileid = mInfo.currentMediaItemPredanieId,
                        position = position,
                        compositionid = mInfo.currentMediaItemCompositionId,
                        lastPlayTimestamp = System.currentTimeMillis(),
                        finished = isFinished,
                        filelength = mInfo.currentMediaItemDuration
                    )
                )
        }
    }

    fun saveCurrentPlaylistToDB(playlist: ArrayList<MediaItem>) {
        CoroutineScope(Dispatchers.IO).launch {
            dbInstance.mainPlaylistDao()
                .updateCurrentPlaylist(playlist, 1)
        }
    }

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    fun getCurrentPlaylistFromDB(): MainPlaylist {
        currentPlaylistFromDB = dbInstance.mainPlaylistDao().findByName("Main")
        return currentPlaylistFromDB
    }

    private fun getMediaInfo(): MediaInfo {
        CoroutineScope(Dispatchers.Main).launch {
            mediaInfo = MediaInfo(
                currentPlaylistPosition = player.currentPosition,
                currentMediaItemPredanieId = player.mediaMetadata.trackNumber.toString(),
                currentMediaItemCompositionId = player.mediaMetadata.compilation.toString(),
                currentMediaItemDuration = player.duration
            )
        }
        return mediaInfo
    }

    private fun getPlaylistPosition(): Long {
        CoroutineScope(Dispatchers.Main).launch {
            playlistPosition = player.currentPosition
        }
        return playlistPosition
    }

    private fun getPlaylistItemIndex(): Int {
        CoroutineScope(Dispatchers.Main).launch {
            playlistIndex = player.currentMediaItemIndex
        }
        return playlistIndex
    }

    override fun onDestroy() {
        mediaSession?.player?.release()
        mediaSession?.release()
        mediaSession = null
        super.onDestroy()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? =
        mediaSession

    private fun createPendingIntent(deepLink: String): PendingIntent {
        val startActivityIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        startActivityIntent.putExtra("player", "true")

        val rootIntent =
            applicationContext.packageManager.getLaunchIntentForPackage(applicationContext.packageName)
        val nextIntent = Intent(applicationContext, MainActivity::class.java)

        return PendingIntent.getActivities(
            applicationContext,
            0,
            arrayOf(startActivityIntent),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    @UnstableApi
    private inner class CustomMediaSessionCallback : MediaSession.Callback {
        //Восстанавливаем позицию при возобновлении прослушивания произведения по главной кнопке
        override fun onSetMediaItems(
            mediaSession: MediaSession,
            controller: MediaSession.ControllerInfo,
            mediaItems: MutableList<MediaItem>,
            startIndex: Int,
            startPositionMs: Long
        ): ListenableFuture<MediaItemsWithStartPosition> {
            var index = 0
            if (startIndex >= 0) {
                index = startIndex
            }
            var position = getPositionByFileId(
                mediaItems[index].mediaId,
                mediaItems[index].mediaMetadata.compilation.toString()
            )

            if (position == "null") {
                position = "0"
            }

            return Futures.immediateFuture(
                MediaItemsWithStartPosition(
                    mediaItems,
                    index,
                    position.toLong()
                )
            )
        }

        //Событие на восстановление плейлиста (клик по нотификейшену)
        /*Returns the last recent playlist of the player with which the player should be prepared when
        playback resumption from a media button receiver or the System UI notification is requested.*/
        override fun onPlaybackResumption(
            session: MediaSession,
            controller: MediaSession.ControllerInfo
        ): ListenableFuture<MediaItemsWithStartPosition> {
            return getLastPlaylistFuture()
        }
    }

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    fun getPositionByFileId(fileid: String, compositionid: String): String {
        currentPositionByFileId =
            dbInstance.filePositionDao()
                .getPositionByFileIdAndCompositionId(fileid, compositionid)?.position.toString()
        return currentPositionByFileId
    }

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    fun isFinishedByFileId(fileid: String, compositionid: String): Boolean? {
        return dbInstance.filePositionDao()
            .getPositionByFileIdAndCompositionId(fileid, compositionid)?.finished
    }

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    fun getLastPlaylistFuture(): ListenableFuture<MediaItemsWithStartPosition> {
        return try {
            //получаем из базы текущий плейлист и данные
            val mediaItems = getCurrentPlaylistFromDB()

            Futures.immediateFuture(
                MediaItemsWithStartPosition(
                    mediaItems.playlistJson,
                    mediaItems.playlistFile,
                    mediaItems.playlistTime
                )
            )
        } catch (e: Exception) {
            return Futures.immediateFailedFuture(e)
        }
    }

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    fun createNotificationOldAndroid(session: MediaSession) {
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationCompat = NotificationCompat.Builder(this)
            .setSmallIcon(R.drawable.predanie)
            .setContentTitle(session.player.mediaMetadata.title)
            .setContentIntent(createClickPendingIntent("https://predanie.ru/player", this))
            .setContentText(session.player.mediaMetadata.writer)
            // set session here
            .setStyle(MediaStyleNotificationHelper.MediaStyle(session))
            .build()
        notificationManager.notify(1, notificationCompat)
        nBuilder = NotificationCompat.Builder(this, "1")
            .setSmallIcon(R.drawable.predanie)
            .setContentTitle("")
            .setContentText("")
            .setContentIntent(createPendingIntent("https://predanie.ru/player"))
            .setStyle(MediaStyleNotificationHelper.MediaStyle(session))
    }

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    @RequiresApi(Build.VERSION_CODES.O)

    private fun createClickPendingIntent(deepLink: String, context: Context): PendingIntent {
        val startActivityIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        startActivityIntent.putExtra("player", "true")
        /*
                val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
                    addNextIntentWithParentStack(startActivityIntent)
                    getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE)
                }

                return resultPendingIntent!!*/

        val rootIntent =
            context.packageManager.getLaunchIntentForPackage(context.packageName)
        val nextIntent = Intent(context, MainActivity::class.java)

        return PendingIntent.getActivities(
            context,
            0,
            arrayOf(startActivityIntent),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
}

