package fund.predanie.medialib.domain

import fund.predanie.medialib.domain.models.api.items.RequestAuthorModel
import fund.predanie.medialib.domain.models.api.items.RequestItemModel
import fund.predanie.medialib.domain.models.api.items.RequestPostModel
import fund.predanie.medialib.domain.models.api.items.ResponseAuthorModel
import fund.predanie.medialib.domain.models.api.items.ResponseItemModel
import fund.predanie.medialib.domain.models.api.items.ResponsePostModel
import fund.predanie.medialib.domain.models.api.lists.RequestBlogListModel
import fund.predanie.medialib.domain.models.api.lists.RequestListModel
import fund.predanie.medialib.domain.models.api.lists.RequestVideoListModel
import fund.predanie.medialib.domain.models.api.lists.ResponceBlogListModel
import fund.predanie.medialib.domain.models.api.lists.ResponseAuthorsListModel
import fund.predanie.medialib.domain.models.api.lists.ResponseCatalogModel
import fund.predanie.medialib.domain.models.api.lists.ResponseGlobalSearchListModel
import fund.predanie.medialib.domain.models.api.lists.ResponseItemsListModel
import fund.predanie.medialib.domain.models.api.lists.ResponseVideoListModel

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
