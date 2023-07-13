package studio.vadim.predanie.presentation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
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
import studio.vadim.predanie.data.AppDatabase
import studio.vadim.predanie.data.app.MainPlaylist


class PlayerService : MediaSessionService(), MediaSession.Callback {

    private var mediaSession: MediaSession? = null
    private lateinit var player: ExoPlayer
    private lateinit var nBuilder: NotificationCompat.Builder
    private lateinit var notificationManager: NotificationManager

    private lateinit var dbInstance: AppDatabase

    var savePlayerPositionJob: Job? = null
    private var playlistPosition: Long = 0

    override fun onCreate() {
        super.onCreate()

        dbInstance = AppDatabase.getInstance(this)

        player = ExoPlayer.Builder(this).build()
            .also { player ->
                //exoPlayer
            }
        val customCallback = CustomMediaSessionCallback()
        mediaSession = MediaSession.Builder(this, player).setCallback(customCallback).build()

        this.setMediaNotificationProvider(object : MediaNotification.Provider {
            override fun createNotification(
                mediaSession: MediaSession,// this is the session we pass to style
                customLayout: ImmutableList<CommandButton>,
                actionFactory: MediaNotification.ActionFactory,
                onNotificationChangedCallback: MediaNotification.Provider.Callback
            ): MediaNotification {
                createNotification(mediaSession)
                // notification should be created before you return here
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

        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                val scope = MainScope()
                val playlistArray = arrayListOf<MediaItem>()

                if (isPlaying)
                    savePlayerPositionJob = scope.launch {

                        while (true) {
                            repeat(player.mediaItemCount) {
                                playlistArray.add(player.getMediaItemAt(it))
                            }
                            saveCurrentPlaylistToDB(playlistArray, player.currentPosition)

                            delay(1000)
                        }
                    }
                if (!isPlaying) {
                    savePlayerPositionJob?.cancel()
                    savePlayerPositionJob = null
                }
            }
        })
    }

    fun saveCurrentPlaylistToDB(currentPlaylist: ArrayList<MediaItem>, currentPosition: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            dbInstance.mainPlaylistDao()
                .insertAll(MainPlaylist(0, "Main", getPlaylistPosition(), "playlist2"))
        }
    }


    private fun getPlaylistPosition(): Long {
        CoroutineScope(Dispatchers.Main).launch {
            playlistPosition = player.currentPosition
        }
        return playlistPosition
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

        // NotificationCompat.Builder here.
        nBuilder = NotificationCompat.Builder(this, "1")
            // Text can be set here
            // but I believe setting MediaMetaData to MediaSession would be enough.
            // I havent tested it deeply yet but did display artist from session
            .setSmallIcon(R.drawable.predanie)
            .setContentTitle("your Content title")
            .setContentText("your content text")
            .setContentIntent(createPendingIntent("https://predanie.ru/player"))
            // set session here
            .setStyle(MediaStyleNotificationHelper.MediaStyle(session))
        // we don build.
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
        override fun onPlaybackResumption(
            session: MediaSession,
            controller: MediaSession.ControllerInfo
        ): ListenableFuture<MediaItemsWithStartPosition> {
            return getLastPlaylist()
        }
    }

    fun getLastPlaylist(): ListenableFuture<MediaItemsWithStartPosition> {
        Log.d("getLastPlaylst", "fires")
        try {
            val mediaItem: MediaItem = MediaItem.Builder()
                .setUri("https://predanie.clients-cdnnow.ru//uploads//ftp//makdonald-dzhordzh-g//dary-mladenca-hrista//03-glava-2.mp3")
                .setMediaId("100")
                .build()

            return Futures.immediateFuture(MediaItemsWithStartPosition(listOf(mediaItem), 0, 0))
        } catch (e: Exception) {
            return Futures.immediateFailedFuture(e)
        }
    }
}