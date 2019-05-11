package com.example.timesnewswire.activities.articlesRecyclerView

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.example.timesnewswire.core.CoreDAO
import java.util.concurrent.Executors

class RVPagedListFactory <T> (private val DAO: CoreDAO<T>) {
    fun createPagedList(pageSize: Int, enablePlaceHolders: Boolean, dataSourceFactory: DataSource.Factory<Int, T>)
            : LiveData<PagedList<T>> {
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(enablePlaceHolders)
            .setPageSize(pageSize)
            .build()

        return LivePagedListBuilder(dataSourceFactory, config)
            .setFetchExecutor(Executors.newSingleThreadExecutor())
            .build()
    }

    fun createPagedList(pageSize: Int, enablePlaceHolders: Boolean,
        boundaryCallback: PagedList.BoundaryCallback<T>):LiveData<PagedList<T>> {
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(enablePlaceHolders)
            .setPageSize(pageSize)
            .build()

        return LivePagedListBuilder(DAO.GetArticlesDataSource(), config)
            .setFetchExecutor(Executors.newSingleThreadExecutor())
            .setBoundaryCallback(boundaryCallback)
            .build()
    }
}