package com.mazenrashed.github

import androidx.room.Room
import com.mazen.wash.data.local.database.Database
import com.mazenrashed.github.data.DataManager
import com.mazenrashed.github.data.network.ServiceGenerator
import com.mazenrashed.github.data.reposotories.RepoRepository
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { DataManager() }
    single { ServiceGenerator() }
    single { RepoRepository() }
    viewModel { RepositoriesViewModel() }

    single { Room.databaseBuilder(get(), Database::class.java, "mDatabase").build() }
    single { get<Database>().repoDao() }
}