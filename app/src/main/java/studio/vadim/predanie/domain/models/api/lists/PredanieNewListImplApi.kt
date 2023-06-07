package studio.vadim.predanie.domain.models.api.lists

import studio.vadim.predanie.domain.models.api.PredanieApiRoutes
import studio.vadim.predanie.domain.models.api.PredanieRouteModel

class PredanieNewListImplApi : PredanieApiRoutes {
    val BASE_URL = "predanie.ru/api/mobile/v1/compositions/new/"
    val ROUTE = "/new"

    override fun getRoute(): PredanieRouteModel {
        return PredanieRouteModel(BASE_URL, ROUTE)
    }
}