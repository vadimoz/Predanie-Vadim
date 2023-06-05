package studio.vadim.predanie.domain

import studio.vadim.predanie.data.models.PredanieApiResponseListModel

interface ApiConnection {
    suspend fun getItemsList(): PredanieApiResponseListModel
}