package fund.predanie.medialib.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface HistoryCompositionsDao {
    @Query("SELECT * FROM HistoryCompositions ORDER BY lastTimestamp DESC LIMIT :limit OFFSET :offset")
    fun getHistory(offset: Int, limit: Int): List<HistoryCompositions>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(downloadComposition: HistoryCompositions)
}