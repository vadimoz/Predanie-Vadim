package fund.predanie.medialib.presentation.pagination

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import fund.predanie.medialib.domain.models.api.lists.ResponceBlogListModel
import fund.predanie.medialib.domain.usecases.showLists.GetLists

class BlogPagingSource(
    private val api: GetLists,
    val type: String
) : PagingSource<Int, ResponceBlogListModel>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ResponceBlogListModel> {
        return try {
            val nextOffset = params.key ?: 1

            val response = when (type) {
                "blog" -> api.getBlogList(page = nextOffset.toString(), perPage = "5")
                else -> {api.getBlogList(page = nextOffset.toString(), perPage = "5")}
            }

            LoadResult.Page(
                data = response,
                prevKey = if (nextOffset == 1) null else nextOffset.minus(1),
                nextKey = if (response.isEmpty()) null else nextOffset.plus(1),
            )
        } catch (e: Exception) {
            Log.d("loaderror", e.message.toString())
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ResponceBlogListModel>): Int? {

        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}