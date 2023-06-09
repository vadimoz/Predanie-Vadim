package studio.vadim.predanie.domain.models.api.lists

data class ResponseGlobalSearchListModel(
    var entities   : ArrayList<Entities> = arrayListOf(),
    var count      : Int?                = null,
    var status_code : Int?                = null,
    var banner     : Banner?             = Banner()

)

data class Entities (

    var id         : Int?    = null,
    var name       : String? = null,
    var entity_type : String? = null,
    var img        : String? = null

)