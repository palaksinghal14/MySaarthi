package com.palaksinghal.mysaarthi.data.local.converters

import androidx.room.TypeConverter
import com.palaksinghal.mysaarthi.domain.model.PracticeReminder
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


class Converters {

    @TypeConverter
    fun fromStringList(value:List<String>): String{
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun toStringList(value:String):List<String>
    {
        return Json.decodeFromString(value)
    }

    @TypeConverter
    fun fromPracticeReminders(value: List<PracticeReminder>): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun toPracticeReminders(value: String): List<PracticeReminder> {
        return Json.decodeFromString(value)
    }
}