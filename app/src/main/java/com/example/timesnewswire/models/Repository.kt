package com.example.timesnewswire.models

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.example.timesnewswire.activities.articlesRecyclerView.RVPagedListFactory
import com.example.timesnewswire.core.CoreDAO
import com.example.timesnewswire.core.CoreSingleton
import com.example.timesnewswire.core.liveData.LiveDataOnChangedListener
import com.example.timesnewswire.models.roomDatabases.RoomsDatabase
import com.example.timesnewswire.models.roomDatabases.timesNewsWireDatabase.TNWDao
import com.example.timesnewswire.models.roomDatabases.timesNewsWireDatabase.articles.ArticleEntity
import com.example.timesnewswire.models.roomDatabases.timesNewsWireDatabase.sections.SectionEntity
import kotlinx.coroutines.*

/**
 * Repository providing a unified data interface.
 */
class Repository(val context: Context) {
    private val tnwDao: TNWDao
            by lazy { RoomsDatabase.GetInstance(context).GetArticleDAO() }
        @Synchronized get

    private val allArticles: LiveData<List<ArticleEntity>>
            by lazy { tnwDao.GetAllArticlesLiveData() }
        @Synchronized get

    private val allSections: LiveData<List<SectionEntity>>
            by lazy { tnwDao.GetAllSectionsLiveData() }
        @Synchronized get

    val pagedListFactory by lazy { RVPagedListFactory(GetDAO<ArticleEntity>()) }
        @Synchronized get

    @Suppress("UNCHECKED_CAST")
    private inline fun <reified T> GetDAO(): CoreDAO<T> {
        return when (T::class.java) {
            ArticleEntity::class.java -> tnwDao as CoreDAO<T>
            else -> throw ClassNotFoundException("There is no appropriate DAO in Repository.")
        }
    }

    //region Articles functions
    //Must take care of our queries, that must
    //be executed in background thread, except of
    //observable data holder class "LiveData"
    fun InsertArticles(articleEntity: ArticleEntity) {
        CoreSingleton.getInstance(context).coroutineScope.launch(Dispatchers.Default) {
            InsertArticleCoroutine(articleEntity)
        }
    }

    fun InsertArticles(articleEntities: List<ArticleEntity>) {
        CoreSingleton.getInstance(context).coroutineScope.launch(Dispatchers.Default) {
            for (articleEntity in articleEntities) {
                InsertArticleCoroutine(articleEntity)
            }
        }
    }

    fun DeleteArticle(articleEntity: ArticleEntity) {
        CoreSingleton.getInstance(context).coroutineScope.launch(Dispatchers.Default) {
            DeleteArticleCoroutine(articleEntity)
        }
    }

    fun UpdateArticle(articleEntity: ArticleEntity) {
        CoreSingleton.getInstance(context).coroutineScope.launch(Dispatchers.Default) {
            UpdateArticleCoroutine(articleEntity)
        }
    }

    fun ResetArticlesDB(articlesEntities: List<ArticleEntity>) {
        CoreSingleton.getInstance(context).coroutineScope.launch(Dispatchers.Default) {
            DeleteAllArticlesCoroutine()
            InsertArticlesCoroutine(articlesEntities)
        }
    }

    fun DeleteAllArticles() {
        CoreSingleton.getInstance(context).coroutineScope.launch(Dispatchers.Default) {
            DeleteAllArticlesCoroutine()
        }
    }

    fun GetAllArticlesLiveData(): LiveData<List<ArticleEntity>> {
        return allArticles
    }

    fun SetObserverArticlesLiveData(onChangedListener: LiveDataOnChangedListener<ArticleEntity>) {
        val observer = Observer<List<ArticleEntity>> { articles ->
            articles?.let { onChangedListener.OnChanged(it) }
        }

        CoreSingleton.getInstance(context).coroutineScope.launch(Dispatchers.Main) {
            allArticles.observe((context as LifecycleOwner), observer)
        }
    }

