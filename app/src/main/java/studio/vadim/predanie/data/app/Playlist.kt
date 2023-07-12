package studio.vadim.predanie.data.app

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Playlist(@PrimaryKey val uid: Int,
                    @ColumnInfo(name = "playlist_name") val playlistName: String?,
                    @ColumnInfo(name = "sort") val playlistSort: String?)
