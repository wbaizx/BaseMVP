package com.base.common.util

import android.content.Context
import android.content.SharedPreferences
import com.base.common.BaseAPP

class SharedPreferencesUtil {
    class KeyValue<T : Comparable<T>>(val key: String)

    companion object {
        val LOGIN = KeyValue<Boolean>("is_login")

        private val sharedPreferences: SharedPreferences by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            BaseAPP.baseAppContext.getSharedPreferences("BASE", Context.MODE_PRIVATE)
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

        fun getBoolean(key: KeyValue<Boolean>, defData: Boolean) = sharedPreferences.getBoolean(key.key, defData)

        fun getString(key: KeyValue<String>, defData: String) = sharedPreferences.getString(key.key, defData)

        fun getInt(key: KeyValue<Int>, defData: Int) = sharedPreferences.getInt(key.key, defData)

        fun getFloat(key: KeyValue<Float>, defData: Float) = sharedPreferences.getFloat(key.key, defData)
    }
}