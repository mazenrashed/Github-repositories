package com.mazenrashed.github

import androidx.lifecycle.ViewModel
import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import com.mazenrashed.github.data.DataManager
import com.mazenrashed.github.data.model.Repo
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.reactivestreams.Subscription
import java.net.ConnectException
import java.net.UnknownHostException

class RepositoriesViewModel : ViewModel(), KoinComponent {

    private val dataManager: DataManager by inject()
    private val bag = CompositeDisposable()

    val repositories = BehaviorRelay.createDefault(ArrayList<Repo>())
    val isLoading = BehaviorRelay.createDefault(false)
    val toastMessages: PublishRelay<String> = PublishRelay.create()

    init {
        loadRepos()
    }

    fun loadRepos() {
        dataManager
            .getRepositories()
            .subscribeOn(Schedulers.io())
            .doOnSubscribe(::onLoadSubscribe)
            .doOnNext(::onLoadSuccess)
            .doOnComplete(::onLoadCompleted)
            .doOnError(::onLoadError)
            .subscribe({},{it.printStackTrace()})
            .addTo(bag)
    }

    private fun onLoadCompleted(){
        isLoading.accept(false)
    }


    private fun onLoadSuccess(repos: List<Repo>) {
        repositories.accept(ArrayList(repos))
    }

    private fun onLoadError(t: Throwable) {
        isLoading.accept(false)
        if (t is ConnectException || t is UnknownHostException) {
            toastMessages.accept("Check connection")
        }
    }

    private fun onLoadSubscribe(subscription: Subscription) {
        isLoading.accept(true)
    }

    override fun onCleared() {
        super.onCleared()
        bag.clear()
    }
}