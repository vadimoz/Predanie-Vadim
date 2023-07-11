package studio.vadim.predanie.presentation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.net.toUri
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.CommandButton
import androidx.media3.session.MediaNotification
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.session.MediaStyleNotificationHelper
import com.google.common.collect.ImmutableList
import studio.vadim.predanie.MainActivity
import studio.vadim.predanie.R

class PlayerService : MediaSessionService(), MediaSession.Callback {

    private var mediaSession: MediaSession? = null
    private lateinit var exoPlayer: ExoPlayer
    private lateinit var nBuilder: NotificationCompat.Builder

    override fun onCreate() {
        super.onCreate()

        exoPlayer = ExoPlayer.Builder(this).build()
            .also { exoPlayer ->
                //exoPlayer
            }

        mediaSession = MediaSession.Builder(this, exoPlayer).build()

        this.setMediaNotificationProvider(object : MediaNotification.Provider{
            override fun createNotification(
                mediaSession: MediaSession,// this is the session we pass to style
                customLayout: ImmutableList<CommandButton>,
                actionFactory: MediaNotification.ActionFactory,
                onNotificationChangedCallback: MediaNotification.Provider.Callback
            ): MediaNotification {
                createNotification(mediaSession)
                // notification should be created before you return here
                return MediaNotification(1,nBuilder.build())
            }

            override fun handleCustomCommand(
                session: MediaSession,
                action: String,
                extras: Bundle
            ): Boolean {
                TODO("Not yet implemented")
            }
        })
    }

    fun  createNotification(session: MediaSession) {
        val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(NotificationChannel("1","Плеер", NotificationManager.IMPORTANCE_LOW))

        // NotificationCompat.Builder here.
        nBuilder = NotificationCompat.Builder(this,"1")
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
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession?
            = mediaSession

    private fun createPendingIntent(deepLink: String): PendingIntent {
        val startActivityIntent = Intent(Intent.ACTION_VIEW, deepLink.toUri(),
            this, MainActivity::class.java)

        startActivityIntent.flags = FLAG_ACTIVITY_CLEAR_TASK

        val pendingIntent = PendingIntent.getActivity(this, 0, startActivityIntent, PendingIntent.FLAG_IMMUTABLE)
        return pendingIntent!!
    }
}