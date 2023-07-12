package studio.vadim.predanie.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import studio.vadim.predanie.data.app.Playlist

@Dao
interface PlaylistDao {
    @Query("SELECT * FROM playlist")
    fun getAll(): List<Playlist>

    @Query("SELECT * FROM playlist WHERE uid IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<Playlist>

    @Query(
        "SELECT * FROM playlist WHERE playlist_name LIKE :first AND " +
                "sort LIKE :last LIMIT 1"
    )
    fun findByName(first: String, last: String): Playlist

    @Insert
    fun insertAll(vararg playlists: Playlist)

    @Delete
    fun delete(playlist: Playlist)
}