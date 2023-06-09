package studio.vadim.predanie.domain.models.api.lists

data class PredanieApiRequestListModel(
    val route: String,
    val type: String = "music,audio",
    val offset: Int = 0,
    val limit: Int = 40
)