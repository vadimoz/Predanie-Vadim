package fund.predanie.medialib.data.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FavoriteTracks(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "compositionid") val compositionid: String,
    @ColumnInfo(name = "uri") val uri: String,
    @ColumnInfo(name = "lastTimestamp") val lastPlayTimestamp: Long,
)