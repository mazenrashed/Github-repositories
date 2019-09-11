package com.mazen.wash.data.local.database

import androidx.room.RoomDatabase
import androidx.room.Database
import com.mazen.wash.data.local.database.daos.RepoDao
import com.mazenrashed.github.data.model.Repo


@Database(entities = [Repo::class], version = 1, exportSchema = true)
abstract class Database : RoomDatabase() {

    abstract fun repoDao(): RepoDao
}