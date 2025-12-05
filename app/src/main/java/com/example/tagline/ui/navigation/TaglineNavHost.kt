package com.example.tagline.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.tagline.ui.screens.auth.ForgotPasswordScreen
import com.example.tagline.ui.screens.auth.LoginScreen
import com.example.tagline.ui.screens.auth.RegisterScreen
import com.example.tagline.ui.screens.details.MovieDetailsScreen
import com.example.tagline.ui.screens.details.TvDetailsScreen
import com.example.tagline.ui.screens.mylist.MyListScreen
import com.example.tagline.ui.screens.search.SearchScreen

@Composable
fun TaglineNavHost(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Auth screens
        composable(NavRoutes.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(NavRoutes.Register.route)
                },
                onNavigateToForgotPassword = {
                    navController.navigate(NavRoutes.ForgotPassword.route)
                },
                onLoginSuccess = {
                    navController.navigate(NavRoutes.Search.route) {
                        popUpTo(NavRoutes.Login.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(NavRoutes.Register.route) {
            RegisterScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    navController.navigate(NavRoutes.Search.route) {
                        popUpTo(NavRoutes.Login.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(NavRoutes.ForgotPassword.route) {
            ForgotPasswordScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        // Main screens
        composable(NavRoutes.Search.route) {
            SearchScreen(
                onNavigateToMovieDetails = { movieId ->
                    navController.navigate(NavRoutes.MovieDetails.createRoute(movieId))
                },
                onNavigateToTvDetails = { seriesId ->
                    navController.navigate(NavRoutes.TvDetails.createRoute(seriesId))
                },
                onNavigateToMyList = {
                    navController.navigate(NavRoutes.MyList.route)
                },
                onLogout = {
                    navController.navigate(NavRoutes.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        
        composable(NavRoutes.MyList.route) {
            MyListScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToMovieDetails = { movieId ->
                    navController.navigate(NavRoutes.MovieDetails.createRoute(movieId))
                },
                onNavigateToTvDetails = { seriesId ->
                    navController.navigate(NavRoutes.TvDetails.createRoute(seriesId))
                }
            )
        }
        
        // Detail screens
        composable(
            route = NavRoutes.MovieDetails.route,
            arguments = listOf(
                navArgument(NavRoutes.MOVIE_ID_ARG) { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val movieId = backStackEntry.arguments?.getInt(NavRoutes.MOVIE_ID_ARG) ?: return@composable
            MovieDetailsScreen(
                movieId = movieId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(
            route = NavRoutes.TvDetails.route,
            arguments = listOf(
                navArgument(NavRoutes.SERIES_ID_ARG) { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val seriesId = backStackEntry.arguments?.getInt(NavRoutes.SERIES_ID_ARG) ?: return@composable
            TvDetailsScreen(
                seriesId = seriesId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

