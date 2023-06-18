package studio.vadim.predanie.presentation

import studio.vadim.predanie.domain.models.api.lists.ResponseCatalogModel
import studio.vadim.predanie.domain.models.api.lists.ResponseGlobalSearchListModel
import studio.vadim.predanie.domain.models.api.lists.ResponseItemsListModel

data class UIState(
    val newList: ResponseItemsListModel = ResponseItemsListModel(),
    val musicPopularList: ResponseItemsListModel = ResponseItemsListModel(),
    val audioPopularList: ResponseItemsListModel = ResponseItemsListModel(),
    val favoritesList: ResponseItemsListModel = ResponseItemsListModel(),
    val catalogList: ResponseCatalogModel = ResponseCatalogModel(),
    var searchString: String = "",
    val searchList: ResponseGlobalSearchListModel = ResponseGlobalSearchListModel()
)
