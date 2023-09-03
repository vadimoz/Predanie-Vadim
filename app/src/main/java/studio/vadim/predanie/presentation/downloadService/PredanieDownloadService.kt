package studio.vadim.predanie.presentation.downloadService

import android.app.Notification
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.Util
import androidx.media3.exoplayer.offline.Download
import androidx.media3.exoplayer.offline.DownloadManager
import androidx.media3.exoplayer.offline.DownloadNotificationHelper
import androidx.media3.exoplayer.offline.DownloadService
import androidx.media3.exoplayer.scheduler.PlatformScheduler
import androidx.media3.exoplayer.scheduler.Scheduler
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import studio.vadim.predanie.R
import studio.vadim.predanie.data.api.ApiImpl
import studio.vadim.predanie.data.room.AppDatabase
import studio.vadim.predanie.data.room.DownloadedCompositions
import studio.vadim.predanie.domain.models.api.items.DataItem
import studio.vadim.predanie.domain.usecases.showItems.GetItems
import java.io.File
import java.lang.Exception

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
class PredanieDownloadService : DownloadService(
    2,
    DEFAULT_FOREGROUND_NOTIFICATION_UPDATE_INTERVAL,
    "Downloads",
    R.string.app_name,  /* channelDescriptionResourceId= */
    0
) {
    private val apiItems = GetItems(ApiImpl())

    var downloadNotificationHelper: DownloadNotificationHelper? = null
    val context = this
    private lateinit var dm: DownloadManager

    override fun getDownloadManager(): DownloadManager {
        dm = DownloadManagerSingleton.getInstance(this)
        activateListeners()
        return dm
    }

    //Слушаем события на скачивание и удаление файлов и сохраняем произведение в базу для показа в оффлайн режиме
    //Сохраняем все произведение и его плейлист, даже те файлы, которые не скачаны. То, что не скачано не будет
    //отображаться на уровне представления, чтобы сохранить порядок следования файлов в произведении

    private fun activateListeners() {
        dm.addListener(
            object : DownloadManager.Listener {
                override fun onDownloadChanged(
                    downloadManager: DownloadManager,
                    download: Download,
                    finalException: Exception?
                ) {
                    super.onDownloadChanged(downloadManager, download, finalException)

                    val dbInstance = AppDatabase.getInstance(context)
                    //Добавляем произведение с его плейлистом в базу
                    //Получаю композицию с картинкой
                    val delim = "_"
                    val id = download.request.id.split(delim).toTypedArray()

                    GlobalScope.launch {
                        val compositionInto = apiItems.getItem(id[0].toInt())
                        val mediaItems =
                            compositionInto.data?.let { prepareDownloadedCompositionForPlayer(it) }
                        if (mediaItems != null) {
                            if (mediaItems.isEmpty()) {
                                dbInstance.downloadedCompositionsDao()
                                    .deleteByComposition(id[0].toInt())
                            } else {
                                if (compositionInto.data != null) {
                                    Log.d("iddd", id.toString())
                                    mediaItems.let {
                                        DownloadedCompositions(
                                            uid = compositionInto.data?.id,
                                            title = compositionInto.data?.name.toString(),
                                            playlistJson = it,
                                            image = compositionInto.data?.img_big
                                        )
                                    }.let {
                                        dbInstance.downloadedCompositionsDao().insert(
                                            it
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        )
    }

    //Подготавливаем объект MediaItems для сохранения в db
    //в данном случае исключаем все несохраненные локально файлы
    fun prepareDownloadedCompositionForPlayer(data: DataItem): ArrayList<MediaItem> {
        val mediaItems = arrayListOf<MediaItem>()

        for (part in data.parts) {
            val accordionItems =
                data.tracks.filter { s -> s.parent == part.id.toString() }

            for (it in accordionItems) {

                //Делаем только если файл  есть в загруженных
                if (DownloadManagerSingleton.getInstance(this).downloadIndex.getDownload(
                        "${data.id}_${it.url}"
                    )?.state == 3
                ) {
                    mediaItems.add(
                        MediaItem.Builder()
                            .setUri(it.url)
                            .setMediaId(it.id.toString())
                            .setTag(it.name)
                            .setMediaMetadata(
                                MediaMetadata.Builder()
                                    .setArtworkUri(Uri.parse(data.img_medium))
                                    .setTitle(it.name)
                                    .setDisplayTitle(it.name)
                                    .setDescription(it.url)
                                    .setArtist(data.author_name)//
                                    .setAlbumArtist(data.author_id.toString())//
                                    .setTrackNumber(it.id?.toInt()) //file id
                                    .setCompilation(data.id.toString())
                                    .build()
                            )
                            .build()
                    )
                }
            }
        }

        val separateFiles =
            data.tracks.filter { s -> s.parent == null }

        for (it in separateFiles) {
            //Делаем только если файл  есть в загруженных
            if (DownloadManagerSingleton.getInstance(this).downloadIndex.getDownload(
                    "${data.id}_${it.url}"
                )?.state == 3
            ) {
                mediaItems.add(
                    MediaItem.Builder()
                        .setUri(it.url)
                        .setMediaId(it.id.toString())
                        .setMediaMetadata(
                            MediaMetadata.Builder()
                                .setDisplayTitle(it.name)
                                .setDescription(it.url)
                                .setArtworkUri(Uri.parse(data.img_big.toString()))
                                .setCompilation(data.id.toString())
                                .setArtist(data.author_name)//
                                .setAlbumArtist(data.author_id.toString())//
                                .setTrackNumber(it.id?.toInt())
                                .setTitle(it.name)
                                .build()
                        )
                        .build()
                )
            }
        }

        return mediaItems
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