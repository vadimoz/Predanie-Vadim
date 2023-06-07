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
import studio.vadim.predanie.domain.models.api.lists.PredanieApiRequestListModel
import studio.vadim.predanie.domain.models.api.lists.PredanieApiResponseListModel
import studio.vadim.predanie.domain.ApiConnection

class ApiRepositoryImpl : ApiConnection {

    @OptIn(InternalAPI::class)
    override suspend fun getItemsList(request: PredanieApiRequestListModel): PredanieApiResponseListModel {

        val client = HttpClient(Android) {
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
        }.get{
                url(){
                        host = request.route.getRoute().BASE_URL
                        protocol = URLProtocol.HTTPS
                        parameters.append("limit", request.limit.toString())
                        parameters.append("offset", request.offset.toString())
                        parameters.append("type", request.library)
                }
            }
        return client.body()
    }
}