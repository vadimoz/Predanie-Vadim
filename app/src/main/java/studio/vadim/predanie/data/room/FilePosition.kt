package studio.vadim.predanie.data.room

import androidx.media3.common.MediaItem
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FilePosition(@PrimaryKey(autoGenerate = true) val uid: Int = 0,
                        @ColumnInfo(name = "fileid") val fileid: String,
                        @ColumnInfo(name = "compositionid") val compositionid: String,
                        @ColumnInfo(name = "position") val position: Long,
                        @ColumnInfo(name = "lastPlayTimestamp") val lastPlayTimestamp: Long,
                        @ColumnInfo(name = "finished") val finished: Boolean = false,
                        @ColumnInfo(name = "filelength")  val filelength: Long,

)