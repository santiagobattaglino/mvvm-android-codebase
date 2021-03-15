package com.santiagobattaglino.mvvm.codebase.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.santiagobattaglino.mvvm.codebase.data.room.dao.*
import com.santiagobattaglino.mvvm.codebase.domain.entity.*
import com.santiagobattaglino.mvvm.codebase.util.Constants

@Database(
    entities = [
        Login::class,
        Incident::class,
        Comment::class,
        Update::class,
        Notification::class,
        Stock::class,
        Product::class,
        Color::class,
        Category::class
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
    abstract fun stockDAO(): StockDAO
    abstract fun productDAO(): ProductDAO
    abstract fun colorDAO(): ColorDAO
    abstract fun categoryDAO(): CategoryDAO
}