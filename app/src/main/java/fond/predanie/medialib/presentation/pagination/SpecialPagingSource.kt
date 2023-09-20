package fund.predanie.medialib.presentation.pagination

import androidx.paging.PagingSource
import androidx.paging.PagingState
import fund.predanie.medialib.domain.models.api.lists.VideoData
import fund.predanie.medialib.domain.usecases.showLists.GetLists

class SpecialPagingSource(
    private val api: GetLists,
    val type: String,
) : PagingSource<Int, VideoData>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, VideoData> {
        return try {
            val nextOffset = 0

            val response = when (type) {
                "special" -> api.getVideoList()
                else -> {api.getVideoList()}
            }

            LoadResult.Page(
                data = response.data,
                prevKey = null,
                nextKey = null,
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