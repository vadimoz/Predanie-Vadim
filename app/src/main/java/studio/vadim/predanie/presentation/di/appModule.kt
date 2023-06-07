package studio.vadim.predanie.presentation.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import studio.vadim.predanie.data.ApiRepositoryImpl
import studio.vadim.predanie.domain.ApiConnection
import studio.vadim.predanie.domain.models.api.lists.PredanieApiRequestListModel
import studio.vadim.predanie.domain.models.api.lists.PredanieNewListImplApi
import studio.vadim.predanie.domain.models.api.lists.PredaniePopularListImplApi
import studio.vadim.predanie.domain.usecases.NewItemsToList
import studio.vadim.predanie.domain.usecases.PopularItemsToList
import studio.vadim.predanie.presentation.MainViewModel

val appModule = module {
    single<ApiConnection> { ApiRepositoryImpl() }

    factory {
        PopularItemsToList(get(), PredanieApiRequestListModel(PredaniePopularListImplApi()))
    }

    factory {
        NewItemsToList(get(), PredanieApiRequestListModel(PredanieNewListImplApi()))
    }

    viewModel { MainViewModel(get(), get()) }
}