package studio.vadim.predanie.presentation.pagination

import android.content.Context
import androidx.paging.PagingSource
import androidx.paging.PagingState
import studio.vadim.predanie.data.room.AppDatabase
import studio.vadim.predanie.data.room.FavoriteTracks
import studio.vadim.predanie.data.room.UserPlaylist

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
class PlaylistsPagingSource(
    val type: String,
    private val ctx: Context,
) : PagingSource<Int, UserPlaylist>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserPlaylist> {
        return try {
            val nextOffset = params.key ?: 0

            val response = when (type) {
                "playlists" -> AppDatabase.getInstance(ctx).userPlaylistDao().getPlaylists( offset = nextOffset, limit = 15)
                else -> {AppDatabase.getInstance(ctx).userPlaylistDao().getPlaylists( offset = nextOffset, limit = 15)}
            }

            LoadResult.Page(
                data = response,
                prevKey = if (nextOffset == 0) null else nextOffset.minus(15),
                nextKey = if (response.isEmpty()) null else nextOffset.plus(15),
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, UserPlaylist>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(15)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(15)
        }
    }
}