package com.example.timesnewswire.core

import android.content.Context
import android.content.Intent
import com.example.timesnewswire.activities.WebViewActivity
import com.example.timesnewswire.models.roomDatabases.timesNewsWireDatabase.articles.ArticleEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren

class CoreSingleton private constructor(context: Context){
    companion object {
        @Synchronized
        fun getInstance(context: Context): CoreSingleton {
            if (!Companion::instance.isInitialized)
                instance = CoreSingleton(context)

            return instance
        }

        private lateinit var instance: CoreSingleton
        fun isInstanceInit() = Companion::instance.isInitialized
    }

    init {
        //Change constructor scope modification through reflection ->
        //can create new instance of Singleton.
        if (isInstanceInit()){
            //Prevent form the reflection api.
            throw RuntimeException("Use getInstance() method to get the single instance of this class.")
        }
    }

    private val appContext: Context by lazy { context.applicationContext }
    private val job = SupervisorJob()
    val coroutineScope = CoroutineScope(Dispatchers.Default + job)

    fun CancelAllJobs(){
        coroutineScope.coroutineContext.cancelChildren()
    }

    fun GetWebViewIntent(article: ArticleEntity):Intent {
        val intent = Intent(appContext, WebViewActivity::class.java)
        intent.putExtra(WebViewActivity.URL_EXTRA, article.URL)
        return intent
    }
}