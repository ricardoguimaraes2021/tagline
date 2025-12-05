package com.example.tagline.ui.navigation

sealed class NavRoutes(val route: String) {
    
    // Auth routes
    object Login : NavRoutes("login")
    object Register : NavRoutes("register")
    object ForgotPassword : NavRoutes("forgot_password")
    
    // Main routes
    object Search : NavRoutes("search")
    object MyList : NavRoutes("my_list")
    
    // Detail routes with arguments
    object MovieDetails : NavRoutes("movie_details/{movieId}") {
        fun createRoute(movieId: Int) = "movie_details/$movieId"
    }
    
    object TvDetails : NavRoutes("tv_details/{seriesId}") {
        fun createRoute(seriesId: Int) = "tv_details/$seriesId"
    }
    
    companion object {
        const val MOVIE_ID_ARG = "movieId"
        const val SERIES_ID_ARG = "seriesId"
    }
}

