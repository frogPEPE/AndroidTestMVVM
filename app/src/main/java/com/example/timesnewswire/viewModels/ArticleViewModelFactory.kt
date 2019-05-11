package com.example.timesnewswire.viewModels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.timesnewswire.models.Repository
import com.example.timesnewswire.models.timesNewsWireAPI.TNWAPI
import com.example.timesnewswire.models.timesNewsWireAPI.TNWFunctions


class ArticleViewModelFactory(private val app: Application,
                              private val repository: Repository,
                              private val tnwAPI: TNWAPI,
                              private val tnwFunctions: TNWFunctions)
    : ViewModelProvider.Factory{

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ArticleViewModel(app, repository, tnwAPI, tnwFunctions) as T
    }

}