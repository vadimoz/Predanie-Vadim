package fund.predanie.medialib.domain.models.api.lists

data class ResponseAuthorsListModel(
    var status_code : Int?            = null,
    var count      : Int?            = null,
    var data       : ArrayList<Data> = arrayListOf()
)

data class Data (

    var id   : String? = null,
    var name : String? = null,
    var img  : String? = null

)