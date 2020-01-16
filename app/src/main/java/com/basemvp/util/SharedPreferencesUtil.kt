package com.basemvp.util

import android.content.Context
import android.content.SharedPreferences
import com.basemvp.APP

class SharedPreferencesUtil {
    class KeyValue<T : Comparable<T>>(val key: String)

    companion object {
        val B = KeyValue<Boolean>("aaa")
        val S = KeyValue<String>("bbb")
        val F = KeyValue<Float>("ccc")
        val I = KeyValue<Int>("ddd")

        private val sharedPreferences: SharedPreferences by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            APP.appContext.getSharedPreferences("BASE", Context.MODE_PRIVATE)
        }

        private val edit by lazy { sharedPreferences.edit() }

        fun <T : Comparable<T>> putData(key: KeyValue<T>, data: T) {
            when (data) {
                is String -> edit.putString(key.key, data)
                is Int -> edit.putInt(key.key, data)
                is Boolean -> edit.putBoolean(key.key, data)
                is Float -> edit.putFloat(key.key, data)
                is Long -> edit.putLong(key.key, data)
            }
            edit.apply()
        }

        fun <T : Comparable<T>> getData(key: KeyValue<T>, defData: T): Comparable<*>? {
            return when (defData) {
                is String -> sharedPreferences.getString(key.key, defData)
                is Int -> sharedPreferences.getInt(key.key, defData)
                is Boolean -> sharedPreferences.getBoolean(key.key, defData)
                is Float -> sharedPreferences.getFloat(key.key, defData)
                is Long -> sharedPreferences.getLong(key.key, defData)
                else -> null
            }
        }
    }
}