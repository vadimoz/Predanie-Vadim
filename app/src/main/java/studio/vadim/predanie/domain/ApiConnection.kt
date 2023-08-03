package studio.vadim.predanie.domain

import studio.vadim.predanie.domain.models.api.items.RequestAuthorModel
import studio.vadim.predanie.domain.models.api.items.RequestItemModel
import studio.vadim.predanie.domain.models.api.items.RequestPostModel
import studio.vadim.predanie.domain.models.api.items.ResponseAuthorModel
import studio.vadim.predanie.domain.models.api.items.ResponseItemModel
import studio.vadim.predanie.domain.models.api.items.ResponsePostModel
import studio.vadim.predanie.domain.models.api.lists.RequestBlogListModel
import studio.vadim.predanie.domain.models.api.lists.RequestListModel
import studio.vadim.predanie.domain.models.api.lists.RequestVideoListModel
import studio.vadim.predanie.domain.models.api.lists.ResponceBlogListModel
import studio.vadim.predanie.domain.models.api.lists.ResponseAuthorsListModel
import studio.vadim.predanie.domain.models.api.lists.ResponseCatalogModel
import studio.vadim.predanie.domain.models.api.lists.ResponseGlobalSearchListModel
import studio.vadim.predanie.domain.models.api.lists.ResponseItemsListModel
import studio.vadim.predanie.domain.models.api.lists.ResponseVideoListModel

interface ApiConnection {
    suspend fun getAuthor(request: RequestAuthorModel): ResponseAuthorModel
    suspend fun getItem (request: RequestItemModel): ResponseItemModel
    suspend fun getPost (request: RequestPostModel): ResponsePostModel
    suspend fun getItemsList(request: RequestListModel): ResponseItemsListModel
    suspend fun getAuthorsList(request: RequestListModel): ResponseAuthorsListModel
    suspend fun getCatalogList(request: RequestListModel): ResponseCatalogModel
    suspend fun getGlobalSearchList(request: RequestListModel): ResponseGlobalSearchListModel
    suspend fun getVideoList(request: RequestVideoListModel): ResponseVideoListModel
    suspend fun getBlogList(request: RequestBlogListModel): List<ResponceBlogListModel>
}
