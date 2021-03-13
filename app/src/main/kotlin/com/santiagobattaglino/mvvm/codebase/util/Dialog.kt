package com.santiagobattaglino.mvvm.codebase.util

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.santiagobattaglino.mvvm.codebase.R

fun alertDialog(context: Context) {
    AlertDialog.Builder(context, R.style.AlertDialog)
        .setTitle("delete comment")
        .setPositiveButton("ok") { _, _ -> }
        .setNegativeButton("cancel") { _, _ -> }
        .create()
        .show()
}