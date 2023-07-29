package studio.vadim.predanie.presentation.navigation

import studio.vadim.predanie.R
sealed class NavigationItem(var route: String, var icon: Int, var title: String) {
    object Splash : NavigationItem("SplashScreen", R.drawable.ic_action_blog, "СплэшСкрин")
    object QuickSplash : NavigationItem("QuickScreen", R.drawable.ic_action_blog, "СплэшСкрин")
    object Home : NavigationItem("MainScreen", R.drawable.ic_home, "Ленты")
    object Catalog : NavigationItem("CatalogScreen", R.drawable.ic_catalog, "Каталог")
    object Search : NavigationItem("SearchScreen/{query}", R.drawable.ic_search, "Поиск")
    object CatalogItems : NavigationItem("CatalogItemsScreen/{catalogId}/{catalogName}", R.drawable.ic_action_records, "Элементы каталога")
    object Profile : NavigationItem("ProfileScreen/{play}", R.drawable.ic_action_user, "Моё")
    object Author : NavigationItem("AuthorScreen/{authorId}", R.drawable.ic_action_user, "Автор")
    object Item : NavigationItem("ItemScreen/{itemId}", R.drawable.ic_action_blog, "Произведение")
    object Fund : NavigationItem("FundScreen", R.drawable.predanie, "Фонд")
    object Player : NavigationItem("PlayerScreen", R.drawable.predanie, "Плеер")
    object OfflineItem : NavigationItem("OfflineItemScreen/{itemId}", R.drawable.predanie, "Плеер")
}