package studio.vadim.predanie.domain.models.api.items

import com.google.gson.annotations.SerializedName
import studio.vadim.predanie.domain.models.api.lists.Author
import studio.vadim.predanie.domain.models.api.lists.wpFeaturedmedia
import studio.vadim.predanie.domain.models.api.lists.wpTerm

data class ResponsePostModel(
    val _embedded: Embedded,
    val content: Content,
    val date: String,
    val id: Int,
    val title: Title
)

data class Content(
    val rendered: String
)

data class Embedded(

    var author: ArrayList<Author> = arrayListOf(),
    @SerializedName("wp:featuredmedia")
    var wp_featuredmedia: List<wpFeaturedmedia> = listOf(),
    @SerializedName("wp:term") var wp_term: ArrayList<ArrayList<wpTerm>> = arrayListOf()

)


data class WpTerm(
    val id: Int,
    val name: String
)

data class WpFeaturedmedia(
    val source_url: String
)

data class Title(
    val rendered: String
)