package studio.vadim.predanie.presentation.downloadService

import android.app.Notification
import android.content.Context
import android.util.Log
import androidx.media3.common.util.Util
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.offline.Download
import androidx.media3.exoplayer.offline.DownloadManager
import androidx.media3.exoplayer.offline.DownloadNotificationHelper
import androidx.media3.exoplayer.offline.DownloadService
import androidx.media3.exoplayer.scheduler.PlatformScheduler
import androidx.media3.exoplayer.scheduler.Scheduler
import studio.vadim.predanie.R
import java.io.File
import java.lang.Exception
import java.util.concurrent.Executor

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
class PredanieDownloadService : DownloadService(
    2,
    DEFAULT_FOREGROUND_NOTIFICATION_UPDATE_INTERVAL,
    "Downloads",
    R.string.app_name,  /* channelDescriptionResourceId= */
    0
) {
    var downloadNotificationHelper: DownloadNotificationHelper? = null
    private lateinit var dm: DownloadManager

    override fun getDownloadManager(): DownloadManager {
        dm = DownloadManagerSingleton.getInstance(this)
        activateListeners()
        return dm
    }

    private fun activateListeners() {
        dm.addListener(
            object : DownloadManager.Listener {
                override fun onDownloadChanged(
                    downloadManager: DownloadManager,
                    download: Download,
                    finalException: Exception?
                ) {
                    super.onDownloadChanged(downloadManager, download, finalException)
                    if (download.state == 3) {
                        Log.d("downloadManager", download.request.id.toString())
                    }
                }
            }
        )
    }

    override fun getScheduler(): Scheduler? {
        return if (Util.SDK_INT >= 21) PlatformScheduler(this, 1) else null
    }

    override fun getForegroundNotification(
        downloads: MutableList<Download>,
        notMetRequirements: Int
    ): Notification {
        val helper = DownloadNotificationHelper(this, "Downloads")

        return helper.buildProgressNotification(
            /* context = */ this,
            R.drawable.predanie,
            /* contentIntent = */ null,
            /* message = */ "Скачивание...",
            downloads,
            notMetRequirements
        )
    }

    private fun getDownloadDirectory(context: Context): File? {
        var downloadDirectory = context.getExternalFilesDir(null)
        if (downloadDirectory == null) {
            downloadDirectory = context.filesDir
        }
        return downloadDirectory
    }
}