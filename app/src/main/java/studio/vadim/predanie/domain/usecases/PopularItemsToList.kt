package studio.vadim.predanie.domain.usecases

import studio.vadim.predanie.data.models.PredanieApiRequestListModel
import studio.vadim.predanie.domain.models.PredanieApiResponseListModel
import studio.vadim.predanie.domain.ApiConnection

class PopularItemsToList(private val api: ApiConnection, private val params: PredanieApiRequestListModel) {
    suspend fun execute(): PredanieApiResponseListModel {
        return api.getItemsList(params)
    }
}