package com.example.timesnewswire.core

import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.timesnewswire.activities.MainActivity
import com.example.timesnewswire.models.Repository
import com.example.timesnewswire.models.roomDatabases.timesNewsWireDatabase.articles.ArticleEntity
import com.example.timesnewswire.models.timesNewsWireAPI.TNWAPI
import com.example.timesnewswire.models.timesNewsWireAPI.TNWFunctions
import com.example.timesnewswire.models.timesNewsWireAPI.TNWTools
import com.example.timesnewswire.models.timesNewsWireAPI.TNWURLBuilder
import com.example.timesnewswire.R
import com.example.timesnewswire.volley.VolleyReturnCallback
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlertReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        //Где QUICKBOOT_POWERON в INTENT?
        val repository = Repository(context)
        val tnwFunctions = TNWFunctions(context, repository)
        if (Intent.ACTION_BOOT_COMPLETED == intent.action)
            tnwFunctions.SetArticlesUpdateDBAlarm()
        else
            ArticlesUpdate(repository, context, tnwFunctions)
    }

    private fun ArticlesUpdate(repository: Repository, context: Context, tnwFunctions: TNWFunctions) {
        val tnwTools = TNWTools()
        val downloadOldArticles = object : VolleyReturnCallback {
            override fun onError(error: String) {
                Log.d("ERROR: Alert receiver get articles.", error)
            }

            override fun onResponse(result: String) {
                CoreSingleton.getInstance(context).coroutineScope.launch(Dispatchers.Default) {
                    val jElement: JsonElement = JsonParser().parse(result)
                    val jObject: JsonObject = jElement.asJsonObject
                    val articles: List<ArticleEntity> = tnwTools.GetArticlesFromJson(jObject)
                    if (articles.isEmpty())
                        return@launch

                    repository.InsertArticles(articles)
                    val favoriteArticles: List<ArticleEntity> = tnwFunctions.GetAllFavoriteArticles(articles)
                    val articlesNotifyMessage =
                        context.resources.getString(R.string.article_Of_Favorite_Section_Published)

                    for ((privateIntentCode, article) in favoriteArticles.withIndex()) {
                        val stackBuilder = TaskStackBuilder.create(context)
                        stackBuilder.addParentStack(MainActivity::class.java)
                        stackBuilder.addNextIntent(Intent(context, MainActivity::class.java))
                        stackBuilder.addNextIntent(CoreSingleton.getInstance(context).GetWebViewIntent(article))

                        val notifyIntent = stackBuilder.getPendingIntent(
                            privateIntentCode,
                            PendingIntent.FLAG_UPDATE_CURRENT
                        )

                        val nHelper = NotificationHelper(context)
                        val nBuilder: NotificationCompat.Builder = nHelper.getChannelNotification(
                            articlesNotifyMessage + article.sectionName,
                            article.title,
                            notifyIntent
                        )

                        nHelper.mManager.notify(NotificationHelper.notifyId, nBuilder.build())
                    }
                }
            }
        }

        val tnwURLBuilder = TNWURLBuilder(context)
        tnwFunctions.DownloadNewArticlesInPeriod(
            downloadOldArticles,
            TNWAPI(context, tnwURLBuilder),
            tnwURLBuilder,
            tnwURLBuilder.defaultHoursPeriodArticlesDownload
        )
    }
}
