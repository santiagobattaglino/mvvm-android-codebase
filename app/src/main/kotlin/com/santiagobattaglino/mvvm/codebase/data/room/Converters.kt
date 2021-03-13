package com.santiagobattaglino.mvvm.codebase.data.room

import com.santiagobattaglino.mvvm.codebase.domain.model.Media
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Collections.emptyList

class Converters {

    private var gson = Gson()

    @TypeConverter
    fun stringListToString(strings: List<String>): String {
        return gson.toJson(strings)
    }

    @TypeConverter
    fun stringToStringList(data: String?): List<String> {
        if (data == null) {
            return emptyList()
        }

        val listType = object : TypeToken<List<String>>() {}.type

        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun mediaListToString(mediaList: List<Media>): String {
        return gson.toJson(mediaList)
    }

    @TypeConverter
    fun stringToMediaList(data: String?): List<Media> {
        if (data == null) {
            return emptyList()
        }

        val listType = object : TypeToken<List<Media>>() {}.type

        return gson.fromJson(data, listType)
    }
}