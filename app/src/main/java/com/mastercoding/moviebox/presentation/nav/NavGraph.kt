package com.mastercoding.moviebox.presentation.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mastercoding.moviebox.presentation.detail.DetailRoute
import com.mastercoding.moviebox.presentation.favorites.FavoritesRoute
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
            FavoritesRoute(
                onMovieClick = { id -> navController.navigate(Route.MovieDetail.create(id)) },
                onBack = { navController.popBackStack() },
            )
        }

        composable(
            route = Route.MovieDetail.path,
            arguments = listOf(navArgument("id") { type = NavType.IntType }),
        ) {
            DetailRoute(onBack = { navController.popBackStack() })
        }
    }

}
