package studio.vadim.predanie.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import studio.vadim.predanie.data.app.MainPlaylist

@Dao
interface PlaylistDao {
    @Query("SELECT * FROM MainPlaylist")
    fun getAll(): List<MainPlaylist>

    @Query("SELECT * FROM MainPlaylist WHERE uid IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<MainPlaylist>

    @Query(
        "SELECT * FROM MainPlaylist WHERE playlist_name LIKE :first AND " +
                "sort LIKE :last LIMIT 1"
    )
    fun findByName(first: String, last: String): MainPlaylist

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg playlists: MainPlaylist)

    @Delete
    fun delete(playlist: MainPlaylist)
}