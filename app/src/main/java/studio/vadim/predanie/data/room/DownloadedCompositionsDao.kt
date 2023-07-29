package studio.vadim.predanie.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DownloadedCompositionsDao {
    @Query("SELECT * FROM DownloadedCompositions LIMIT :limit OFFSET :offset")
    fun getCompositions(offset: Int, limit: Int): List<DownloadedCompositions>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(downloadComposition: DownloadedCompositions)

    @Query("DELETE FROM DownloadedCompositions WHERE uid = :uid")
    fun deleteByComposition(uid: Int)
}