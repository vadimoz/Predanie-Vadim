package studio.vadim.predanie.data.room.models

import kotlinx.serialization.Serializable

@Serializable
data class MediaItemToData(
    val id: String,
    val uri: String,
    val title: String,
    val author: String
)
