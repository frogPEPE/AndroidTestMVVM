package com.example.timesnewswire.models.roomDatabases.timesNewsWireDatabase

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import com.example.timesnewswire.core.CoreDAO
import com.example.timesnewswire.models.roomDatabases.timesNewsWireDatabase.articles.ArticleEntity
import com.example.timesnewswire.models.roomDatabases.timesNewsWireDatabase.sections.SectionEntity

@Dao
interface TNWDao: CoreDAO<ArticleEntity> {
    //region Articles queries
    @Insert
    fun InsertArticle(articleEntity: ArticleEntity)

    @Update
    fun UpdateArticle(articleEntity: ArticleEntity)

    @Delete
    fun DeleteArticle(articleEntity: ArticleEntity)

    @Query("DELETE FROM Article")
    fun DeleteAllArticles()

    @Query("SELECT * FROM Article ORDER BY date(CREATED_DATE_TIME) DESC")
    fun GetAllArticles(): List<ArticleEntity>

    @Query("SELECT * FROM Article ORDER BY date(CREATED_DATE_TIME) DESC")
    fun GetAllArticlesLiveData(): LiveData<List<ArticleEntity>>

    @Query("SELECT * FROM Article ORDER BY date(CREATED_DATE_TIME) DESC LIMIT 1")
    fun GetLastPublishedArticle(): ArticleEntity

    @Query("SELECT * FROM Article ORDER BY date(CREATED_DATE_TIME) ASC LIMIT 1")
    fun GetOldestPublishedArticle(): ArticleEntity

    @Query("SELECT COUNT(*) FROM Article")
    fun GetArticlesCount(): Int
    //endregion

    //region Sections queries
    @Insert
    fun InsertSection(sectionEntity: SectionEntity)

    @Update
    fun UpdateSection(sectionEntity: SectionEntity)

    @Delete
    fun DeleteSection(sectionEntity: SectionEntity)

    @Query("DELETE FROM Section")
    fun DeleteAllSections()

    @Query("SELECT * FROM Section")
    fun GetAllSectionsLiveData(): LiveData<List<SectionEntity>>

    @Query("SELECT * FROM Section")
    fun GetAllSections(): List<SectionEntity>

    @Query("SELECT COUNT(*) FROM Section")
    fun GetSectionsCount(): Int
    //endregion

    @Query("SELECT * FROM Article")
    override fun GetArticlesDataSource(): DataSource.Factory<Int, ArticleEntity>
}