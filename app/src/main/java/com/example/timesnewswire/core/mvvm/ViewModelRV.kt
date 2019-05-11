package com.example.timesnewswire.core.mvvm

import android.app.Application
import android.content.Context
import android.provider.MediaStore
import androidx.lifecycle.*
import androidx.paging.PagedList
import com.example.timesnewswire.core.CoreSingleton
import com.example.timesnewswire.core.liveData.StringChangedLiveData
import com.example.timesnewswire.models.Repository
import com.example.timesnewswire.models.roomDatabases.timesNewsWireDatabase.articles.ArticleEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class ViewModelRV<T>(private val app: Application, protected val repository: Repository): AndroidViewModel(app) {

    protected val appContext: Context by lazy { app.applicationContext }
    val pagedListLiveData: LiveData<PagedList<T>> by lazy { PagedListInitialize() }
        @Synchronized get
    val filterConstraintLiveData by lazy { StringChangedLiveData() }

    protected abstract fun PagedListInitialize(): LiveData<PagedList<T>>
    protected abstract suspend fun PagedListFilter(pagedList: PagedList<T>, filterConstraint: String): List<T>
}