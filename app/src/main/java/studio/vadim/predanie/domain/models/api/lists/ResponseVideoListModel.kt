package studio.vadim.predanie.domain.models.api.lists

data class ResponseVideoListModel(
    var data : ArrayList<VideoData> = arrayListOf()
)

data class Attributes (

    var title     : String? = null,
    var url       : String? = null,
    var image       : String? = null,
    var sort      : Int?    = null,
    var createdAt : String? = null,
    var updatedAt : String? = null

)

data class VideoData (

    var id         : Int?        = null,
    var attributes : Attributes? = Attributes()

)