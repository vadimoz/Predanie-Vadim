package studio.vadim.predanie.domain.usecases.showItems

import studio.vadim.predanie.domain.ApiConnection
import studio.vadim.predanie.domain.models.api.items.RequestAuthorModel
import studio.vadim.predanie.domain.models.api.items.RequestItemModel
import studio.vadim.predanie.domain.models.api.items.ResponseAuthorModel
import studio.vadim.predanie.domain.models.api.items.ResponseItemModel

class GetItems(private val api: ApiConnection) {
    suspend fun getItem(id: Int): ResponseItemModel {
        val params = RequestItemModel(route = "predanie.ru/api/mobile/v1/composition-single/", compositionId = id)
        return api.getItem(params)
    }

    suspend fun getAuthor(id: Int): ResponseAuthorModel {
        val params = RequestAuthorModel(route = "predanie.ru/api/mobile/v1/single-author/", authorId = id)
        return api.getAuthor(params)
    }
}