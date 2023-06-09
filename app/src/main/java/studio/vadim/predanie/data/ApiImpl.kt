package studio.vadim.predanie.data

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.observer.ResponseObserver
import io.ktor.client.request.get
import io.ktor.client.statement.request
import io.ktor.http.URLProtocol
import io.ktor.serialization.gson.gson
import io.ktor.util.InternalAPI
import studio.vadim.predanie.domain.models.api.lists.RequestListModel
import studio.vadim.predanie.domain.models.api.lists.ResponseItemsListModel
import studio.vadim.predanie.domain.ApiConnection
import studio.vadim.predanie.domain.models.api.items.RequestAuthorModel
import studio.vadim.predanie.domain.models.api.items.RequestItemModel
import studio.vadim.predanie.domain.models.api.items.ResponseAuthorModel
import studio.vadim.predanie.domain.models.api.items.ResponseItemModel
import studio.vadim.predanie.domain.models.api.lists.ResponseAuthorsListModel
import studio.vadim.predanie.domain.models.api.lists.ResponseCatalogModel
import studio.vadim.predanie.domain.models.api.lists.ResponseGlobalSearchListModel

class ApiImpl : ApiConnection {

    override suspend fun getAuthor(request: RequestAuthorModel): ResponseAuthorModel {
        val client = createHttpClient()
        return client.get {
            url() {
                host = request.route
                protocol = URLProtocol.HTTPS
                //TODO: Промапить RequestListModel

                parameters.append("author_id", request.authorId.toString())
            }
        }.body()
    }
    override suspend fun getItem(request: RequestItemModel): ResponseItemModel {
        val client = createHttpClient()
        return client.get {
            url() {
                host = request.route
                protocol = URLProtocol.HTTPS
                //TODO: Промапить RequestListModel

                parameters.append("composition_id", request.compositionId.toString())
            }
        }.body()
    }
    override suspend fun getCatalogList(request: RequestListModel): ResponseCatalogModel {
        val client = createHttpClient()
        return client.get {
            url() {
                host = request.route
                protocol = URLProtocol.HTTPS
                //TODO: Промапить RequestListModel
            }
        }.body()
    }
    @OptIn(InternalAPI::class)
    override suspend fun getItemsList(request: RequestListModel): ResponseItemsListModel {
        val client = createHttpClient()
        return client.get {
            url() {
                host = request.route
                protocol = URLProtocol.HTTPS
                //TODO: Промапить RequestListModel

                parameters.append("limit", request.limit.toString())
                parameters.append("offset", request.offset.toString())
                parameters.append("id_category", request.id_category.toString())
            }
        }.body()
    }

    override suspend fun getGlobalSearchList(request: RequestListModel): ResponseGlobalSearchListModel {
        val client = createHttpClient()
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
    }
    override suspend fun getAuthorsList(request: RequestListModel): ResponseAuthorsListModel {
        val client = createHttpClient()
        return client.get {
            url() {
                host = request.route
                protocol = URLProtocol.HTTPS
                //TODO: Промапить RequestListModel

                parameters.append("limit", request.limit.toString())
                parameters.append("offset", request.offset.toString())
                parameters.append("type", request.type)
                parameters.append("search", request.search)
                parameters.append("letter", request.letter)
            }
        }.body()
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

            install(ResponseObserver) {
                onResponse { response ->
                    Log.d("HTTP status:", "${response.request.url}")
                }
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