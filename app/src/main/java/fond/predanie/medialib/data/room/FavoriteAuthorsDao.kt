package fund.predanie.medialib.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FavoriteAuthorsDao {
    @Query("SELECT * FROM FavoriteAuthors ORDER BY lastTimestamp DESC LIMIT :limit OFFSET :offset")
    fun getFavoriteAuthors(offset: Int, limit: Int): List<FavoriteAuthors>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(author: FavoriteAuthors)

    @Query("SELECT * from FavoriteAuthors WHERE uid= :uid LIMIT 1")
    fun getById(uid: String?): FavoriteAuthors?

    @Query("DELETE FROM FavoriteAuthors WHERE uid = :uid")
    fun deleteById(uid: String)
}