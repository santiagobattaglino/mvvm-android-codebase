package com.santiagobattaglino.mvvm.codebase.data.room

import com.santiagobattaglino.mvvm.codebase.util.Constants
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.santiagobattaglino.mvvm.codebase.data.room.dao.*
import com.santiagobattaglino.mvvm.codebase.domain.entity.*

@Database(
    entities = [
        Login::class,
        Incident::class,
        Comment::class,
        Update::class,
        Notification::class
    ],
    version = Constants.DB_VERSION
)
@TypeConverters(Converters::class)
abstract class Database : RoomDatabase() {
    abstract fun loginDAO(): LoginDAO
    abstract fun incidentDAO(): IncidentDAO
    abstract fun commentDAO(): CommentDAO
    abstract fun updateDAO(): UpdateDAO
    abstract fun notificationDAO(): NotificationDAO
}