package com.example.timesnewswire.models.roomDatabases.timesNewsWireDatabase.sections

import androidx.room.*

@Entity(tableName = "Section", indices = [Index(value = ["Section_Name"], unique = true)])
class SectionEntity(sectionName: String, sectionDisplayName: String, isFavorite: Boolean) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID")
    var id: Int = 0

    @ColumnInfo(name = "Section_Name")
    var sectionName: String = sectionName

    @ColumnInfo(name = "Section_Display_Name")
    var sectionDisplayName: String = sectionDisplayName

    @ColumnInfo(name = "Section_Is_Favorite")
    var isFavorite: Boolean = isFavorite
}