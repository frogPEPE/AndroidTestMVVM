package com.example.timesnewswire.models.roomDatabases.timesNewsWireDatabase.articles

import androidx.room.TypeConverter
import com.example.timesnewswire.core.CoreTools
import java.util.*

class ArticleEntityConverter {

    @TypeConverter
    fun fromCreatedDateTime(createdDate: Date): String{
        return CoreTools().formatTo(createdDate)
    }

    @TypeConverter
    fun toCreatedDateTime(date: String): Date {
        return CoreTools().toDate(date)
    }
}