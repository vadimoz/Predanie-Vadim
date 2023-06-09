package studio.vadim.predanie.domain.usecases.showLists

import studio.vadim.predanie.domain.models.api.lists.PredanieApiRequestListModel
import studio.vadim.predanie.domain.models.api.lists.PredanieApiResponseListModel
import studio.vadim.predanie.domain.ApiConnection

class PredefinedItemsToList(private val api: ApiConnection) {
    suspend fun getListNew(type: String, offset: Int = 0, limit: Int = 40): PredanieApiResponseListModel {
        val params = PredanieApiRequestListModel(route = "predanie.ru/api/mobile/v1/compositions/new/", type = type, offset = offset, limit = limit)
        return api.getItemsList(params)
    }
    suspend fun getListPopular(type: String, offset: Int = 0, limit: Int = 40): PredanieApiResponseListModel {
        val params = PredanieApiRequestListModel(route = "predanie.ru/api/mobile/v1/compositions/popular/", type = type, offset = offset, limit = limit)
        return api.getItemsList(params)
    }

    suspend fun getListRecomend(type: String, offset: Int = 0, limit: Int = 40): PredanieApiResponseListModel {
        val params = PredanieApiRequestListModel(route = "predanie.ru/api/mobile/v1/compositions/favorites/", type = type, offset = offset, limit = limit)
        return api.getItemsList(params)
    }
}