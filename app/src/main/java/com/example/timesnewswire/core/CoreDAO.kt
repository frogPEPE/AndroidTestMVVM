package com.example.timesnewswire.core

import androidx.paging.DataSource

interface CoreDAO <T> {
    fun GetArticlesDataSource(): DataSource.Factory<Int, T>
}