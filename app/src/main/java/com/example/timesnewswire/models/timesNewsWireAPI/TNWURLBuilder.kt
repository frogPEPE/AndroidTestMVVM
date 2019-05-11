package com.example.timesnewswire.models.timesNewsWireAPI

import android.content.Context
import com.example.timesnewswire.R

class TNWURLBuilder(context: Context) {
    private val https by lazy { "https" }
    private val apiURL by lazy { "api.nytimes.com" }
    private val contentURL by lazy { "svc/news/v3/content" }
    private val sectionList by lazy { "section-list" }
    private val jsonFormat by lazy { ".json" }
    private val apiKeyParam by lazy { "api-key" }
    private val limit by lazy { "limit" }
    private val offset by lazy { "offset" }
    val defaultTimePeriodArticlesDownload: Long by lazy { 0.toLong() }
    val defaultArticlesCount: Int by lazy { context.resources.getInteger(R.integer.Default_Count_Of_Articles_Download) }
    val defaultHoursPeriodArticlesDownload: Long
            by lazy { context.resources.getInteger(R.integer.Default_Hours_Period_Articles_Download).toLong() }
    val defaultArticlesOffset: Int by lazy { context.resources.getInteger(R.integer.Default_Offset_Of_Articles_Download) }
    val nytSource: String by lazy { "nyt" }
    val ihtSource: String by lazy { "iht" }
    val allSources: String by lazy { "all" }
    val allSections: String by lazy { "all" }

    /**
     * @param source Sources of articles
     * @param section Sections of articles
     * @param articlesOffset Offset in articles of New York Time Database
     */
    fun GetArticlesURL(source: String,
                       section: String,
                       apiKey: String,
                       articlesCount: Int,
                       articlesOffset: Int): String {
        return String.format(
            "$https://" +
                    "$apiURL/" +
                    "$contentURL/" +
                    "$source/" +
                    "$section/" +
                    "$jsonFormat?" +
                    "$limit=$articlesCount&" +
                    "$offset=$articlesOffset&" +
                    "$apiKeyParam=$apiKey"
        )
    }

    /**
     * @param source Sources of articles
     * @param section Sections of articles
     * @param articlesOffset Offset (starting point) of the articles set in New York Time Database
     * @param timePeriod Limits the set of items by time published, integer in number of hours
     */
    fun GetArticlesURL(source: String,
                       section: String,
                       timePeriod: Long,
                       apiKey: String,
                       articlesCount: Int,
                       articlesOffset: Int): String {
        return String.format(
            "$https://" +
                    "$apiURL/" +
                    "$contentURL/" +
                    "$source/" +
                    "$section/" +
                    "$timePeriod$jsonFormat?" +
                    "$limit=$articlesCount&" +
                    "$offset=$articlesOffset&" +
                    "$apiKeyParam=$apiKey"
        )
    }

    fun GetSectionsURL(apiKey: String): String {
        return String.format(
            "$https://" +
                    "$apiURL/" +
                    "$contentURL/" +
                    "$sectionList/" +
                    "$jsonFormat?" +
                    "$apiKeyParam=$apiKey"
        )
    }
}
