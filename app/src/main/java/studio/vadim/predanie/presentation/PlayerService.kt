package studio.vadim.predanie.presentation

import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSession.MediaItemsWithStartPosition
import androidx.media3.session.MediaSessionService
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import studio.vadim.predanie.data.room.AppDatabase
import studio.vadim.predanie.data.room.MainPlaylist


class PlayerService : MediaSessionService(), MediaSession.Callback {

    private var mediaSession: MediaSession? = null
    private lateinit var player: ExoPlayer
    private lateinit var nBuilder: NotificationCompat.Builder
    private lateinit var notificationManager: NotificationManager

    private lateinit var dbInstance: AppDatabase
    private lateinit var currentPlaylistFromDB: MainPlaylist

    var savePlayerPositionJob: Job? = null
    var savePlayerPlaylistJob: Job? = null
    private var playlistPosition: Long = 0
    private var playlistIndex: Int = 0

    override fun onUpdateNotification(
        session: MediaSession,
        startInForegroundRequired: Boolean
    ) {
        //super.onUpdateNotification(session, startInForegroundRequired)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotification(session, this)
        } else {
            createNotificationOldAndroid(session, this)
        }
    }

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    override fun  onCreate() {
        super.onCreate()

        dbInstance = AppDatabase.getInstance(this)

        player = ExoPlayer.Builder(this).build()
            .also { player ->
                //exoPlayer settings
            }

        val customCallback = CustomMediaSessionCallback()
        mediaSession = MediaSession.Builder(this, player).setCallback(customCallback).build()

        player.addListener(object : Player.Listener {
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


                val mediaItems = dbInstance.mainPlaylistDao().findByName("Main")

                Log.d("mediadd", mediaItems.toString())

                if (isPlaying) {
                    savePlayerPlaylistJob = scopePlaylist.launch {
                        while (true) {
                            saveCurrentPlaylistToDB(playlistArray) //store main playlist
                            delay(60000)
                        }
                    }

                    savePlayerPositionJob = scope.launch {
                        while (true) {
                            saveCurrentPositionToDB() //store main playlist
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

    fun saveCurrentPositionToDB() {
        CoroutineScope(Dispatchers.IO).launch {
            dbInstance.mainPlaylistDao()
                .updateCurrentTimers(getPlaylistPosition(), getPlaylistItemIndex(), 1)
        }
    }

    fun saveCurrentPlaylistToDB(playlist: ArrayList<MediaItem>) {
        CoroutineScope(Dispatchers.IO).launch {
            dbInstance.mainPlaylistDao()
                .updateCurrentPlaylist(playlist, 1)
        }
    }

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    fun  getCurrentPlaylistFromDB(): MainPlaylist {
        CoroutineScope(Dispatchers.IO).launch {
            currentPlaylistFromDB = dbInstance.mainPlaylistDao().findByName("Main")
        }
        return currentPlaylistFromDB
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

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
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

    @UnstableApi
    private inner class CustomMediaSessionCallback : MediaSession.Callback {
        override fun onPlaybackResumption(
            session: MediaSession,
            controller: MediaSession.ControllerInfo
        ): ListenableFuture<MediaItemsWithStartPosition> {
            player.play()
            return getLastPlaylist()
        }
    }

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    fun  getLastPlaylist(): ListenableFuture<MediaItemsWithStartPosition> {
        return try {
            //получаем из базы текущий плейлист и данные
            val mediaItems = getCurrentPlaylistFromDB()

            Futures.immediateFuture(MediaItemsWithStartPosition(mediaItems.playlistJson, mediaItems.playlistFile, mediaItems.playlistTime))
        } catch (e: Exception) {
            return Futures.immediateFailedFuture(e)
        }
    }
}