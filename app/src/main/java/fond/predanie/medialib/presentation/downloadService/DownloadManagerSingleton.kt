package fund.predanie.medialib.presentation.downloadService

import android.content.Context
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.offline.DownloadManager
import java.util.concurrent.Executor

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
class DownloadManagerSingleton {
    companion object {
        private var INSTANCE: DownloadManager? = null

        fun  getInstance(context: Context): DownloadManager {
            if (INSTANCE == null) {
                val databaseProvider = StandaloneDatabaseProvider(context)
                val downloadCache = PlayerCacheSingleton.getInstance(context)
                val dataSourceFactory = DefaultHttpDataSource.Factory()
                val downloadExecutor = Executor(Runnable::run)

                INSTANCE = DownloadManager(
                    context,
                    databaseProvider,
                    downloadCache,
                    dataSourceFactory,
                    downloadExecutor
                )
            }
            return INSTANCE!!
        }
    }
}