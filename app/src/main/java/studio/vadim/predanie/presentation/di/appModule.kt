package studio.vadim.predanie.presentation.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import studio.vadim.predanie.data.ApiImpl
import studio.vadim.predanie.domain.ApiConnection
import studio.vadim.predanie.domain.usecases.showItems.GetItems
import studio.vadim.predanie.domain.usecases.showLists.GetLists
import studio.vadim.predanie.presentation.MainViewModel

val appModule = module {
    single<ApiConnection> { ApiImpl() }

    factory {
        GetLists(get())
    }
    factory {
        GetItems(get())
    }


    viewModel { MainViewModel(get(), get()) }
}