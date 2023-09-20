package fund.predanie.medialib.presentation.downloadService

import android.content.Context
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.cache.NoOpCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import java.io.File

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
class PlayerCacheSingleton {
    companion object {
        private var INSTANCE: SimpleCache? = null

        fun  getInstance(context: Context): SimpleCache {
            if (INSTANCE == null) {
                val databaseProvider = StandaloneDatabaseProvider(context)
                val downloadDirectory = File(getDownloadDirectory(context), "DownloadPredanie")
                INSTANCE = SimpleCache(downloadDirectory, NoOpCacheEvictor(), databaseProvider)
            }
            return INSTANCE!!
        }

        private fun getDownloadDirectory(context: Context): File? {
            var downloadDirectory = context.getExternalFilesDir(null)
            if (downloadDirectory == null) {
                downloadDirectory = context.filesDir
            }
            return downloadDirectory
        }
    }
}