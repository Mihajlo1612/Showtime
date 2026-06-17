package org.mihajlo1612.showtime.di

import org.koin.dsl.module
import org.mihajlo1612.showtime.data.local.datastore.AuthDataStore
import org.mihajlo1612.showtime.data.repository.AuthRepositoryImpl
import org.mihajlo1612.showtime.domain.repository.AuthRepository

val appModule = module {
    includes(networkModule)

    single { AuthDataStore(get()) }

    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
}