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
import studio.vadim.predanie.domain.models.api.lists.PredanieApiResponseListModel
import studio.vadim.predanie.domain.ApiConnection
import studio.vadim.predanie.domain.models.api.lists.PredanieApiResponseAuthorsListModel

class ApiImpl : ApiConnection {

    @OptIn(InternalAPI::class)
    override suspend fun getItemsList(request: RequestListModel): PredanieApiResponseListModel {
        val client = createHttpClient()
        return client.get {
            url() {
                host = request.route
                protocol = URLProtocol.HTTPS
                //TODO: Промапить RequestListModel

                parameters.append("limit", request.limit.toString())
                parameters.append("offset", request.offset.toString())
                parameters.append("type", request.type)
            }
        }.body()
    }

    override suspend fun getAuthorsList(request: RequestListModel): PredanieApiResponseAuthorsListModel {
        val client = createHttpClient()
        return client.get {
            url() {
                host = request.route
                protocol = URLProtocol.HTTPS
                //TODO: Промапить RequestListModel

                parameters.append("limit", request.limit.toString())
                parameters.append("offset", request.offset.toString())
                parameters.append("type", request.type)
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