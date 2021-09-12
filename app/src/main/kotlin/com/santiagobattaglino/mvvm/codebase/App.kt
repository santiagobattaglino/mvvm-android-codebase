package com.santiagobattaglino.mvvm.codebase

//import com.google.firebase.messaging.FirebaseMessaging
import android.app.Application
import com.santiagobattaglino.mvvm.codebase.di.appModule
import com.santiagobattaglino.mvvm.codebase.util.Constants
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

@ExperimentalCoroutinesApi
class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@App)
            modules(appModule)
        }

        // TODO only for develop debug distribuition.
        // Stetho.initializeWithDefaults(this)

        // Notifications
        /*val channelId = if (BuildConfig.DEBUG) {
            Constants.DEV_NOTIFICATION_CHANNEL_ID
        } else {
            Constants.DEFAULT_NOTIFICATION_CHANNEL_ID
        }*/
        //FirebaseMessaging.getInstance().subscribeToTopic(channelId)
    }
}