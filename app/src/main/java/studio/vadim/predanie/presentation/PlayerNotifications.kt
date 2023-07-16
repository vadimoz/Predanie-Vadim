package studio.vadim.predanie.presentation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaStyleNotificationHelper
import studio.vadim.predanie.MainActivity
import studio.vadim.predanie.R

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
fun  createNotificationOldAndroid(session: MediaSession, context: Context) {
    val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    val notificationCompat = NotificationCompat.Builder(context)
        .setSmallIcon(R.drawable.predanie)
        .setContentTitle(session.player.mediaMetadata.title)
        .setContentIntent(createClickPendingIntent("https://predanie.ru/player", context))
        .setContentText(session.player.mediaMetadata.writer)
        // set session here
        .setStyle(MediaStyleNotificationHelper.MediaStyle(session))
        .build()
    notificationManager.notify(1,notificationCompat)
}

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@RequiresApi(Build.VERSION_CODES.O)
fun  createNotification(session: MediaSession, context: Context) {
    val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.createNotificationChannel(NotificationChannel("1","Плеер Предание.ру", NotificationManager.IMPORTANCE_HIGH))

    val notificationCompat = NotificationCompat.Builder(context,"1")
        .setSmallIcon(R.drawable.predanie)
        .setContentTitle(session.player.mediaMetadata.title)
        .setContentIntent(createClickPendingIntent("https://predanie.ru/player", context))
        .setContentText(session.player.mediaMetadata.writer)
        .setStyle(MediaStyleNotificationHelper.MediaStyle(session))
        .build()
    notificationManager.notify(1,notificationCompat)
}

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