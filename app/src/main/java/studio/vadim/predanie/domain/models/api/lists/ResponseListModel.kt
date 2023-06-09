package studio.vadim.predanie.domain.models.api.lists

data class PredanieApiResponseListModel(
    var compositions : ArrayList<Compositions> = arrayListOf(),
    var count        : Int?                    = null,
    var status_code   : Int?                    = null,
    var banner       : Banner?                 = Banner()
)

data class Compositions (

    var id         : Int?    = null,
    var name       : String? = null,
    var desc       : String? = null,
    var author_name : String? = null,
    var entity_type : String? = null,
    var img_s       : String? = null

)

data class Banner (

    var url    : String? = null,
    var img    : String? = null,
    var h      : Int?    = null,
    var height : Int?    = null,
    var type   : String? = null

)