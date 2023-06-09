package studio.vadim.predanie.domain

import studio.vadim.predanie.domain.models.api.lists.RequestListModel
import studio.vadim.predanie.domain.models.api.lists.PredanieApiResponseAuthorsListModel
import studio.vadim.predanie.domain.models.api.lists.PredanieApiResponseListModel

interface ApiConnection {
    suspend fun getItemsList(request: RequestListModel): PredanieApiResponseListModel
    suspend fun getAuthorsList(request: RequestListModel): PredanieApiResponseAuthorsListModel
}