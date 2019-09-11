package com.mazenrashed.github

import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class GithubApplication : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()

        MultiDex.install(this)

        startKoin {
            androidLogger()
            androidContext(this@GithubApplication)
            modules(appModule)
        }

    }


}