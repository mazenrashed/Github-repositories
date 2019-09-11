package com.mazenrashed.github.data

import com.mazenrashed.github.data.model.Repo
import com.mazenrashed.github.data.reposotories.RepoRepository
import io.reactivex.Flowable
import org.koin.core.KoinComponent
import org.koin.core.inject

class DataManager : KoinComponent {

    private val repoRepository : RepoRepository by inject()

    fun getRepositories(): Flowable<List<Repo>> {
        return repoRepository.getRepositories()
    }

}
