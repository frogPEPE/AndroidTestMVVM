package com.example.timesnewswire.core

import android.icu.text.SimpleDateFormat
import android.icu.util.TimeZone
import java.util.*

class CoreTools{
    val dateFormatUTC = "yyyy-MM-dd'T'HH:mm:ss.X"
    val timeZoneUTC = TimeZone.getTimeZone("UTC")
    val dateFormatDefault = "dd MMM yyyy HH:mm:ss"
    val timeZoneDefault = TimeZone.getDefault()

    /**
     * String to Date.
     * @param dateFormat default: "yyyy-MM-dd'T'HH:mm:ss.X"
     * @param timeZone default: 'UTC'
     */
    fun toDate(
        date: String, dateFormat: String = dateFormatUTC,
        timeZone: TimeZone = timeZoneUTC
    ): Date {
        val parser = SimpleDateFormat(dateFormat, Locale.getDefault())
        parser.timeZone = timeZone
        return parser.parse(date)
    }

    /**
     * Date to String.
     * @param dateFormat default: "yyyy-MM-dd'T'HH:mm:ss.X"
     * @param timeZone default: 'UTC'
     */
    fun formatTo(
        date: Date, dateFormat: String = dateFormatUTC,
        timeZone: TimeZone = timeZoneUTC
    ): String {
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
        formatter.timeZone = timeZone
        return formatter.format(date)
    }
}