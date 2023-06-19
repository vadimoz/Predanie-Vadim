package studio.vadim.predanie.presentation

import studio.vadim.predanie.R

sealed class NavigationItem(var route: String, var icon: Int, var title: String) {
    object Home : NavigationItem("MainScreen", R.drawable.ic_action_blog, "Ленты")
    object Music : NavigationItem("music", R.drawable.ic_action_meeting, "Каталог")
    object Movies : NavigationItem("movies", R.drawable.ic_action_user, "Поиск")
    object Books : NavigationItem("books", R.drawable.ic_action_records, "Мое")
    object Profile : NavigationItem("profile", R.drawable.ic_action_user, "Фонд")
}