package com.santiagobattaglino.mvvm.codebase.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.santiagobattaglino.mvvm.codebase.data.room.dao.*
import com.santiagobattaglino.mvvm.codebase.domain.entity.*
import com.santiagobattaglino.mvvm.codebase.util.Constants

@Database(
    entities = [
        Login::class
    ],
    version = Constants.DB_VERSION
)
@TypeConverters(Converters::class)
abstract class Database : RoomDatabase() {
    abstract fun loginDAO(): LoginDAO
}