package studio.vadim.predanie.presentation.navigation

import studio.vadim.predanie.R
sealed class NavigationItem(var route: String, var icon: Int, var title: String) {
    object Splash : NavigationItem("SplashScreen", R.drawable.ic_action_blog, "Ленты")
    object Home : NavigationItem("MainScreen", R.drawable.ic_action_blog, "Ленты")
    object Music : NavigationItem("music", R.drawable.ic_action_meeting, "Каталог")
    object Movies : NavigationItem("movies", R.drawable.ic_action_user, "Поиск")
    object Books : NavigationItem("books", R.drawable.ic_action_records, "Мое")
    object Profile : NavigationItem("profile", R.drawable.ic_action_user, "Фонд")
    object Author : NavigationItem("AuthorScreen/{authorId}", R.drawable.ic_action_user, "Автор")
    object Item : NavigationItem("ItemScreen/{itemId}", R.drawable.ic_action_blog, "Произведение")
}