package studio.vadim.predanie.presentation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
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
import studio.vadim.predanie.MainActivity
import studio.vadim.predanie.R
import studio.vadim.predanie.data.room.AppDatabase
import studio.vadim.predanie.data.room.FilePosition
import studio.vadim.predanie.data.room.MainPlaylist


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
    private var currentMediaItemPredanieId: String = ""
    private var currentMediaItemCompositionId: String = ""
    private var currentMediaItemDuration: Long = 0
    private var currentPositionByFileId: String = ""

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    override fun onCreate() {
        super.onCreate()

        this.setMediaNotificationProvider(object : MediaNotification.Provider {
            override fun createNotification(
                mediaSession: MediaSession,
                customLayout: ImmutableList<CommandButton>,
                actionFactory: MediaNotification.ActionFactory,
                onNotificationChangedCallback: MediaNotification.Provider.Callback
            ): MediaNotification {
                createNotification(mediaSession)
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

        player = ExoPlayer.Builder(this).build()
            .also { player ->
                //exoPlayer settings
            }

        val customCallback = CustomMediaSessionCallback()
        mediaSession = MediaSession.Builder(this, player).setCallback(customCallback).build()

        player.addListener(object : Player.Listener {
            //Срабатывает при открытии нового файла - ставим тут последнюю позицию
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                if (mediaItem != null) {
                    val isFinished = isFinishedByFileId(
                        mediaItem.mediaId,
                        mediaItem.mediaMetadata.compilation.toString()
                    )

                    //Будем пропускать файлы, которые уже прослушаны
                    if (isFinished == true){
                        player.seekToNextMediaItem()
                    }
                }
                dbInstance.filePositionDao()
                    .getPositionByFileId(
                        player.mediaMetadata.trackNumber.toString(),
                        player.mediaMetadata.compilation.toString()
                    )
                    ?.let { player.seekTo(it.position) }
                super.onMediaItemTransition(mediaItem, reason)
            }

            override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {
                super.onPlaybackParametersChanged(playbackParameters)
            }

            override fun onSeekForwardIncrementChanged(seekForwardIncrementMs: Long) {
                val mInfo = getMediaInfo()
                dbInstance.filePositionDao()
                    .insertOrUpdate(
                        FilePosition(
                            fileid = mInfo.currentMediaItemPredanieId,
                            position = mInfo.currentPlaylistPosition,
                            compositionid = mInfo.currentMediaItemCompositionId,
                            lastPlayTimestamp = System.currentTimeMillis()
                        )
                    )

                savePlayerPositionJob = MainScope().launch {
                    saveCurrentPositionToPlaylistDB(1) //store main playlist
                }
                super.onSeekForwardIncrementChanged(seekForwardIncrementMs)
            }

            override fun onSeekBackIncrementChanged(seekBackIncrementMs: Long) {
                val mInfo = getMediaInfo()
                dbInstance.filePositionDao()
                    .insertOrUpdate(
                        FilePosition(
                            fileid = mInfo.currentMediaItemPredanieId,
                            position = mInfo.currentPlaylistPosition,
                            compositionid = mInfo.currentMediaItemCompositionId,
                            lastPlayTimestamp = System.currentTimeMillis()
                        )
                    )
                savePlayerPositionJob = MainScope().launch {
                    saveCurrentPositionToPlaylistDB(1) //store main playlist
                }

                super.onSeekBackIncrementChanged(seekBackIncrementMs)
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
            .setContentTitle("your Content title")
            .setContentText("your content text")
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

            if (mInfo.currentPlaylistPosition > (mInfo.currentMediaItemDuration * 0.98)) {
                isFinished = true
            }
            dbInstance.filePositionDao()
                .insertOrUpdate(
                    FilePosition(
                        fileid = mInfo.currentMediaItemPredanieId,
                        position = mInfo.currentPlaylistPosition,
                        compositionid = mInfo.currentMediaItemCompositionId,
                        lastPlayTimestamp = System.currentTimeMillis(),
                        finished = isFinished
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
        CoroutineScope(Dispatchers.IO).launch {
            currentPlaylistFromDB = dbInstance.mainPlaylistDao().findByName("Main")
        }
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

    private fun getCurrentMediaItemCompositionId(): String {
        CoroutineScope(Dispatchers.Main).launch {
            currentMediaItemCompositionId =
                player.mediaMetadata.compilation.toString() //в это поле записываем id трека из Предания
        }
        return currentMediaItemCompositionId
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("ONDESTROY MSERVICE", "FIRE")

        mediaSession?.player?.release()
        mediaSession?.release()
        mediaSession = null

        notificationManager.cancelAll()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? =
        mediaSession

    private fun createPendingIntent(deepLink: String): PendingIntent {
        val startActivityIntent = Intent(this, MainActivity::class.java).apply {
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
            /*
            Для этого элемента запрашиваем в базе и ставим позицию плееру в возвращаемой
            mediaItems[startIndex].mediaId
            mediaItems[startIndex].mediaMetadata.compilation*/


            /*return super.onSetMediaItems(
                mediaSession,
                controller,
                mediaItems,
                startIndex,
                startPositionMs
            )*/
        }

        //Событие на восстановление плейлиста (клик по нотификейшену)
        /*Returns the last recent playlist of the player with which the player should be prepared when
        playback resumption from a media button receiver or the System UI notification is requested.*/
        override fun onPlaybackResumption(
            session: MediaSession,
            controller: MediaSession.ControllerInfo
        ): ListenableFuture<MediaItemsWithStartPosition> {
            Log.d("onPlaybackResumption", "fired event")
            player.play()
            return getLastPlaylist()
        }

        override fun onConnect(
            session: MediaSession,
            controller: MediaSession.ControllerInfo
        ): MediaSession.ConnectionResult {
            Log.d("onConnect", "onConnect fired")
            return super.onConnect(session, controller)
        }

        override fun onDisconnected(
            session: MediaSession,
            controller: MediaSession.ControllerInfo
        ) {
            Log.d("onDisconnected", "onDisconnected fired")
            super.onDisconnected(session, controller)
        }
    }

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    fun getPositionByFileId(fileid: String, compositionid: String): String {
        currentPositionByFileId =
            dbInstance.filePositionDao()
                .getPositionByFileId(fileid, compositionid)?.position.toString()
        return currentPositionByFileId
    }

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    fun isFinishedByFileId(fileid: String, compositionid: String): Boolean? {
        return dbInstance.filePositionDao()
            .getPositionByFileId(fileid, compositionid)?.finished
    }

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    fun getLastPlaylist(): ListenableFuture<MediaItemsWithStartPosition> {
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
        val notificationCompat = NotificationCompat.Builder(this)
            .setSmallIcon(R.drawable.predanie)
            .setContentTitle(session.player.mediaMetadata.title)
            .setContentIntent(createClickPendingIntent("https://predanie.ru/player", this))
            .setContentText(session.player.mediaMetadata.writer)
            // set session here
            .setStyle(MediaStyleNotificationHelper.MediaStyle(session))
            .build()
        notificationManager.notify(1, notificationCompat)
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

