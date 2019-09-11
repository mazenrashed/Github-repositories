package com.mazenrashed.github.data.network

import com.mazenrashed.github.data.model.Repo
import io.reactivex.Single
import retrofit2.http.GET

interface EndPoints{

    @GET("/users/mazenrashed/repos")
    fun getRepositories() : Single<ArrayList<Repo>>
}