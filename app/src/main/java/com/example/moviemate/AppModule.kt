package com.example.moviemate

import HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

val appModule = module {
    single { NetworkModule.createRetrofit("http://www.omdbapi.com/") }
    single { get<Retrofit>().create(ApiService::class.java) }
    single { MediaRepository(get(), "74218f38") }


    viewModel { HomeViewModel(get()) }
    viewModel { DetailsViewModel(get()) }
}
