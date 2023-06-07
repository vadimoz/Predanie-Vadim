package studio.vadim.predanie.data.models.libraryTypes

class PredanieAudioLibrary : PredanieLibraryTypes {
    val library = "audio,music"

    override fun getLibraryType(): PredanieLibTypesModel {
        return PredanieLibTypesModel(library)
    }
}