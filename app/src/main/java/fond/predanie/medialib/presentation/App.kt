package fund.predanie.medialib.presentation

import android.app.Application
import io.appmetrica.analytics.AppMetrica
import io.appmetrica.analytics.AppMetricaConfig
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import fund.predanie.medialib.presentation.di.appApi

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        val config = AppMetricaConfig.newConfigBuilder("da730b20-9885-4c79-82c7-8b101ed5740a").build()
        AppMetrica.activate(this, config)

        startKoin{
            androidLogger()
            androidContext(this@App)
            modules(appApi)
        }
    }
}