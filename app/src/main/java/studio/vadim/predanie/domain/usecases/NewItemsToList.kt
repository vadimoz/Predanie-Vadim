package studio.vadim.predanie.domain.usecases

import studio.vadim.predanie.domain.models.api.lists.PredanieApiRequestListModel
import studio.vadim.predanie.domain.models.api.lists.PredanieApiResponseListModel
import studio.vadim.predanie.domain.ApiConnection

class NewItemsToList(private val api: ApiConnection, private val params: PredanieApiRequestListModel) {
    suspend fun execute(): PredanieApiResponseListModel {
        return api.getItemsList(params)
    }
}