package fund.predanie.medialib.domain.models.api.lists

data class RequestBlogListModel(
    val route: String,
    val page: String,
    val per_page: String,
)