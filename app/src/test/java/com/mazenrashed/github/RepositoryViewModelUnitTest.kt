package com.mazenrashed.github

import com.mazenrashed.github.data.DataManager
import com.mazenrashed.github.data.model.Repo
import com.mazenrashed.github.di.FileHelper
import com.mazenrashed.github.di.getGsonResponse
import com.mazenrashed.github.di.testAppModule
import com.mazenrashed.github.ui.RepositoriesViewModel
import com.mazenrashed.github.utils.TestHelper.mockObserver
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.Flowable
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.koinApplication
import org.koin.test.KoinTest
import org.koin.test.check.checkModules
import org.koin.test.inject
import org.koin.test.mock.declareMock
import org.mockito.ArgumentMatchers
import org.mockito.Matchers
import org.mockito.Mockito
import java.net.UnknownHostException

class RepositoryViewModelUnitTest : KoinTest {

    private val bag = CompositeDisposable()

    @Before
    fun before(){
        startKoin { modules(testAppModule) }
    }

    @Before
    fun setupRXTest() {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
    }

    @Test
    fun `checking modules`() {
        koinApplication { modules(testAppModule) }.checkModules()
    }

    @Test
    fun `test File exists in the resources`() {
        val file = FileHelper().getFileFromPath("repo_response.json")
        assert(file.exists())
        assert(file.readText().length > 10)

    }

    @Test
    fun `test the json file content`() {
        val repos = getGsonResponse()

        assertNotNull(repos)
        assertEquals(7, repos.size)
    }

    @Test
    fun `test load success`(){
        val viewModel : RepositoriesViewModel by inject()
        val dataManager : DataManager by inject()

        var reposObserver = mockObserver<ArrayList<Repo>>()
        var loadingStateObserver = mockObserver<Boolean>()
        val toastMessagesObserver = mockObserver<String>()

        viewModel.repositories.subscribe(reposObserver)
        viewModel.isLoading.subscribe(loadingStateObserver)
        viewModel.toastMessages.subscribe(toastMessagesObserver)

        viewModel.loadRepos()

        verify(dataManager, times(2)).getRepositories()
        verify(reposObserver, times(3)).onNext(ArgumentMatchers.anyList<Repo>() as ArrayList<Repo>)
        verify(toastMessagesObserver, Mockito.never()).onNext(ArgumentMatchers.anyString())
        verify(loadingStateObserver, Mockito.never()).onError(ArgumentMatchers.any(Throwable::class.java))
        verify(loadingStateObserver, Mockito.never()).onComplete()
        verify(toastMessagesObserver, Mockito.never()).onError(ArgumentMatchers.any(Throwable::class.java))
        verify(toastMessagesObserver, Mockito.never()).onComplete()
        verify(reposObserver, Mockito.never()).onError(ArgumentMatchers.any(Throwable::class.java))
        verify(reposObserver, Mockito.never()).onComplete()
    }

    @Test
    fun `test connection issue`(){
        val viewModel : RepositoriesViewModel by inject()

        val toastMessagesObserver = mockObserver<String>()
        val loadingStateObserver = mockObserver<Boolean>()
        declareMock<DataManager> {
            given(this.getRepositories()).willReturn(Flowable.unsafeCreate { it.onError(UnknownHostException()) })
        }

        viewModel.toastMessages.subscribe(toastMessagesObserver)
        viewModel.isLoading.subscribe(loadingStateObserver)

        viewModel.loadRepos()

        verify(loadingStateObserver, times(1)).onNext(true)
        verify(loadingStateObserver, times(2)).onNext(false)
        verify(toastMessagesObserver, times(1)).onNext("Check connection")
        verify(loadingStateObserver, Mockito.never()).onError(ArgumentMatchers.any(Throwable::class.java))
        verify(loadingStateObserver, Mockito.never()).onComplete()
        verify(toastMessagesObserver, Mockito.never()).onError(ArgumentMatchers.any(Throwable::class.java))
        verify(toastMessagesObserver, Mockito.never()).onComplete()
    }

    @Test
    fun `test others issues`(){
        val viewModel : RepositoriesViewModel by inject()

        val toastMessagesObserver = mockObserver<String>()
        val loadingStateObserver = mockObserver<Boolean>()
        declareMock<DataManager> {
            given(this.getRepositories()).willReturn(Flowable.unsafeCreate { it.onError(Throwable("Some error")) })
        }

        viewModel.toastMessages.subscribe(toastMessagesObserver)
        viewModel.isLoading.subscribe(loadingStateObserver)

        viewModel.loadRepos()

        verify(loadingStateObserver, times(1)).onNext(true)
        verify(loadingStateObserver, times(2)).onNext(false)
        verify(toastMessagesObserver, times(1)).onNext("Something wrong")
        verify(loadingStateObserver, Mockito.never()).onError(ArgumentMatchers.any(Throwable::class.java))
        verify(loadingStateObserver, Mockito.never()).onComplete()
        verify(toastMessagesObserver, Mockito.never()).onError(ArgumentMatchers.any(Throwable::class.java))
        verify(toastMessagesObserver, Mockito.never()).onComplete()
    }



    @After
    fun cleanup(){
        bag.clear()
        stopKoin()
    }

}
