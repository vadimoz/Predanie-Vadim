package studio.vadim.predanie.domain

import studio.vadim.predanie.domain.models.api.lists.PredanieApiRequestListModel
import studio.vadim.predanie.domain.models.api.lists.PredanieApiResponseListModel

interface ApiConnection {
    suspend fun getItemsList(request: PredanieApiRequestListModel): PredanieApiResponseListModel
}