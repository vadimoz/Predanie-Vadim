package studio.vadim.predanie.presentation

import androidx.paging.PagingSource
import androidx.paging.PagingState
import studio.vadim.predanie.domain.models.api.lists.Compositions
import studio.vadim.predanie.domain.usecases.showLists.GetLists

class CompositionsPagingSource(
    private val api: GetLists
) : PagingSource<Int, Compositions>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Compositions> {
        return try {
            val nextOffset = params.key ?: 0
            val response = api.getListNew("audio,music", offset = nextOffset, limit = 5)

            LoadResult.Page(
                data = response.compositions,
                prevKey = if (nextOffset == 0) null else nextOffset.minus(5),
                nextKey = if (response.compositions.isEmpty()) null else nextOffset.plus(5),
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Compositions>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(5)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(5)
        }
    }
}