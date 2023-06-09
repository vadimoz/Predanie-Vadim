package studio.vadim.predanie.domain.models.api.lists

data class PredanieApiResponseAuthorsListModel(
    var statusCode : Int?            = null,
    var count      : Int?            = null,
    var data       : ArrayList<Data> = arrayListOf()
)

data class Data (

    var id   : String? = null,
    var name : String? = null,
    var img  : String? = null

)