package fund.predanie.medialib.domain.usecases.showItems

import fund.predanie.medialib.domain.ApiConnection
import fund.predanie.medialib.domain.models.api.items.RequestAuthorModel
import fund.predanie.medialib.domain.models.api.items.RequestItemModel
import fund.predanie.medialib.domain.models.api.items.RequestPostModel
import fund.predanie.medialib.domain.models.api.items.ResponseAuthorModel
import fund.predanie.medialib.domain.models.api.items.ResponseItemModel
import fund.predanie.medialib.domain.models.api.items.ResponsePostModel

class GetItems(private val api: ApiConnection) {
    suspend fun getItem(id: Int): ResponseItemModel {
        val params = RequestItemModel(route = "predanie.ru/api/mobile/v1/composition-single/", compositionId = id)
        return api.getItem(params)
    }

    suspend fun getAuthor(id: Int): ResponseAuthorModel {
        val params = RequestAuthorModel(route = "predanie.ru/api/mobile/v1/single-author/", authorId = id)
        return api.getAuthor(params)
    }

    suspend fun getPost(postId: String): ResponsePostModel {
        val params = RequestPostModel(route = "blog.predanie.ru/wp-json/wp/v2/posts", postId = postId)
        return api.getPost(params)
    }
}