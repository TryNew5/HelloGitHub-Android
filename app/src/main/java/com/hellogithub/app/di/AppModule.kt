package com.hellogithub.app.di

import com.hellogithub.app.data.repository.HomeRepository
import com.hellogithub.app.data.repository.PeriodicalRepository
import com.hellogithub.app.data.repository.RepoDetailRepository
import com.hellogithub.app.data.repository.SearchRepository
import com.hellogithub.app.ui.feed.FeedViewModel
import com.hellogithub.app.ui.detail.DetailViewModel
import com.hellogithub.app.ui.search.SearchViewModel
import com.hellogithub.app.ui.periodical.PeriodicalViewModel
import com.hellogithub.app.ui.settings.ThemeViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    // Repositories
    single { HomeRepository(get()) }
    single { RepoDetailRepository(get()) }
    single { SearchRepository(get()) }
    single { PeriodicalRepository(get()) }

    // ViewModels
    viewModel { FeedViewModel(get()) }
    viewModel { DetailViewModel(get()) }
    viewModel { SearchViewModel(get()) }
    viewModel { PeriodicalViewModel(get()) }
    viewModel { ThemeViewModel(androidContext()) }
}
