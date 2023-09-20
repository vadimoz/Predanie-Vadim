package fund.predanie.medialib.domain.models.api.lists

import com.google.gson.annotations.SerializedName


data class ResponceBlogListModel(
    @SerializedName("id"             ) var id            : Int?              = null,
    @SerializedName("date"           ) var date          : String?           = null,
    @SerializedName("date_gmt"       ) var dateGmt       : String?           = null,
    @SerializedName("guid"           ) var guid          : Guid?             = Guid(),
    @SerializedName("modified"       ) var modified      : String?           = null,
    @SerializedName("modified_gmt"   ) var modifiedGmt   : String?           = null,
    @SerializedName("slug"           ) var slug          : String?           = null,
    @SerializedName("status"         ) var status        : String?           = null,
    @SerializedName("type"           ) var type          : String?           = null,
    @SerializedName("link"           ) var link          : String?           = null,
    @SerializedName("title"          ) var title         : Title?            = Title(),
    @SerializedName("content"        ) var content       : Content?          = Content(),
    @SerializedName("excerpt"        ) var excerpt       : Excerpt?          = Excerpt(),
    @SerializedName("author"         ) var author        : Int?              = null,
    @SerializedName("featured_media" ) var featuredMedia : Int?              = null,
    @SerializedName("comment_status" ) var commentStatus : String?           = null,
    @SerializedName("ping_status"    ) var pingStatus    : String?           = null,
    @SerializedName("sticky"         ) var sticky        : Boolean?          = null,
    @SerializedName("template"       ) var template      : String?           = null,
    @SerializedName("format"         ) var format        : String?           = null,
    @SerializedName("meta"           ) var meta          : ArrayList<String> = arrayListOf(),
    @SerializedName("categories"     ) var categories    : ArrayList<Int>    = arrayListOf(),
    @SerializedName("tags"           ) var tags          : ArrayList<Int>    = arrayListOf(),
    @SerializedName("_links"         ) var Links         : Links?            = Links(),
    @SerializedName("_embedded"      ) var Embedded      : Embedded?         = Embedded()
)

data class Guid(

    var rendered: String? = null

)

data class Embedded(

    var author: ArrayList<Author> = arrayListOf(),
    @SerializedName("wp:featuredmedia")
    var wp_featuredmedia: List<wpFeaturedmedia> = listOf(),
    @SerializedName("wp:term") var wp_term: ArrayList<ArrayList<wpTerm>> = arrayListOf()

)

data class wpTerm(

    @SerializedName("taxonomy") var taxonomy: String? = null,
    @SerializedName("embeddable") var embeddable: Boolean? = null,
    @SerializedName("href") var href: String? = null

)

data class Author(

    @SerializedName("embeddable") var embeddable: Boolean? = null,
    @SerializedName("href") var href: String? = null

)

data class wpFeaturedmedia(
    val source_url: String
)

data class Caption(

    @SerializedName("rendered") var rendered: String? = null

)

data class Links(

    @SerializedName("self") var self: ArrayList<Self> = arrayListOf(),
    @SerializedName("collection") var collection: ArrayList<Collection> = arrayListOf(),
    @SerializedName("about") var about: ArrayList<About> = arrayListOf(),
    @SerializedName("author") var author: ArrayList<Author> = arrayListOf(),
    @SerializedName("replies") var replies: ArrayList<Replies> = arrayListOf(),
    @SerializedName("version-history") var versionHistory: ArrayList<versionHistory> = arrayListOf(),
    @SerializedName("predecessor-version") var predecessorVersion: ArrayList<predecessorVersion> = arrayListOf(),
    @SerializedName("wp:attachment") var wpAttachment: ArrayList<wpAttachment> = arrayListOf(),
    @SerializedName("wp:term") var wpTerm: ArrayList<wpTerm> = arrayListOf(),
    @SerializedName("curies") var curies: ArrayList<Curies> = arrayListOf()

)

data class Self(

    @SerializedName("href") var href: String? = null

)

data class Collection(

    @SerializedName("href") var href: String? = null

)

data class About(

    @SerializedName("href") var href: String? = null

)


data class Replies(

    @SerializedName("embeddable") var embeddable: Boolean? = null,
    @SerializedName("href") var href: String? = null

)

