package com.contentprovider.producer.glue.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.contentprovider.humans.presentation.add.AddHumanPage
import com.contentprovider.humans.presentation.update.HumanUpdatePage
import com.contentprovider.humans.presentation.list.HumanListPage

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = NavigationItem.List.route) {
        composable(NavigationItem.List.route) {
            HumanListPage(
                navigateToAddHuman = {
                    navController.navigate(NavigationItem.Add.route)
                },
                onClickItem = {
                    val route = "${Screen.Update.name}/${it.id}"
                    navController.navigate(route)
                }
            )
        }

        composable(NavigationItem.Add.route) {
            AddHumanPage(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(NavigationItem.Update.route) {
            HumanUpdatePage()
        }
    }
}