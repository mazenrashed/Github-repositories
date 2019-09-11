package com.mazen.wash.data.local.database.daos

import androidx.room.*
import com.mazenrashed.github.data.model.Repo
import io.reactivex.Single
import org.jetbrains.annotations.NotNull


@Dao
interface RepoDao {

    @Query("SELECT * FROM Repo")
    fun fetchAllRepositories(): Single<List<Repo>>

    @NotNull
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRepositories(repositories: List<Repo>)

}