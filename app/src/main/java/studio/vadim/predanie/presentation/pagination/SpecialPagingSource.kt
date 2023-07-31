package studio.vadim.predanie.presentation.pagination

import android.content.Context
import androidx.paging.PagingSource
import androidx.paging.PagingState
import studio.vadim.predanie.data.room.AppDatabase
import studio.vadim.predanie.data.room.HistoryCompositions
import studio.vadim.predanie.domain.models.api.lists.VideoData
import studio.vadim.predanie.domain.usecases.showLists.GetLists

class SpecialPagingSource(
    private val api: GetLists,
    val type: String,
) : PagingSource<Int, VideoData>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, VideoData> {
        return try {
            val nextOffset = params.key ?: 0

            val response = when (type) {
                "special" -> api.getVideoList()
                else -> {api.getVideoList()}
            }

            LoadResult.Page(
                data = response.data,
                prevKey = if (nextOffset == 0) null else nextOffset.minus(15),
                nextKey = if (response.data.isEmpty()) null else nextOffset.plus(15),
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, VideoData>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(15)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(15)
        }
    }
}