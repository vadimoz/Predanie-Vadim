package fund.predanie.medialib.domain.models.api.lists

data class RequestVideoListModel(
    val route: String,
    val type: String = "video",
    val offset: Int = 0,
    val limit: Int = 40,
    val search: String = "",
    val q: String = "",
    val id_category: Int = 0,
    val letter: String = ""
)