package studio.vadim.predanie.presentation.PlayerService

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
