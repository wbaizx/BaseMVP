package com.basemvp.main.mvvm

import com.base.common.base.mvvm.BaseMVVMRepository
import com.basemvp.util.room.User
import com.basemvp.util.room.UserDatabase

class MVVMDemoRepository : BaseMVVMRepository() {
    suspend fun insertUsers(user: User): Long = UserDatabase.DB_USER.insertUsers(user)

    suspend fun getAllUsers(): List<User> = UserDatabase.DB_USER.getAllUsers()
}