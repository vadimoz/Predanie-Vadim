package fund.predanie.medialib.data.room

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import fund.predanie.medialib.data.room.models.MediaItemToData

@androidx.media3.common.util.UnstableApi
class Converters {
    @TypeConverter
    fun MediaListToJson(source: ArrayList<MediaItem>): String {
        val mediaItemsForStore = mutableListOf<MediaItemToData>()
        for (it in source) {
            mediaItemsForStore.add(
                MediaItemToData(
                    id = it.mediaId,
                    uri = it.localConfiguration?.uri.toString(),
                    author = it.mediaMetadata.artist.toString(),
                    authorId = it.mediaMetadata.albumArtist.toString(),
                    title = it.mediaMetadata.title.toString(),
                    fileid = it.mediaMetadata.trackNumber.toString(),
                    compositionid = it.mediaMetadata.compilation.toString(),
                    artworkUri = it.mediaMetadata.artworkUri.toString(),
                    urlFromDescription = it.mediaMetadata.description.toString()
                )
            )
        }
        return Gson().toJson(mediaItemsForStore)
    }

    @TypeConverter
    fun jsonToMediaList(value: String): ArrayList<MediaItem> {
        val sType = object : TypeToken<ArrayList<MediaItemToData>>() {}.type
        val list = Gson().fromJson<ArrayList<MediaItemToData>>(value, sType)

        val playlist = ArrayList<MediaItem>()
        for (it in list) {
            playlist.add(
                MediaItem.Builder()
                    .setUri(it.urlFromDescription)
                    .setMediaId(it.id)
                    .setMediaMetadata(
                        MediaMetadata.Builder()
                            .setDisplayTitle(it.title)
                            .setTrackNumber(it.fileid.toIntOrNull())
                            .setCompilation(it.compositionid)
                            .setTitle(it.title)
                            .setAlbumArtist(it.authorId)
                            .setArtist(it.author)
                            .setDescription(it.urlFromDescription)
                            .setArtworkUri(Uri.parse(it.artworkUri))
                            .build()
                    )
                    .build()
            )
        }
        return playlist
    }
}