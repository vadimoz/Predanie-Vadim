package studio.vadim.predanie.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FavoriteTracksDao {
    @Query("SELECT * FROM FavoriteTracks ORDER BY lastTimestamp DESC LIMIT :limit OFFSET :offset")
    fun getFavoriteTracks(offset: Int, limit: Int): List<FavoriteTracks>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(FavoriteTracks: FavoriteTracks)

    @Query("SELECT * from FavoriteTracks WHERE uri= :uri LIMIT 1")
    fun getByUrl(uri: String?): FavoriteTracks?

    @Query("DELETE FROM FavoriteTracks WHERE uri = :uri")
    fun deleteByUri(uri: String)

    fun insertTrack(track: FavoriteTracks) {
        val itemsFromDB = getPositionByFileIdAndCompositionId(track.uri)
        if (itemsFromDB == null) {
            insert(track)
        } else {
            updatePosition(uri = track.uri, lastTimestamp = track.lastPlayTimestamp)
        }
    }

    @Query("SELECT * from FavoriteTracks WHERE uri= :uri")
    fun getPositionByFileIdAndCompositionId(uri: String): FavoriteTracks?

    @Query("UPDATE FavoriteTracks SET lastTimestamp = :lastTimestamp WHERE uri = :uri")
    fun updatePosition(uri: String, lastTimestamp: Long)
}