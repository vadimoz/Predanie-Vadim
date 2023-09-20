package fund.predanie.medialib.domain.models.api.lists

data class ResponseCatalogModel(
    var categories : ArrayList<Categories> = arrayListOf(),
    var status_code : Int?                  = null,
    var banner     : BannerCatalog?               = BannerCatalog()
)

data class Categories (

    var id_category : Int?              = null,
    var id_parent   : Int?              = null,
    var name       : String?           = null,
    var categories : ArrayList<SubCategories> = arrayListOf()
)

data class SubCategories (

    var id_category : Int?    = null,
    var id_parent   : Int?    = null,
    var name       : String? = null,
    var categories : ArrayList<InnerCategories>? = arrayListOf()
)

data class InnerCategories (

    var id_category : Int?    = null,
    var id_parent   : Int?    = null,
    var name       : String? = null,
)

data class BannerCatalog (

    var url    : String? = null,
    var img    : String? = null,
    var h      : Int?    = null,
    var height : Int?    = null,
    var type   : String? = null

)