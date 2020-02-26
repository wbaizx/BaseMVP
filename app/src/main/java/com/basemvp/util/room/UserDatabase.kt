package com.basemvp.util.room

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.basemvp.APP

/**
 * 数据库名
 */
const val DBNAME = "userDatabase.db"

@Database(entities = [User::class], version = 1)
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        private val bulid = Room.databaseBuilder(APP.appContext, UserDatabase::class.java, DBNAME).build()

        val DB_USER by lazy {
            bulid.userDao()
        }
    }
}