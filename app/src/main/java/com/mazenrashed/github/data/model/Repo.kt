package com.mazenrashed.github.data.model


import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "Repo")
class Repo : Serializable {
    @PrimaryKey(autoGenerate = false)
    @SerializedName("id")
    var id: Int = 0
    @SerializedName("private")
    var isPrivate: Boolean = false
    @SerializedName("archived")
    var isArchived: Boolean = false
    @SerializedName("created_at")
    var createdAt: String? = null
    @SerializedName("description")
    var description: String? = ""
    @SerializedName("disabled")
    var disabled: Boolean = false
    @SerializedName("fork")
    var fork: Boolean = false
    @SerializedName("forks_count")
    var forksCount: Int? = 0
    @SerializedName("full_name")
    var fullName: String = ""
    @SerializedName("has_downloads")
    var hasDownloads: Boolean = false
    @SerializedName("language")
    var language: String = ""
    @SerializedName("name")
    var name: String = ""
    @SerializedName("stargazers_count")
    var stargazersCount: Int = 0
    @SerializedName("updated_at")
    var updatedAt: String? = null
    @SerializedName("url")
    var url: String? = null
    @SerializedName("watchers")
    var watchers: Int = 0
    @SerializedName("watchers_count")
    var watchersCount: Int = 0


    @Embedded
    var owner: Owner? = null
}

