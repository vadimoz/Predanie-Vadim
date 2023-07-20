package studio.vadim.predanie.data.room

import android.text.TextUtils
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FilePositionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(filePosition: FilePosition)

    @Query("UPDATE FilePosition SET position = :position WHERE fileid = :fileid AND compositionid = :compositionid")
    fun updatePosition(fileid: String, position: Long, compositionid: String)

    @Query("SELECT * from FilePosition WHERE fileid= :fileid AND compositionid = :compositionid")
    fun getPositionByFileId(fileid: String, compositionid: String): FilePosition?

    fun insertOrUpdate(filePosition: FilePosition) {
        val itemsFromDB = getPositionByFileId(filePosition.fileid, filePosition.compositionid)
        if (itemsFromDB == null) {
            insert(filePosition)
        } else {
            updatePosition(filePosition.fileid, filePosition.position, filePosition.compositionid)
        }
    }
}