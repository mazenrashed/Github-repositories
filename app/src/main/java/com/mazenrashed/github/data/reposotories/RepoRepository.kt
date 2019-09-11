package com.mazenrashed.github.data.reposotories

import com.mazen.wash.data.local.database.Database
import com.mazenrashed.github.data.model.Repo
import com.mazenrashed.github.data.network.ServiceGenerator
import io.reactivex.Flowable
import io.reactivex.Single
import org.koin.core.KoinComponent
import org.koin.core.inject

class RepoRepository : KoinComponent {

    private val serviceGenerator: ServiceGenerator by inject()
    private val database: Database by inject()


    fun getRepositories(): Flowable<List<Repo>> {
        return Single
            .merge(
                database.repoDao().fetchAllRepositories(),
                getReposFromApi()
            )
    }

    private fun getReposFromApi(): Single<ArrayList<Repo>> {
        return serviceGenerator
            .getRestService()
            .getRepositories()
            .doOnSuccess(::insertRepos)
    }

    private fun insertRepos(repos: ArrayList<Repo>) {
         database
            .repoDao()
            .insertRepositories(repos)
    }
}