    /**
     * Remove the old observer that trigger old changed listener.
     */
    fun RemoveObserverArticlesLiveData(onChangedListener: LiveDataOnChangedListener<ArticleEntity>) {
        val oldObserver = Observer<List<ArticleEntity>> { articles ->
            articles?.let { onChangedListener.OnChanged(it) }
        }

        CoreSingleton.getInstance(context).coroutineScope.launch(Dispatchers.Main) {
            allArticles.removeObserver(oldObserver)
        }
    }

    suspend fun GetAllArticles(): List<ArticleEntity> {
        return GetAllArticlesCoroutine()
    }

    suspend fun GetLastPublishedArticle(): ArticleEntity? {
        return GetLastPublishedArticleCoroutine()
    }

    suspend fun GetOldestPublishedArticle(): ArticleEntity? {
        return GetOldestPublishedArticleCoroutine()
    }

    suspend fun GetArticlesCount(): Int{
        return GetArticlesCountCoroutine()
    }

    //region Coroutines
    private suspend fun InsertArticleCoroutine(articleEntity: ArticleEntity) = withContext(Dispatchers.Default) {
        tnwDao.InsertArticle(articleEntity)
    }

    private suspend fun InsertArticlesCoroutine(articlesEntities: List<ArticleEntity>) =
        withContext(Dispatchers.Default) {
            for (article in articlesEntities) {
                tnwDao.InsertArticle(article)
            }
        }

    private suspend fun DeleteArticleCoroutine(articleEntity: ArticleEntity) = withContext(Dispatchers.Default) {
        tnwDao.DeleteArticle(articleEntity)
    }

    private suspend fun UpdateArticleCoroutine(articleEntity: ArticleEntity) = withContext(Dispatchers.Default) {
        tnwDao.UpdateArticle(articleEntity)
    }

    private suspend fun DeleteAllArticlesCoroutine() = withContext(Dispatchers.Default) {
        tnwDao.DeleteAllArticles()
    }

    private suspend fun GetLastPublishedArticleCoroutine() = withContext(Dispatchers.Default) {
        return@withContext tnwDao.GetLastPublishedArticle()
    }

    private suspend fun GetOldestPublishedArticleCoroutine() = withContext(Dispatchers.Default) {
        return@withContext tnwDao.GetOldestPublishedArticle()
    }

    private suspend fun GetArticlesCountCoroutine() = withContext(Dispatchers.Default) {
        return@withContext tnwDao.GetArticlesCount()
    }

    private suspend fun GetAllArticlesCoroutine() = withContext(Dispatchers.Default) {
        return@withContext tnwDao.GetAllArticles()
    }
    //endregion
    //endregion

    //region Sections functions
    fun ResetSectionsDB(sectionsEntities: List<SectionEntity>) {
        CoreSingleton.getInstance(context).coroutineScope.launch(Dispatchers.Default) {
            DeleteAllSectionsCoroutine()
            InsertSectionsCoroutine(sectionsEntities)
        }
    }

    fun GetAllSectionsLiveData(): LiveData<List<SectionEntity>> {
        return allSections
    }

    suspend fun GetAllSections(): List<SectionEntity> {
        return GetAllSectionsCoroutine()
    }

    suspend fun SectionEntityIsEmpty(): Boolean {
        return GetSectionsCountCoroutine() < 1
    }

    private suspend fun InsertSectionCoroutine(sectionsEntity: SectionEntity) = withContext(Dispatchers.Default) {
        tnwDao.InsertSection(sectionsEntity)
    }

    private suspend fun InsertSectionsCoroutine(sectionsEntities: List<SectionEntity>) =
        withContext(Dispatchers.Default) {
            for (section in sectionsEntities) {
                tnwDao.InsertSection(section)
            }
        }

    private suspend fun DeleteAllSectionsCoroutine() = withContext(Dispatchers.Default) {
        tnwDao.DeleteAllSections()
    }

    private suspend fun GetSectionsCountCoroutine() = withContext(Dispatchers.Default) {
        return@withContext tnwDao.GetSectionsCount()
    }

    private suspend fun GetAllSectionsCoroutine() = withContext(Dispatchers.Default) {
        return@withContext tnwDao.GetAllSections()
    }
    //endregion
}