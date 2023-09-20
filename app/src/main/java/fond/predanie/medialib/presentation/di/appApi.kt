package fund.predanie.medialib.presentation.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import fund.predanie.medialib.data.api.ApiImpl
import fund.predanie.medialib.domain.ApiConnection
import fund.predanie.medialib.domain.usecases.showItems.GetItems
import fund.predanie.medialib.domain.usecases.showLists.GetLists
import fund.predanie.medialib.presentation.MainViewModel

val appApi = module {
    single<ApiConnection> { ApiImpl() }

    factory {
        GetLists(get())
    }
    factory {
        GetItems(get())
    }

    viewModel { MainViewModel(get(), get()) }
}