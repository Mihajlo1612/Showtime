package org.mihajlo1612.showtime.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.mihajlo1612.showtime.data.local.datastore.AuthDataStore
import org.mihajlo1612.showtime.data.remote.api.FavoritesApi
import org.mihajlo1612.showtime.data.remote.api.MovieApi
import org.mihajlo1612.showtime.data.remote.api.QuizApi
import org.mihajlo1612.showtime.data.remote.api.WatchlistApi
import org.mihajlo1612.showtime.data.repository.AuthRepositoryImpl
import org.mihajlo1612.showtime.domain.repository.FavoritesRepository
import org.mihajlo1612.showtime.data.repository.FavoritesRepositoryImpl
import org.mihajlo1612.showtime.data.repository.MovieRepositoryImpl
import org.mihajlo1612.showtime.data.repository.QuizRepositoryImpl
import org.mihajlo1612.showtime.data.repository.WatchlistRepositoryImpl
import org.mihajlo1612.showtime.domain.repository.AuthRepository
import org.mihajlo1612.showtime.domain.repository.MovieRepository
import org.mihajlo1612.showtime.domain.repository.QuizRepository
import org.mihajlo1612.showtime.domain.repository.WatchlistRepository
import org.mihajlo1612.showtime.ui.auth.AppViewModel
import org.mihajlo1612.showtime.ui.auth.login.LoginViewModel
import org.mihajlo1612.showtime.ui.auth.register.RegisterViewModel
import org.mihajlo1612.showtime.ui.catalog.CatalogViewModel
import org.mihajlo1612.showtime.ui.detail.DetailViewModel
import org.mihajlo1612.showtime.ui.favorites.FavoritesViewModel
import org.mihajlo1612.showtime.ui.profile.ProfileViewModel
import org.mihajlo1612.showtime.ui.quiz.QuizViewModel
import org.mihajlo1612.showtime.ui.watchlist.WatchlistViewModel

val appModule = module {
    includes(networkModule)

    single { AuthDataStore(get()) }

    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }

    single { MovieApi(get()) }
    single<MovieRepository> { MovieRepositoryImpl(get(), get(), get(), get()) }

    single { QuizApi(get()) }
    single<QuizRepository> { QuizRepositoryImpl(get(), get(), get(), get(), get()) }

    single { FavoritesApi(get()) }
    single<FavoritesRepository> { FavoritesRepositoryImpl(get(), get(), get(), get()) }

    single { WatchlistApi(get()) }
    single<WatchlistRepository> { WatchlistRepositoryImpl(get(), get(), get(), get()) }

    viewModel { WatchlistViewModel(get()) }
    viewModel { (imdbId: String) -> DetailViewModel(get(), get(), get(), imdbId) }
    viewModel { FavoritesViewModel(get()) }
    viewModel { QuizViewModel(get()) }
    viewModel { CatalogViewModel(get()) }
    viewModel { AppViewModel(get(), get(), get()) }
    viewModel { LoginViewModel(get()) }
    viewModel { RegisterViewModel(get()) }
    viewModel { (imdbId: String) -> DetailViewModel(get(), get(), get(),  imdbId) }
    viewModel { ProfileViewModel(get(), get(), get(), get()) }
}