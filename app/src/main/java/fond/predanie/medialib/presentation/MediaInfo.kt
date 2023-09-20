package fund.predanie.medialib.presentation

data class MediaInfo(
    var currentMediaItemCompositionId: String,
    var currentMediaItemPredanieId: String,
    var currentPlaylistPosition: Long,
    val currentMediaItemDuration: Long,
)
