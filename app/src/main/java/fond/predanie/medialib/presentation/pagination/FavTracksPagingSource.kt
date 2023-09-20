package fund.predanie.medialib.presentation.pagination

import android.content.Context
import androidx.paging.PagingSource
import androidx.paging.PagingState
import fund.predanie.medialib.data.room.AppDatabase
import fund.predanie.medialib.data.room.FavoriteTracks

class FavTracksPagingSource(
    val type: String,
    private val ctx: Context,
) : PagingSource<Int, FavoriteTracks>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FavoriteTracks> {
        return try {
            val nextOffset = params.key ?: 0

            val response = when (type) {
                "favTracks" -> AppDatabase.getInstance(ctx).favoriteTracksDao().getFavoriteTracks( offset = nextOffset, limit = 15)
                else -> {AppDatabase.getInstance(ctx).favoriteTracksDao().getFavoriteTracks( offset = nextOffset, limit = 15)}
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

    override fun getRefreshKey(state: PagingState<Int, FavoriteTracks>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(15)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(15)
        }
    }
}