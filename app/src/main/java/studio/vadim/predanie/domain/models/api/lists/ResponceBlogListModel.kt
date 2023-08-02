package studio.vadim.predanie.domain.models.api.lists

data class RequestBlogListModel(
    var id            : Int?              = null,
    var date          : String?           = null,
    var dateGmt       : String?           = null,
    var guid          : Guid?             = Guid(),
    var modified      : String?           = null,
    var modifiedGmt   : String?           = null,
    var slug          : String?           = null,
    var status        : String?           = null,
    var type          : String?           = null,
    var link          : String?           = null,
    var title         : Title?            = Title(),
    var content       : Content?          = Content(),
    var excerpt       : Excerpt?          = Excerpt(),
    var author        : Int?              = null,
    var featuredMedia : Int?              = null,
    var commentStatus : String?           = null,
    var pingStatus    : String?           = null,
    var sticky        : Boolean?          = null,
    var template      : String?           = null,
    var format        : String?           = null,
    var meta          : ArrayList<String> = arrayListOf(),
    var categories    : ArrayList<Int>    = arrayListOf(),
    var tags          : ArrayList<Int>    = arrayListOf(),
)

data class Guid (

    var rendered : String? = null

)

data class Title (

    var rendered : String? = null

)

data class Content (

    var rendered  : String?  = null,
    var protected : Boolean? = null

)

data class Excerpt (

    var rendered  : String?  = null,
    var protected : Boolean? = null

)