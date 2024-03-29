package fund.predanie.medialib.data.room

import androidx.media3.common.MediaItem
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserPlaylist(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo(name = "playlist_name") val playlistName: String,
    @ColumnInfo(name = "playlistJson") val playlistJson: ArrayList<MediaItem>,
)