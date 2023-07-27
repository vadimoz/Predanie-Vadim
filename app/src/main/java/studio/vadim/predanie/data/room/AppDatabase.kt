package studio.vadim.predanie.data.room

import android.content.Context
import androidx.media3.common.util.UnstableApi
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@UnstableApi
@Database(entities = [MainPlaylist::class, FilePosition::class], version = 29)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun mainPlaylistDao(): PlaylistDao
    abstract fun filePositionDao(): FilePositionDao

    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context, AppDatabase::class.java, "PredanieDB").allowMainThreadQueries().fallbackToDestructiveMigration()
                    .build()
            }
            return INSTANCE!!
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}