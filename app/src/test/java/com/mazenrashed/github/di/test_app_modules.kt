package com.mazenrashed.github.di

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.mazenrashed.github.data.DataManager
import com.mazenrashed.github.data.model.Repo
import com.mazenrashed.github.ui.RepositoriesViewModel
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import io.reactivex.Flowable
import org.koin.dsl.module

val testAppModule = module {
    single { provideMockedDataManager() }

    single { RepositoriesViewModel() }

}

fun provideMockedDataManager() : DataManager{


    return mock<DataManager> {
        on { getRepositories() } doReturn Flowable.unsafeCreate {
            it.onNext(getGsonResponse())
        }
    }
}

fun getGsonResponse(): List<Repo> {
    val fileHelper = FileHelper()
    val file = fileHelper.getFileFromPath("repo_response.json")
    val itemType = object : TypeToken<List<Repo>>() {}.type

    return GsonBuilder().create().fromJson(file.readText(), itemType)
}

