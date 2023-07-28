package studio.vadim.predanie.data.room

import androidx.media3.common.MediaItem
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface DownloadedCompositionsDao {
    @Query("SELECT * FROM DownloadedCompositions")
    fun getAll(): List<DownloadedCompositions>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(downloadComposition: DownloadedCompositions)

    @Query("DELETE FROM DownloadedCompositions WHERE uid = :uid")
    fun deleteByComposition(uid: Int)
}