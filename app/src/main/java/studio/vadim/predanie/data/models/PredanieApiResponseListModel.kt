package studio.vadim.predanie.data.models

import kotlinx.serialization.Serializable

data class PredanieApiResponseListModel(
    var compositions : ArrayList<Compositions> = arrayListOf(),
    var count        : Int?                    = null,
    var statusCode   : Int?                    = null,
    var banner       : Banner?                 = Banner()
)

data class Compositions (

    var id         : Int?    = null,
    var name       : String? = null,
    var desc       : String? = null,
    var authorName : String? = null,
    var entityType : String? = null,
    var imgS       : String? = null

)

data class Banner (

    var url    : String? = null,
    var img    : String? = null,
    var h      : Int?    = null,
    var height : Int?    = null,
    var type   : String? = null

)