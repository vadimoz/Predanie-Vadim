package studio.vadim.predanie.data.models.apiRoutes

class PredaniePopularListApi : PredanieApiRoutes {
    val BASE_URL = "predanie.ru/api/mobile/v1/compositions/"
    val ROUTE = "$BASE_URL/popular"

    override fun getRoute(): PredanieRouteModel {
        return PredanieRouteModel(BASE_URL, ROUTE)
    }
}