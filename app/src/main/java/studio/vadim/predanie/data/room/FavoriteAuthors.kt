package studio.vadim.predanie.data.room

import androidx.media3.common.MediaItem
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FavoriteAuthors(
    @PrimaryKey() val uid: Int?,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "lastTimestamp") val lastPlayTimestamp: Long,
    @ColumnInfo(name = "image") val image: String?
)