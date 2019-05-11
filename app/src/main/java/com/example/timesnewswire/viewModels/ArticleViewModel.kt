package com.example.timesnewswire.viewModels

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.example.timesnewswire.core.liveData.ConnectivityLiveData
import com.example.timesnewswire.models.Repository
import com.example.timesnewswire.models.roomDatabases.timesNewsWireDatabase.articles.ArticleEntity
import com.example.timesnewswire.models.timesNewsWireAPI.TNWAPI
import com.example.timesnewswire.R
import com.example.timesnewswire.activities.SwipeRefreshSuccess
import com.example.timesnewswire.core.CoreSingleton
import com.example.timesnewswire.core.mvvm.ViewModelRV
import com.example.timesnewswire.models.timesNewsWireAPI.TNWFunctions
import com.example.timesnewswire.models.timesNewsWireAPI.TNWTools
import com.example.timesnewswire.models.timesNewsWireAPI.TNWURLBuilder
import com.example.timesnewswire.volley.VolleyReturnCallback
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ArticleViewModel(application: Application, repository: Repository, private val tnwAPI: TNWAPI,
                       private val tnwFunctions: TNWFunctions)
    :ViewModelRV<ArticleEntity>(application, repository) {

    val connectivityLiveData: ConnectivityLiveData by lazy { ConnectivityLiveData(application) }

    //В PAGED LIST ДАННЫЕ ЯВЛЯЮТСЯ IMMUTABLE, ПОЭТОМУ
    //В PAGED LIST СЛЕДУЕТ ИСПОЛЬЗОВАТЬ СВОЙ DATA SOURCE
    //ВМЕСТО АВТОГЕНЕРИРУЕМОГО В DAO (В CoreDAO объявлена функция, в TNWDao она реализуется)
    //ИБО В СВОЁМ DATA SOURCE МОЖНО БУДЕТ ИСПОЛЬЗОВАТЬ ФИЛЬТР ЗАПИСЕЙ
    //СВОЙ DATA SOURCE УЖЕ ЕСТЬ В [activities.articlesRecyclerView]
    override fun PagedListInitialize(): LiveData<PagedList<ArticleEntity>> {
        return repository.pagedListFactory.createPagedList(
            appContext.resources.getInteger(R.integer.PagedList_PageSize),
            appContext.resources.getBoolean(R.bool.PagedList_PlaceHolders_Enable),
            object: PagedList.BoundaryCallback<ArticleEntity>() {
                override fun onItemAtEndLoaded(itemAtEnd: ArticleEntity) {
                    val downloadOldArticles = object : VolleyReturnCallback {
                        override fun onError(error: String) {
                            Toast.makeText(repository.context, R.string.API_Articles_Response_Error, Toast.LENGTH_SHORT).show()
                        }

                        override fun onResponse(result: String) {
                            CoreSingleton.getInstance(repository.context).coroutineScope.launch(Dispatchers.Main) {
                                withContext(Dispatchers.Default) {
                                    val jElement: JsonElement = JsonParser().parse(result)
                                    val jObject: JsonObject = jElement.asJsonObject
                                    val receivedArticles: List<ArticleEntity> = TNWTools().GetArticlesFromJson(jObject)
                                    repository.InsertArticles(receivedArticles)
                                }
                            }
                        }
                    }

                    tnwFunctions.DownloadOldArticles(downloadOldArticles, tnwAPI, TNWURLBuilder(repository.context))
                }
            }
        )
    }

    override suspend fun PagedListFilter(pagedList: PagedList<ArticleEntity>, filterConstraint: String)
            :List<ArticleEntity>
    = withContext(Dispatchers.Default) {
        val articles: ArrayList<ArticleEntity> = arrayListOf()
        articles.addAll(pagedList)
        if (filterConstraint.isNotEmpty()) {
            loop@ for (article in pagedList) {
                when {
                    article.title.trim().toLowerCase().contains(filterConstraint) -> continue@loop
                    article.sectionName.trim().toLowerCase().contains(filterConstraint) -> continue@loop
                    else -> articles.remove(article)
                }
            }
        }

        return@withContext articles
    }

    fun UpdateArticlesDB(source: String, section: String, articlesCount: Int, articlesOffset: Int,
                         swipeRefreshSuccess: SwipeRefreshSuccess) {
        val downloadNewArticles = object : VolleyReturnCallback {
            override fun onError(error: String) {
                Toast.makeText(appContext, R.string.API_Articles_Response_Error, Toast.LENGTH_SHORT).show()
                swipeRefreshSuccess.onSuccess()
            }

            override fun onResponse(result: String) {
                CoreSingleton.getInstance(appContext).coroutineScope.launch(Dispatchers.Main) {
                    withContext(Dispatchers.Default) {
                        val jElement: JsonElement = JsonParser().parse(result)
                        val jObject: JsonObject = jElement.asJsonObject
                        val receivedArticles: List<ArticleEntity> = TNWTools().GetArticlesFromJson(jObject)
                        repository.InsertArticles(tnwFunctions.GetNewArticles(receivedArticles))
                    }
                    swipeRefreshSuccess.onSuccess()
                }
            }
        }

        tnwFunctions.DownloadNewArticles(source, section, articlesCount, articlesOffset, tnwAPI, downloadNewArticles)
    }

    fun InsertArticle(articleEntity: ArticleEntity){
        repository.InsertArticles(articleEntity)
    }

    fun UpdateArticle(articleEntity: ArticleEntity){
        repository.UpdateArticle(articleEntity)
    }

    fun DeleteArticle(articleEntity: ArticleEntity){
        repository.DeleteArticle(articleEntity)
    }

    fun DeleteAllArticles(){
        repository.DeleteAllArticles()
    }
}