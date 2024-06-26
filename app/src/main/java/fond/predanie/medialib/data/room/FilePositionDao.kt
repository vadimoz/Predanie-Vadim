package fund.predanie.medialib.data.room

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

    @Query("SELECT * from FilePosition WHERE compositionid= :compositionid")
    fun getCheckCompositionPlayed(compositionid: String?): FilePosition?

    @Query("SELECT * from FilePosition WHERE compositionid= :compositionid AND lastPlayTimestamp = (SELECT max(lastPlayTimestamp) FROM FilePosition WHERE compositionid= :compositionid )")
    fun getLastCompositionPlayedFileId(compositionid: String?): FilePosition?

    fun insertOrUpdate(filePosition: FilePosition) {
        val itemsFromDB = getPositionByFileIdAndCompositionId(filePosition.fileid, filePosition.compositionid)
        if (itemsFromDB == null) {
            insert(filePosition)
        } else {
            updatePosition(filePosition.fileid, filePosition.position, filePosition.compositionid, filePosition.lastPlayTimestamp, filePosition.finished)
        }
    }

    @Query("DELETE FROM FilePosition WHERE compositionid = :compositionid")
    fun deleteByComposition(compositionid: Int)
}