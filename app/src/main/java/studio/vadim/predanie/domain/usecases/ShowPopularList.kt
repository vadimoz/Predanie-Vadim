package studio.vadim.predanie.domain.usecases

import studio.vadim.predanie.data.models.PredanieApiResponseListModel
import studio.vadim.predanie.domain.ApiConnection

class ShowPopularList(private val api: ApiConnection) {
    suspend fun execute(): PredanieApiResponseListModel {
        return api.getItemsList()
    }
}