package studio.vadim.predanie.presentation

import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService

class PlayerService : MediaSessionService(), MediaSession.Callback {

    private var mediaSession: MediaSession? = null
    private lateinit var exoPlayer: ExoPlayer

    override fun onCreate() {
        super.onCreate()
        exoPlayer = ExoPlayer.Builder(this).build()
            .also { exoPlayer ->
                val mediaItem =
                    MediaItem.fromUri("https://storage.googleapis.com/exoplayer-test-media-0/play.mp3")
                exoPlayer.setMediaItem(mediaItem)
            }

        mediaSession = MediaSession.Builder(this, exoPlayer).build()
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

    fun getPlayer(): ExoPlayer {
        return exoPlayer
    }
}