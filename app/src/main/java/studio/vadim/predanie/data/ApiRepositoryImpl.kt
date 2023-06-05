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
import io.ktor.http.URLProtocol
import io.ktor.serialization.gson.gson
import io.ktor.util.InternalAPI
import studio.vadim.predanie.data.models.PredanieApiRequestListModel
import studio.vadim.predanie.data.models.PredanieApiResponseListModel
import studio.vadim.predanie.data.models.PredanieApiRoutes
import studio.vadim.predanie.domain.ApiConnection
import studio.vadim.predanie.domain.models.PredanieListModel

class ApiRepositoryImpl : ApiConnection {

    @OptIn(InternalAPI::class)
    override suspend fun getItemsList(): PredanieApiResponseListModel {
        val requestParams = PredanieApiRequestListModel(catalog = "popular", limit = 20, offset = 20, type = "audio,music")

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
                    Log.d("HTTP status:", "${response.status.value}")
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
                        host = PredanieApiRoutes.BASE_URL
                        protocol = URLProtocol.HTTPS
                        parameters.append("catalog", requestParams.catalog)
                        parameters.append("limit", requestParams.limit.toString())
                        parameters.append("offset", requestParams.offset.toString())
                        parameters.append("type", requestParams.type)
                }
            }
        Log.d("CONTENT", client.body<PredanieApiResponseListModel>().toString())
        return client.body()
    }
}