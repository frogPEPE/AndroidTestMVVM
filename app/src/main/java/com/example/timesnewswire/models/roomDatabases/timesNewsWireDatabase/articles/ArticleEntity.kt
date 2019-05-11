package com.example.timesnewswire.models.roomDatabases.timesNewsWireDatabase.articles

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.util.*


@Entity(tableName = "Article")
@TypeConverters(ArticleEntityConverter::class)
class ArticleEntity(sectionName: String, createdDateTime: Date, title: String, URL: String, source: String) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID")
    var id: Int = 0

    @ColumnInfo(name = "CREATED_DATE_TIME")
    var createdDateTime: Date = createdDateTime

    @ColumnInfo(name = "SECTION")
    val sectionName: String = sectionName

    @ColumnInfo(name = "TITLE")
    val title: String = title

    @ColumnInfo(name = "URL")
    val URL: String = URL

    @ColumnInfo(name = "SOURCE")
    val source: String = source

    override fun equals(other: Any?): Boolean {
        if (other !is ArticleEntity) return false
        if (this === other) return true
        if (other.URL == URL) return true

        return false
    }
}