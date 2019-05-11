package com.example.timesnewswire.models.timesNewsWireAPI

import android.content.Context
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.example.timesnewswire.R
import com.example.timesnewswire.volley.VolleyReturnCallback
import com.example.timesnewswire.volley.VolleySingleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TNWAPI(context: Context, tnwURLBuilder: TNWURLBuilder) {
    private val apiKey: String by lazy { context.resources.getString(R.string.TNW_API_KEY) }
    private val urlBuilder by lazy { tnwURLBuilder }
    private val appContext: Context by lazy { context.applicationContext }

    /**
     * Get articles TNWApi: https://developer.nytimes.com/docs/timeswire-product/1/routes/content/{source}/{section}.json/get
     * 'withContext(Dispatchers.Default)' doesn't block the caller
     * @param source The following values are allowed: all, nyt, iht
     * @param section Name of articles section (eg. all)
     * @param articlesCount from 1 to 20 by TNWAPI description, but you can put more
     */
    suspend fun GetArticles(
        source: String,
        section: String,
        articlesCount: Int,
        articlesOffset: Int,
        returnCallback: VolleyReturnCallback
    ) = withContext(Dispatchers.Default) {

        val url: String = urlBuilder.GetArticlesURL(source, section, apiKey, articlesCount, articlesOffset)
        val articlesRequest = StringRequest(
            Request.Method.GET,
            url,
            Response.Listener { response -> returnCallback.onResponse(response.toString()) },
            Response.ErrorListener { error -> returnCallback.onError(error.toString()) }
        )

        VolleySingleton.getInstance(appContext).requestQueue.add(articlesRequest)
    }

    /**
     * Get articles in the period from now to specified time period (offset in hours).
     * TNWApi: https://developer.nytimes.com/docs/timeswire-product/1/routes/content/{source}/{section}/{time-period}.json/get
     * 'withContext(Dispatchers.Default)' doesn't block the caller
     * @param source The following values are allowed: all, nyt, iht
     * @param section Name of articles section (eg. all)
     * @param articlesCount from 1 to 20 by TNWAPI description, but you can put more
     */
    suspend fun GetArticlesInPeriod(
        source: String,
        section: String,
        timePeriod: Long,
        articlesCount: Int,
        articlesOffset: Int,
        returnCallback: VolleyReturnCallback
    ) = withContext(Dispatchers.Default) {

        val url: String = urlBuilder.GetArticlesURL(source, section, timePeriod, apiKey, articlesCount, articlesOffset)
        val articlesRequest = StringRequest(
            Request.Method.GET,
            url,
            Response.Listener { response -> returnCallback.onResponse(response.toString()) },
            Response.ErrorListener { error -> returnCallback.onError(error.toString()) }
        )

        VolleySingleton.getInstance(appContext).requestQueue.add(articlesRequest)
    }

    suspend fun GetSetions(returnCallback: VolleyReturnCallback) = withContext(Dispatchers.Default) {
        val url: String = urlBuilder.GetSectionsURL(apiKey)
        val sectionsRequest = StringRequest(
            Request.Method.GET,
            url,
            Response.Listener { response -> returnCallback.onResponse(response.toString()) },
            Response.ErrorListener { error -> returnCallback.onError(error.toString()) }
        )

        VolleySingleton.getInstance(appContext).requestQueue.add(sectionsRequest)
    }
}