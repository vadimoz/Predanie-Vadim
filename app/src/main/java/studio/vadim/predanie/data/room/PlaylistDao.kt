package studio.vadim.predanie.data.room

import androidx.media3.common.MediaItem
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface PlaylistDao {
    @Query("SELECT * FROM MainPlaylist")
    fun getAll(): List<MainPlaylist>

    @Query("SELECT * FROM MainPlaylist WHERE uid IN (:ids)")
    fun loadAllByIds(ids: IntArray): List<MainPlaylist>

    @Query("SELECT * FROM MainPlaylist WHERE playlist_name LIKE :playlistName LIMIT 1")
    fun findByName(playlistName: String): MainPlaylist

    @Insert(entity = MainPlaylist::class)
    fun insertAll(vararg playlists: MainPlaylist)

    @Delete
    fun delete(playlist: MainPlaylist)

    @Update(entity = MainPlaylist::class)
    fun update(playlist: MainPlaylist)

    @Query("UPDATE MainPlaylist SET timeWhereStopped =:timeWhereStopped, fileWhereStopped = :fileWhereStopped WHERE uid = :playlistId")
    fun updateCurrentTimers(timeWhereStopped: Long, fileWhereStopped: Int, playlistId: Int)

    @Query("UPDATE MainPlaylist SET playlistJson =:playlistJson WHERE uid = :playlistId")
    fun updateCurrentPlaylist(playlistJson: ArrayList<MediaItem>, playlistId: Int)
}