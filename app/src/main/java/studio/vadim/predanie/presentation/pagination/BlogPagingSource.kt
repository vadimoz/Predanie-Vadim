package studio.vadim.predanie.presentation.pagination

import androidx.paging.PagingSource
import androidx.paging.PagingState
import studio.vadim.predanie.domain.models.api.lists.Compositions
import studio.vadim.predanie.domain.usecases.showLists.GetLists

class CompositionsPagingSource(
    private val api: GetLists,
    val type: String,
    val catalogId: Int
) : PagingSource<Int, Compositions>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Compositions> {
        return try {
            val nextOffset = params.key ?: 0

            val response = when (type) {
                "new" -> api.getListNew("audio,music", offset = nextOffset, limit = 15)
                "audioPopular" -> api.getListPopular("audio", offset = nextOffset, limit = 15)
                "musicPopular" -> api.getListPopular("music", offset = nextOffset, limit = 15)
                "catalogItems" -> api.getCategoryItemsList(categoryId = catalogId, offset = nextOffset, limit = 15)
                "favorites" -> api.getListFavorites("audio,music", offset = nextOffset, limit = 15)
                else -> {api.getListNew("audio,music", offset = nextOffset, limit = 15)}
            }

            LoadResult.Page(
                data = response.compositions,
                prevKey = if (nextOffset == 0) null else nextOffset.minus(15),
                nextKey = if (response.compositions.isEmpty()) null else nextOffset.plus(15),
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Compositions>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(15)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(15)
        }
    }
}