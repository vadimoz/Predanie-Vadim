package fund.predanie.medialib.data.room

import androidx.media3.common.MediaItem
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DownloadedCompositions(
    @PrimaryKey() val uid: Int?,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "playlistJson") val playlistJson: ArrayList<MediaItem>,
    @ColumnInfo(name = "image") val image: String?
)