data class Curies(

    @SerializedName("name") var name: String? = null,
    @SerializedName("href") var href: String? = null,
    @SerializedName("templated") var templated: Boolean? = null

)

data class versionHistory(

    @SerializedName("count") var count: Int? = null,
    @SerializedName("href") var href: String? = null

)

data class predecessorVersion(

    @SerializedName("id") var id: Int? = null,
    @SerializedName("href") var href: String? = null

)

data class wpAttachment(

    @SerializedName("href") var href: String? = null

)

data class MediaDetails(

    @SerializedName("width") var width: Int? = null,
    @SerializedName("height") var height: Int? = null,
    @SerializedName("file") var file: String? = null,
    @SerializedName("sizes") var sizes: Sizes? = Sizes(),
    @SerializedName("image_meta") var imageMeta: ImageMeta? = ImageMeta()

)

data class ImageMeta(

    @SerializedName("aperture") var aperture: String? = null,
    @SerializedName("credit") var credit: String? = null,
    @SerializedName("camera") var camera: String? = null,
    @SerializedName("caption") var caption: String? = null,
    @SerializedName("created_timestamp") var createdTimestamp: String? = null,
    @SerializedName("copyright") var copyright: String? = null,
    @SerializedName("focal_length") var focalLength: String? = null,
    @SerializedName("iso") var iso: String? = null,
    @SerializedName("shutter_speed") var shutterSpeed: String? = null,
    @SerializedName("title") var title: String? = null,
    @SerializedName("orientation") var orientation: String? = null,
    @SerializedName("keywords") var keywords: ArrayList<String> = arrayListOf()

)

data class Sizes(

    @SerializedName("thumbnail") var thumbnail: Thumbnail? = Thumbnail(),
    @SerializedName("medium") var medium: Medium? = Medium(),
    @SerializedName("medium_large") var mediumLarge: MediumLarge? = MediumLarge(),
    @SerializedName("large") var large: Large? = Large(),
    @SerializedName("alm-thumbnail") var almThumbnail: AlmThumbnail? = AlmThumbnail(),
    @SerializedName("full") var full: Full? = Full()

)

data class AlmThumbnail(

    @SerializedName("file") var file: String? = null,
    @SerializedName("width") var width: Int? = null,
    @SerializedName("height") var height: Int? = null,
    @SerializedName("mime_type") var mimeType: String? = null,
    @SerializedName("source_url") var sourceUrl: String? = null

)


data class Full(

    @SerializedName("file") var file: String? = null,
    @SerializedName("width") var width: Int? = null,
    @SerializedName("height") var height: Int? = null,
    @SerializedName("mime_type") var mimeType: String? = null,
    @SerializedName("source_url") var sourceUrl: String? = null

)

data class MediumLarge(

    @SerializedName("file") var file: String? = null,
    @SerializedName("width") var width: Int? = null,
    @SerializedName("height") var height: Int? = null,
    @SerializedName("mime_type") var mimeType: String? = null,
    @SerializedName("source_url") var sourceUrl: String? = null

)

data class Large(

    @SerializedName("file") var file: String? = null,
    @SerializedName("width") var width: Int? = null,
    @SerializedName("height") var height: Int? = null,
    @SerializedName("mime_type") var mimeType: String? = null,
    @SerializedName("source_url") var sourceUrl: String? = null

)

data class Thumbnail(

    @SerializedName("file") var file: String? = null,
    @SerializedName("width") var width: Int? = null,
    @SerializedName("height") var height: Int? = null,
    @SerializedName("mime_type") var mimeType: String? = null,
    @SerializedName("source_url") var sourceUrl: String? = null

)


data class Medium(

    @SerializedName("file") var file: String? = null,
    @SerializedName("width") var width: Int? = null,
    @SerializedName("height") var height: Int? = null,
    @SerializedName("mime_type") var mimeType: String? = null,
    @SerializedName("source_url") var sourceUrl: String? = null

)

data class Title(

    var rendered: String? = null

)

data class Content(

    var rendered: String? = null,
    var protected: Boolean? = null

)

data class Excerpt(

    var rendered: String? = null,
    var protected: Boolean? = null

)