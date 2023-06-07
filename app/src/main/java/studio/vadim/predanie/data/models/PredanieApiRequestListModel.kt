package studio.vadim.predanie.data.models

import studio.vadim.predanie.data.models.apiRoutes.PredanieApiRoutes
import studio.vadim.predanie.data.models.libraryTypes.PredanieLibraryTypes

data class PredanieApiRequestListModel(
    val route: PredanieApiRoutes,
    val library: PredanieLibraryTypes,
    val offset: Int = 20,
    val limit: Int = 20
)