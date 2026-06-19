package org.mihajlo1612.showtime.navigation

import kotlinx.serialization.Serializable

object Screen {
    const val LANDING = "landing"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val MAIN = "main"
    const val CATALOG = "catalog"
    const val FAVORITES = "favorites"
    const val WATCHLIST = "watchlist"
    const val QUIZ = "quiz"
    const val PROFILE = "profile"
//    const val HOME = "home"
}

@Serializable
data class MovieDetailRoute(val imdbId: String)