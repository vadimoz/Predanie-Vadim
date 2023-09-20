package studio.vadim.predanie.data.api

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.http.URLProtocol
import io.ktor.serialization.gson.gson
import io.ktor.util.InternalAPI
import kotlinx.coroutines.delay
import studio.vadim.predanie.domain.ApiConnection
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

class ApiImpl : ApiConnection {
    override suspend fun getAuthor(request: RequestAuthorModel): ResponseAuthorModel {
        val client = createHttpClient()

        while (true) {
            try {
                return client.get {
                    url() {
                        host = request.route
                        protocol = URLProtocol.HTTPS

                        parameters.append("author_id", request.authorId.toString())
                    }
                }.body()
            } catch (e: Throwable) {
                //Log.d("getAuthor", e.message.toString())
                delay(10000L)
            }
        }
    }

    override suspend fun getItem(request: RequestItemModel): ResponseItemModel {
        val client = createHttpClient()

        while (true) {
            try {
                return client.get {
                    url() {
                        host = request.route
                        protocol = URLProtocol.HTTPS

                        parameters.append("composition_id", request.compositionId.toString())
                    }
                }.body()
            } catch (e: Throwable) {
                delay(10000L)
            }
        }
    }

    override suspend fun getPost(request: RequestPostModel): ResponsePostModel {
        val client = createHttpClient()

        while (true) {
            try {
                return client.get {
                    url() {
                        host = "${request.route}/${request.postId}/"
                        protocol = URLProtocol.HTTPS

                        parameters.append("_embed", "true")
                    }
                }.body()
            } catch (e: Throwable) {
                delay(10000L)
            }
        }
    }

    override suspend fun getCatalogList(request: RequestListModel): ResponseCatalogModel {
        val client = createHttpClient()

        while (true) {
            try {
                return client.get {
                    url() {
                        host = request.route
                        protocol = URLProtocol.HTTPS
                        //TODO: Промапить RequestListModel
                    }
                }.body()
            } catch (e: Throwable) {
                //Log.d("getCatalogList", e.message.toString())
                delay(10000L)
            }
        }
    }

    @OptIn(InternalAPI::class)
    override suspend fun getItemsList(request: RequestListModel): ResponseItemsListModel {
        val client = createHttpClient()
        while (true) {
            try {
                return client.get {
                    url() {
                        host = request.route
                        protocol = URLProtocol.HTTPS
                        //TODO: Промапить RequestListModel

                        parameters.append("limit", request.limit.toString())
                        parameters.append("offset", request.offset.toString())
                        parameters.append("type", request.type)
                        parameters.append("id_category", request.id_category.toString())
                    }
                }.body()
            } catch (e: Throwable) {
                //Log.d("getItemsList", e.message.toString())
                delay(10000L)
            }
        }
    }

    override suspend fun getGlobalSearchList(request: RequestListModel): ResponseGlobalSearchListModel {
        val client = createHttpClient()
        while (true) {
            try {
                return client.get {
                    url() {
                        host = request.route
                        protocol = URLProtocol.HTTPS
                        //TODO: Промапить RequestListModel

                        parameters.append("limit", request.limit.toString())
                        parameters.append("offset", request.offset.toString())
                        parameters.append("q", request.q)
                    }
                }.body()
            } catch (e: Throwable) {
                //Log.d("getGlobalSearchList", e.message.toString())
                delay(10000L)
            }
        }
    }

    override suspend fun getVideoList(request: RequestVideoListModel): ResponseVideoListModel {
        val client = createHttpClient()
        while (true) {
            try {
                return client.get {
                    url() {
                        host = request.route
                        protocol = URLProtocol.HTTP
                    }
                }.body()
            } catch (e: Throwable) {
                delay(100000L)
            }
        }
    }

    override suspend fun getBlogList(request: RequestBlogListModel): List<ResponceBlogListModel> {
        val client = createHttpClient()
        while (true) {
            try {
                return client.get {
                    url() {
                        host = request.route
                        protocol = URLProtocol.HTTPS

                        parameters.append("page", request.page)
                        parameters.append("per_page", request.per_page)
                        parameters.append("_embed", "true")
                    }
                }.body()
            } catch (e: Throwable) {
                delay(10000L)
            }
        }
    }

    override suspend fun getAuthorsList(request: RequestListModel): ResponseAuthorsListModel {
        val client = createHttpClient()
        while (true) {
            try {
                return client.get {
                    url() {
                        host = request.route
                        protocol = URLProtocol.HTTPS

                        parameters.append("limit", request.limit.toString())
                        parameters.append("offset", request.offset.toString())
                        parameters.append("type", request.type)
                        parameters.append("search", request.search)
                        parameters.append("letter", request.letter)
                    }
                }.body()
            } catch (e: Throwable) {
                //Log.d("getAuthorsList", e.message.toString())
                delay(10000L)
            }
        }
    }

    private fun createHttpClient(): HttpClient {
        return HttpClient(Android) {
            // Logging
            install(Logging) {
                level = LogLevel.ALL
            }
            // JSON
            install(ContentNegotiation) {
                gson()
            }
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.ALL
            }
            // Timeout
            install(HttpTimeout) {
                requestTimeoutMillis = 15000L
                connectTimeoutMillis = 15000L
                socketTimeoutMillis = 15000L
            }
        }
    }
}