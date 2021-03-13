package com.santiagobattaglino.mvvm.codebase.data.repository

import android.content.SharedPreferences

class SharedPreferenceUtils(private val sharedPreferences: SharedPreferences) {

    companion object {
        const val SHARED_PREF_NAME = "shared_pref"
    }

    fun saveString(key: String, item: String) {
        sharedPreferences.edit().putString(key, item).apply()
    }

    fun saveBoolean(key: String, boolean: Boolean) {
        sharedPreferences.edit().putBoolean(key, boolean).apply()
    }

    fun saveInt(key: String, item: Int) {
        sharedPreferences.edit().putInt(key, item).apply()
    }

    fun getString(key: String): String? = sharedPreferences.getString(key, null)

    fun getBoolean(key: String): Boolean = sharedPreferences.getBoolean(key, false)

    fun getBoolean(key: String, default: Boolean): Boolean =
        sharedPreferences.getBoolean(key, default)

    fun getInt(key: String): Int = sharedPreferences.getInt(key, 0)

    fun removeString(key: String) = sharedPreferences.edit().remove(key).commit()
}