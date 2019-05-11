package com.example.timesnewswire.models.timesNewsWireAPI

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.timesnewswire.core.AlertReceiver
import com.example.timesnewswire.core.CoreSingleton
import com.example.timesnewswire.models.Repository
import com.example.timesnewswire.models.roomDatabases.timesNewsWireDatabase.articles.ArticleEntity
import com.example.timesnewswire.models.roomDatabases.timesNewsWireDatabase.sections.SectionEntity
import com.example.timesnewswire.R
import com.example.timesnewswire.volley.VolleyReturnCallback
import kotlinx.coroutines.*

class TNWFunctions(val context: Context, val repository: Repository) {
    private val articlesUpdateRequestCode = 100
    private var articlesUpdateAlarmIsInactive: Boolean = (PendingIntent.getBroadcast(
        context,
        articlesUpdateRequestCode,
        Intent(context, AlertReceiver::class.java),
        PendingIntent.FLAG_NO_CREATE) == null)

    fun SetArticlesUpdateDBAlarm() {
        if (articlesUpdateAlarmIsInactive) {
            val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, AlertReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, articlesUpdateRequestCode, intent, 0)
            val interval: Long = context.resources.getInteger(R.integer.AlarmManager_Milliseconds_Default).toLong()

            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis(),
                interval,
                pendingIntent
            )
        }
    }

    fun DownloadNewArticles(source: String, section: String, articlesCount: Int, articlesOffset: Int, tnwAPI: TNWAPI,
                            downloadNewArticles: VolleyReturnCallback) {
        CoreSingleton.getInstance(context).coroutineScope.launch(Dispatchers.Default) {
            tnwAPI.GetArticles(source, section, articlesCount, articlesOffset, downloadNewArticles)
        }
    }

    fun StartSectionsUpdate(tnwAPI: TNWAPI, volleyReturnCallback: VolleyReturnCallback){
        CoreSingleton.getInstance(context).coroutineScope.launch(Dispatchers.Default) {
            tnwAPI.GetSetions(volleyReturnCallback)
        }
    }

    fun DownloadOldArticles(downloadOldArticles: VolleyReturnCallback, tnwAPI: TNWAPI, tnwURLBuilder: TNWURLBuilder) {
        CoreSingleton.getInstance(context).coroutineScope.launch(Dispatchers.Default) {
            tnwAPI.GetArticles(
                tnwURLBuilder.allSources,
                tnwURLBuilder.allSections,
                tnwURLBuilder.defaultArticlesCount,
                repository.GetArticlesCount(),
                downloadOldArticles
            )
        }
    }

    fun DownloadNewArticlesInPeriod(downloadNewArticles: VolleyReturnCallback, tnwAPI: TNWAPI,
                                            tnwURLBuilder: TNWURLBuilder, defaultHoursPeriod: Long) {
        CoreSingleton.getInstance(context).coroutineScope.launch(Dispatchers.Default) {
            val tnwTools = TNWTools()
            val hoursTimePeriod: Long = tnwTools.GetHoursSinceLastPublishedArticle(repository, defaultHoursPeriod)
            if (hoursTimePeriod < 1)
                return@launch

            tnwAPI.GetArticlesInPeriod(
                tnwURLBuilder.allSources,
                tnwURLBuilder.allSections,
                hoursTimePeriod,
                0,
                0,
                downloadNewArticles
            )
        }
    }

    /**
     * Get all articles that had been published in favorites sections.
     */
    suspend fun GetAllFavoriteArticles(articles: List<ArticleEntity>) = withContext(Dispatchers.Default) {
        val sections: ArrayList<SectionEntity> = repository.GetAllSections() as ArrayList<SectionEntity>
        val favoriteArticles: ArrayList<ArticleEntity> = arrayListOf()
        for (article in articles)
            for (section in sections) {
                if (article.sectionName.equals(section.sectionName, true) and section.isFavorite) {
                    favoriteArticles.add(article)
                    break
                }
            }

        return@withContext favoriteArticles
    }

    /**
     * Get only new articles from the list, that hadn't been added to DB.
     */
    suspend fun GetNewArticles(articles: List<ArticleEntity>) = withContext(Dispatchers.Default) {
        val lastArticle: ArticleEntity? = repository.GetLastPublishedArticle()
        if (lastArticle == null)
            return@withContext articles

        val newArticles: ArrayList<ArticleEntity> = arrayListOf()
        //Articles sorted by published time.
        for (article in articles) {
            if (article == lastArticle)
                break

            newArticles.add(article)
        }

        return@withContext newArticles
    }
}