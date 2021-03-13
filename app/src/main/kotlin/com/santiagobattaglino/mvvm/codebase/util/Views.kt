package com.santiagobattaglino.mvvm.codebase.util

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.widget.CompoundButton
import android.widget.EditText

fun CompoundButton.setChecked(value: Boolean, listener: CompoundButton.OnCheckedChangeListener?) {
    setOnCheckedChangeListener(null)
    isChecked = value
    setOnCheckedChangeListener(listener)
}

fun convertDpToPixel(dp: Int, context: Context): Int {
    return dp * (context.resources.displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)
}

fun convertPixelsToDp(px: Int, context: Context): Int {
    return px / (context.resources.displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)
}

fun EditText.onChange(cb: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            cb(s.toString())
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    })
}