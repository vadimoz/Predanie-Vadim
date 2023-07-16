package studio.vadim.predanie.data.room

import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import studio.vadim.predanie.data.room.models.MediaItemToData

@androidx.media3.common.util.UnstableApi
class Converters {
    @TypeConverter
    fun MediaListToJson(source: ArrayList<MediaItem>): String {
        val mediaItemsForStore = mutableListOf<MediaItemToData>()
        for (it in source) {
            Log.d("mediaItemsForStore",source.toString())
            mediaItemsForStore.add(MediaItemToData(id = it.mediaId,
                uri = it.playbackProperties?.uri.toString(),
                author = it.mediaMetadata.artist.toString(),
                title = it.mediaMetadata.albumTitle.toString()))
        }
        return Gson().toJson(mediaItemsForStore)
    }
   @TypeConverter
    fun jsonToMediaList(value: String): ArrayList<MediaItem> {
       val sType = object : TypeToken<ArrayList<MediaItemToData>>() { }.type
       val list = Gson().fromJson<ArrayList<MediaItemToData>>(value, sType)

       val playlist = ArrayList<MediaItem>()

       for (it in list) {
           playlist.add(
               MediaItem.Builder()
                   .setUri(it.uri)
                   .setMediaId(it.id)
                   .setMediaMetadata(
                       MediaMetadata.Builder()
                           .setDisplayTitle(it.title)
                           .build()
                   )
                   .build()
           )
       }
       return playlist
    }
}