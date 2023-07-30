package studio.vadim.predanie.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FavoriteCompositionsDao {
    @Query("SELECT * FROM FavoriteCompositions ORDER BY lastTimestamp DESC LIMIT :limit OFFSET :offset")
    fun getFavoriteCompositions(offset: Int, limit: Int): List<FavoriteCompositions>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(composition: FavoriteCompositions)

    @Query("SELECT * from FavoriteCompositions WHERE uid= :uid LIMIT 1")
    fun getById(uid: String?): FavoriteCompositions?

    @Query("DELETE FROM FavoriteCompositions WHERE uid = :uid")
    fun deleteById(uid: String)
}