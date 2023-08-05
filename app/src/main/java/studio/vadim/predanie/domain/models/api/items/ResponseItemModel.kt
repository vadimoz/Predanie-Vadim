package studio.vadim.predanie.domain.models.api.items

data class ResponseItemModel(
    var data : DataItem? = DataItem()
)

data class DataItem (

    var id         : Int?              = null,
    var name       : String?           = null,
    var desc       : String?           = null,
    var author_id   : Int?              = null,
    var author_name : String?           = null,
    var img_medium  : String?           = null,
    var img_big     : String?           = null,
    var share_url   : String?           = null,
    var topics     : ArrayList<Topics> = arrayListOf(),
    var forms      : ArrayList<Forms>  = arrayListOf(),
    var genre      : ArrayList<Genre>  = arrayListOf(),
    var tracks     : ArrayList<Tracks> = arrayListOf(),
    var parts      : ArrayList<Parts>  = arrayListOf()


)

data class Parts (

    var id       : String? = null,
    var img      : String? = null,
    var name     : String? = null,
    var id_parent : String? = null,
    var desc     : String? = null

)

data class Tracks (

    var parent : String? = null,
    var id     : String? = null,
    var name   : String? = null,
    var time   : Int?    = null,
    var url    : String? = null,
    var composition : String? = null

)

data class Genre (

    var genre_id   : Int?    = null,
    var genre_name : String? = null

)

data class Forms (

    var form_id   : Int?    = null,
    var formName : String? = null

)

data class Topics (

    var topic_id   : Int?    = null,
    var topic_name : String? = null

)