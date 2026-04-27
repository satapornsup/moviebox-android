package com.mastercoding.moviebox.presentation.nav

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mastercoding.moviebox.presentation.home.HomeRoute

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Route.Home.path
    ) {
        composable(Route.Home.path) {
            HomeRoute(
                onMovieClick = { id -> navController.navigate(Route.MovieDetail.create(id)) },
                onOpenFavorites = {
                    navController.navigate(Route.Favorites.path)
                }
            )
        }

        composable(Route.Favorites.path) {
            Text("Favorites — coming soon")
        }

        composable(
            route = Route.MovieDetail.path,
            arguments = listOf(navArgument("id") { type = NavType.IntType }),
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: 0
            Text("Detail $id — coming soon")
        }
    }

}
