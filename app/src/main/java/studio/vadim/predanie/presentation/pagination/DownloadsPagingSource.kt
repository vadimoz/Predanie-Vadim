package studio.vadim.predanie.presentation.pagination

import android.content.Context
import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import studio.vadim.predanie.data.room.AppDatabase
import studio.vadim.predanie.data.room.DownloadedCompositions

class DownloadsPagingSource(
    val type: String,
    private val ctx: Context,
) : PagingSource<Int, DownloadedCompositions>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DownloadedCompositions> {
        return try {
            val nextOffset = params.key ?: 0

            val response = when (type) {
                "downloads" -> AppDatabase.getInstance(ctx).downloadedCompositionsDao().getCompositions( offset = nextOffset, limit = 15)
                else -> {AppDatabase.getInstance(ctx).downloadedCompositionsDao().getCompositions( offset = nextOffset, limit = 15)}
            }

            LoadResult.Page(
                data = response,
                prevKey = if (nextOffset == 0) null else nextOffset.minus(15),
                nextKey = if (response.isEmpty()) null else nextOffset.plus(15),
            )
        } catch (e: Exception) {
            Log.d("Error in Pagins", e.message.toString())
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, DownloadedCompositions>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(15)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(15)
        }
    }
}