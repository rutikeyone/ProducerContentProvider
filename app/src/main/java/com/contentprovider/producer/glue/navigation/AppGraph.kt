package com.contentprovider.producer.glue.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.contentprovider.core.presentation.assistedViewModel
import com.contentprovider.humans.presentation.add.AddHumanPage
import com.contentprovider.humans.presentation.update.HumanUpdatePage
import com.contentprovider.humans.presentation.list.HumanListPage
import com.contentprovider.humans.presentation.update.viewmodel.HumanUpdateViewModel
import com.contentprovider.producer.glue.humans.requiredHumanUpdateViewModelFactory

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

        composable(
            NavigationItem.Update.route,
            arguments = listOf(
                navArgument(NavigationItem.Update.ARG_ID) {
                    type = NavType.StringType
                }
            )
        ) {
            val humanStringId =
                requireNotNull(it.arguments?.getString(NavigationItem.Update.ARG_ID))
            val humanLongId = requireNotNull(humanStringId.toLongOrNull())

            val factory = requiredHumanUpdateViewModelFactory()

            val viewModel = assistedViewModel<HumanUpdateViewModel> {
                factory.create(humanLongId)
            }

            HumanUpdatePage(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}