package studio.vadim.predanie.data.models

data class PredanieApiRequestListModel (
    val catalog: String,
    val type: String,
    val offset: Int,
    val limit: Int
)
