package studio.vadim.predanie.presentation

import studio.vadim.predanie.domain.models.api.lists.ResponseItemsListModel

data class UIState(
    var newList: ResponseItemsListModel = ResponseItemsListModel()
)
