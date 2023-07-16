package studio.vadim.predanie.data.room

import androidx.media3.common.MediaItem
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MainPlaylist(@PrimaryKey(autoGenerate = true) val uid: Int = 1,
                        @ColumnInfo(name = "playlist_name") val playlistName: String,
                        @ColumnInfo(name = "timeWhereStopped") val playlistTime: Long,
                        @ColumnInfo(name = "fileWhereStopped") val playlistFile: Int,
                        @ColumnInfo(name = "playlistJson") val playlistJson: ArrayList<MediaItem>
)