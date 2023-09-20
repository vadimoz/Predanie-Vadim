package fund.predanie.medialib.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DownloadedCompositionsDao {
    @Query("SELECT * FROM DownloadedCompositions ORDER BY uid DESC LIMIT :limit OFFSET :offset")
    fun getCompositions(offset: Int, limit: Int): List<DownloadedCompositions>

    @Query("SELECT * FROM DownloadedCompositions WHERE uid LIKE :id LIMIT 1")
    fun findById(id: String): DownloadedCompositions

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(downloadComposition: DownloadedCompositions)

    @Query("DELETE FROM DownloadedCompositions WHERE uid = :uid")
    fun deleteByComposition(uid: Int)

    @Query("DELETE FROM DownloadedCompositions")
    fun removeAll()
}