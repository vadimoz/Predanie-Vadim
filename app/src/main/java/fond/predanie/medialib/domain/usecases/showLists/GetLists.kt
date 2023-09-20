package fund.predanie.medialib.domain.usecases.showLists

import fund.predanie.medialib.domain.models.api.lists.RequestListModel
import fund.predanie.medialib.domain.models.api.lists.ResponseItemsListModel
import fund.predanie.medialib.domain.ApiConnection
import fund.predanie.medialib.domain.models.api.lists.RequestBlogListModel
import fund.predanie.medialib.domain.models.api.lists.RequestVideoListModel
import fund.predanie.medialib.domain.models.api.lists.ResponceBlogListModel
import fund.predanie.medialib.domain.models.api.lists.ResponseAuthorsListModel
import fund.predanie.medialib.domain.models.api.lists.ResponseCatalogModel
import fund.predanie.medialib.domain.models.api.lists.ResponseGlobalSearchListModel
import fund.predanie.medialib.domain.models.api.lists.ResponseVideoListModel

class GetLists(private val api: ApiConnection) {
    suspend fun getListNew(type: String, offset: Int = 0, limit: Int = 40): ResponseItemsListModel {
        val params = RequestListModel(route = "predanie.ru/api/mobile/v1/compositions/new/", type = type, offset = offset, limit = limit)
        return api.getItemsList(params)
    }
    suspend fun getListPopular(type: String, offset: Int = 0, limit: Int = 40): ResponseItemsListModel {
        val params = RequestListModel(route = "predanie.ru/api/mobile/v1/compositions/popular/", type = type, offset = offset, limit = limit)
        return api.getItemsList(params)
    }
    suspend fun getListFavorites(type: String, offset: Int = 0, limit: Int = 40): ResponseItemsListModel {
        val params = RequestListModel(route = "predanie.ru/api/mobile/v1/compositions/favorites/", type = type, offset = offset, limit = limit)
        return api.getItemsList(params)
    }
    suspend fun getAuthorsSearchList(type: String, search: String, offset: Int = 0, limit: Int = 40): ResponseAuthorsListModel {
        val params = RequestListModel(route = "predanie.ru/api/mobile/v1/author/", type = type, search = search, offset = offset, limit = limit)
        return api.getAuthorsList(params)
    }
    suspend fun getAuthorsLetterList(letter: String): ResponseAuthorsListModel {
        val params = RequestListModel(route = "predanie.ru/api/mobile/v1/author/", letter = letter)
        return api.getAuthorsList(params)
    }
    suspend fun getGlobalSearchList(q: String, offset: Int = 0, limit: Int = 100): ResponseGlobalSearchListModel {
        val params = RequestListModel(route = "predanie.ru/api/mobile/v1/search/", q = q, offset = offset, limit = limit)
        return api.getGlobalSearchList(params)
    }
    suspend fun getCategoryItemsList(categoryId: Int, offset: Int = 0, limit: Int = 100): ResponseItemsListModel {
        val params = RequestListModel(route = "predanie.ru/api/mobile/v1/compositions/", id_category = categoryId, offset = offset, limit = limit)
        return api.getItemsList(params)
    }
    suspend fun getCatalogList(): ResponseCatalogModel {
        val params = RequestListModel(route = "predanie.ru/api/mobile/v1/catalog/")
        return api.getCatalogList(params)
    }
    suspend fun getVideoList(): ResponseVideoListModel {
        val params = RequestVideoListModel(route = "nasledie-college.ru:1337/uploads/predanie/strapi.json")
        return api.getVideoList(params)
    }
    suspend fun getBlogList(page: String, perPage: String): List<ResponceBlogListModel> {
        val params = RequestBlogListModel(route = "blog.predanie.ru/wp-json/wp/v2/posts", page, perPage)
        return api.getBlogList(params)
    }
}