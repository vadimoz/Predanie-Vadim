package studio.vadim.predanie.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FilePositionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(filePosition: FilePosition)

    @Query("UPDATE FilePosition SET position = :position, lastPlayTimestamp = :lastPlayTimestamp, finished = :finished WHERE fileid = :fileid AND compositionid = :compositionid")
    fun updatePosition(fileid: String, position: Long, compositionid: String, lastPlayTimestamp: Long, finished: Boolean)

    @Query("SELECT * from FilePosition WHERE fileid= :fileid AND compositionid = :compositionid")
    fun getPositionByFileIdAndCompositionId(fileid: String, compositionid: String): FilePosition?

    @Query("SELECT * from FilePosition WHERE fileid= :fileid")
    fun getPositionByFileId(fileid: String?): FilePosition?

    fun insertOrUpdate(filePosition: FilePosition) {
        val itemsFromDB = getPositionByFileIdAndCompositionId(filePosition.fileid, filePosition.compositionid)
        if (itemsFromDB == null) {
            insert(filePosition)
        } else {
            updatePosition(filePosition.fileid, filePosition.position, filePosition.compositionid, filePosition.lastPlayTimestamp, filePosition.finished)
        }
    }
}