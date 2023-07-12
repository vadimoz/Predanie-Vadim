package studio.vadim.predanie.data

import androidx.room.Database
import androidx.room.RoomDatabase
import studio.vadim.predanie.data.app.Playlist

@Database(entities = [Playlist::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): PlaylistDao
}