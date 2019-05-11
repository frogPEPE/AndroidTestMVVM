package com.example.timesnewswire.models.timesNewsWireAPI

class JSONFields {
    companion object {
        val results: String by lazy{"results"}
        val status: String by lazy{"status"}
        val title: String by lazy{"title"}
        val url: String by lazy{"url"}
        val createdDate: String by lazy{"created_date"}
        val section: String by lazy{"section"}
        val sectionDisplayName: String by lazy{"display_name"}
        val source: String by lazy{"source"}
    }
}