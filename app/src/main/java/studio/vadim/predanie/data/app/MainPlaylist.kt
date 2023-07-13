package studio.vadim.predanie.data.app

import androidx.media3.common.MediaItem
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MainPlaylist(@PrimaryKey val uid: Int,
                        @ColumnInfo(name = "playlist_name") val playlistName: String?,
                        @ColumnInfo(name = "sort") val playlistSort: Long?,
                        @ColumnInfo(name = "object") val playlist: String
)