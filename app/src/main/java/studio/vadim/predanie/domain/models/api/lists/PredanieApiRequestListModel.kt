package studio.vadim.predanie.domain.models.api.lists

import studio.vadim.predanie.domain.models.api.PredanieApiRoutes

data class PredanieApiRequestListModel(
    val route: PredanieApiRoutes,
    val library: String = "music,audio",
    val offset: Int = 0,
    val limit: Int = 20
)