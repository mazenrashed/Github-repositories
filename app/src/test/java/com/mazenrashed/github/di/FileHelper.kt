package com.mazenrashed.github.di

import java.io.File

class FileHelper {
    fun getFileFromPath(fileName: String): File {
        val classLoader = javaClass.classLoader
        val resource = classLoader?.run { this.getResource(fileName) }
        return File(resource?.path)
    }
}