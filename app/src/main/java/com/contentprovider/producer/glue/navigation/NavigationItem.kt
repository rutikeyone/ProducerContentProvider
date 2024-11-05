package com.contentprovider.producer.glue.navigation

enum class Screen {
    List,
    Add,
    Update,
}

sealed class NavigationItem(val route: String) {
    data object List : NavigationItem(Screen.List.name)
    data object Add: NavigationItem(Screen.Add.name)
    data object Update : NavigationItem("${Screen.Update}/{id}")
}