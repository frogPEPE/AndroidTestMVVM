package com.example.timesnewswire.models.timesNewsWireAPI

import com.example.timesnewswire.core.CoreTools
import com.example.timesnewswire.models.Repository
import com.example.timesnewswire.models.roomDatabases.timesNewsWireDatabase.articles.ArticleEntity
import com.example.timesnewswire.models.roomDatabases.timesNewsWireDatabase.sections.SectionEntity
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import java.util.concurrent.TimeUnit

class TNWTools {
    fun GetArticlesFromJson(jsonResponse: JsonObject): List<ArticleEntity>{
        val articles: ArrayList<ArticleEntity> = ArrayList()
        val jArticles: JsonArray = jsonResponse.getAsJsonArray(JSONFields.results)
        val coreTools = CoreTools()
        for(article in jArticles) {
            articles.add(
                ArticleEntity(
                    article.asJsonObject.get(JSONFields.section).asString,
                    coreTools.toDate(article.asJsonObject.get(JSONFields.createdDate).asString),
                    article.asJsonObject.get(JSONFields.title).asString,
                    article.asJsonObject.get(JSONFields.url).asString,
                    article.asJsonObject.get(JSONFields.source).asString
                )
            )
        }

        return articles
    }

    fun GetSectionsFromJson(jsonResponse: JsonObject): List<SectionEntity>{
        val sections: ArrayList<SectionEntity> = ArrayList()
        val jSections: JsonArray = jsonResponse.getAsJsonArray(JSONFields.results)
        for(section in jSections) {
            sections.add(
                SectionEntity(
                    section.asJsonObject.get(JSONFields.section).asString,
                    section.asJsonObject.get(JSONFields.sectionDisplayName).asString,
                    false
                )
            )
        }

        return sections
    }

    /**
     * @return Return defaultHoursPeriod if there is no articles in database
     */
    suspend fun GetHoursSinceLastPublishedArticle(repository: Repository, defaultHoursPeriod: Long): Long
    = withContext(Dispatchers.Default) {
        val lastArticle: ArticleEntity? = repository.GetLastPublishedArticle()
        return@withContext if (lastArticle == null)
            defaultHoursPeriod
        else
            TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis() - lastArticle.createdDateTime.time)
    }
}