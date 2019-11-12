package com.mazenrashed.github.rx

import io.reactivex.schedulers.Schedulers

class AppSchedulers {

    fun getIoScheduler() = Schedulers.io()

    fun getComputationSchedeler() = Schedulers.computation()

    fun getTrampolineScheduler() = Schedulers.trampoline()

    fun getNewThreadScheduler() = Schedulers.newThread()
}