package studio.vadim.predanie.data.room

import androidx.media3.common.MediaItem
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserPlaylistDao {
    @Query("SELECT * FROM UserPlaylist")
    fun getAll(): List<UserPlaylist>

    @Query("SELECT * FROM UserPlaylist ORDER BY uid DESC LIMIT :limit OFFSET :offset")
    fun getPlaylists(offset: Int, limit: Int): List<UserPlaylist>

    @Query("DELETE FROM UserPlaylist WHERE playlist_name = :name")
    fun deleteByName(name: String)

    @Query("SELECT * FROM UserPlaylist WHERE uid IN (:ids)")
    fun loadAllByIds(ids: IntArray): List<UserPlaylist>

    @Query("SELECT * FROM UserPlaylist WHERE playlist_name LIKE :playlistName LIMIT 1")
    fun findByName(playlistName: String): UserPlaylist

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(playlist: UserPlaylist)

    fun insertPlaylist(playlist: UserPlaylist) {
        val itemsFromDB = getExistPlaylistWithName(playlist.playlistName)
        if (itemsFromDB == null) {
            insert(playlist)
        } else {
            updatePosition(playlistJson = playlist.playlistJson, name = playlist.playlistName)
        }
    }

    @Query("UPDATE UserPlaylist SET playlistJson = :playlistJson WHERE playlist_name = :name")
    fun updatePosition(playlistJson: ArrayList<MediaItem>, name: String)

    @Query("SELECT * from UserPlaylist WHERE playlist_name= :playlist_name")
    fun getExistPlaylistWithName(playlist_name: String): UserPlaylist?

    @Delete
    fun delete(playlist: UserPlaylist)

    @Update(entity = UserPlaylist::class)
    fun update(playlist: UserPlaylist)
}