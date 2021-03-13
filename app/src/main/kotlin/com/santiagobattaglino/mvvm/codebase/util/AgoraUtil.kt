package com.santiagobattaglino.mvvm.codebase.util

import android.content.Context
import android.os.Build
import android.os.Environment
import java.io.File

object AgoraUtil {
    private const val LOG_FOLDER_NAME = "log"
    private const val LOG_FILE_NAME = "agora-rtc.log"

    /**
     * Initialize the log folder
     * @param context Context to find the accessible file folder
     * @return the absolute path of the log file
     */
    fun initializeLogFile(context: Context): String {
        var folder: File?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            folder = File(
                context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
                LOG_FOLDER_NAME
            )
        } else {
            val path = Environment.getExternalStorageDirectory()
                .absolutePath + File.separator +
                    context.packageName + File.separator +
                    LOG_FOLDER_NAME
            folder = File(path)
            if (!folder.exists() && !folder.mkdir()) folder = null
        }
        return if (folder != null && !folder.exists() && !folder.mkdir()) "" else File(
            folder,
            LOG_FILE_NAME
        ).absolutePath
    }

    // TODO ask backend to add this uid inside incident.videoStream object
    fun getBroadcasterUid(incidentId: Int): Int {
        return incidentId * 100 + 10
    }

    // TODO ask backend to add this uid inside incident.videoStream object
    fun getRecorderUid(incidentId: Int): Int {
        return incidentId * 100
    }
}