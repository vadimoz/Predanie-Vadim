package studio.vadim.predanie.domain

import studio.vadim.predanie.data.models.PredanieApiRequestListModel
import studio.vadim.predanie.domain.models.PredanieApiResponseListModel

interface ApiConnection {
    suspend fun getItemsList(request: PredanieApiRequestListModel): PredanieApiResponseListModel
}