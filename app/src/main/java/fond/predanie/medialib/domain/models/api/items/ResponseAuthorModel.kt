package fund.predanie.medialib.domain.models.api.items

data class ResponseAuthorModel(
    var data       : AuthorData?   = AuthorData(),
    var status_code : Int?    = null,
    var banner     : Banner? = Banner()
)

data class AuthorCompositions (

    var id   : Int?    = null,
    var name : String? = null,
    var img_s : String? = null

)

data class AuthorData (

    var id           : Int?                    = null,
    var name         : String?                 = null,
    var years        : String?                 = null,
    var img          : String?                 = null,
    var desc         : String?                 = null,
    var share_url     : String?                 = null,
    var compositions : ArrayList<AuthorCompositions> = arrayListOf()

)

data class Banner (

    var url    : String? = null,
    var img    : String? = null,
    var h      : Int?    = null,
    var height : Int?    = null

)