package com.example.timesnewswire.activities.articlesRecyclerView

import androidx.paging.PositionalDataSource
import com.example.timesnewswire.core.CoreSingleton
import com.example.timesnewswire.models.Repository
import com.example.timesnewswire.models.roomDatabases.timesNewsWireDatabase.articles.ArticleEntity
import kotlinx.coroutines.*
import kotlin.collections.ArrayList

@Deprecated("Replaced by DataSource provided by DAO.")
class ArticlePositionalDataSource(private val repository: Repository): PositionalDataSource<ArticleEntity>() {
    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<ArticleEntity>) {
        CoreSingleton.getInstance(repository.context).coroutineScope.launch {
            val result: List<ArticleEntity> = GetSubListOfArticles(
                params.requestedStartPosition,
                params.requestedLoadSize
            )

            callback.onResult(result, 0)
        }
    }

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<ArticleEntity>) {
        CoreSingleton.getInstance(repository.context).coroutineScope.launch {
            val result: List<ArticleEntity> = GetSubListOfArticles(
                params.startPosition,
                params.loadSize
            )

            callback.onResult(result)
        }
    }

    private suspend fun GetSubListOfArticles(startPosition: Int, count: Int):List<ArticleEntity>
            = withContext(Dispatchers.Default) {
        val result: ArrayList<ArticleEntity> = arrayListOf()
        if (count < 1)
            return@withContext result

        val articles: List<ArticleEntity> = repository.GetAllArticles()
        joinAll()
        if (articles.isEmpty() || startPosition >= articles.size)
            return@withContext result

        val sPosition = if (startPosition > 0) startPosition else 0//LoadInitialParams return -1, LUL?
        if (sPosition < articles.size) {
            if (sPosition + count <= articles.size)
                result.addAll(articles.subList(sPosition, count))
            else
                result.addAll(articles.subList(sPosition, articles.size))
        }

        return@withContext result
    }
}