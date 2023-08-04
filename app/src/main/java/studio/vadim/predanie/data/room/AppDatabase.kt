package studio.vadim.predanie.data.room

import android.content.Context
import androidx.media3.common.util.UnstableApi
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@UnstableApi
@Database(
    entities = [MainPlaylist::class, FilePosition::class, DownloadedCompositions::class, HistoryCompositions::class,
        FavoriteTracks::class, FavoriteCompositions::class, FavoriteAuthors::class],
    version = 1,
    /*autoMigrations = [
        AutoMigration(from = 2, to = 3)
    ]*/
)
@TypeConverters(Converters::class)

abstract class AppDatabase : RoomDatabase() {
    abstract fun mainPlaylistDao(): PlaylistDao
    abstract fun filePositionDao(): FilePositionDao
    abstract fun downloadedCompositionsDao(): DownloadedCompositionsDao
    abstract fun historyCompositionsDao(): HistoryCompositionsDao
    abstract fun favoriteAuthorsDao(): FavoriteAuthorsDao
    abstract fun favoriteTracksDao(): FavoriteTracksDao
    abstract fun favoriteCompositionsDao(): FavoriteCompositionsDao

    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context, AppDatabase::class.java, "PredanieDB")
                    .allowMainThreadQueries()
                    .build()
            }
            return INSTANCE!!
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}