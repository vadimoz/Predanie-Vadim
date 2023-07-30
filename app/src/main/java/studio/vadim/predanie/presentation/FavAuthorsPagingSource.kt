package studio.vadim.predanie.presentation

import android.content.Context
import androidx.paging.PagingSource
import androidx.paging.PagingState
import studio.vadim.predanie.data.room.AppDatabase
import studio.vadim.predanie.data.room.FavoriteAuthors
import studio.vadim.predanie.data.room.HistoryCompositions

class FavAuthorsPagingSource(
    val type: String,
    private val ctx: Context,
) : PagingSource<Int, FavoriteAuthors>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FavoriteAuthors> {
        return try {
            val nextOffset = params.key ?: 0

            val response = when (type) {
                "favAuthors" -> AppDatabase.getInstance(ctx).favoriteAuthorsDao().getFavoriteAuthors( offset = nextOffset, limit = 15)
                else -> {AppDatabase.getInstance(ctx).favoriteAuthorsDao().getFavoriteAuthors( offset = nextOffset, limit = 15)}
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

    override fun getRefreshKey(state: PagingState<Int, FavoriteAuthors>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(15)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(15)
        }
    }
}