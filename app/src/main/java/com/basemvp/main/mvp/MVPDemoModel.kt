package com.basemvp.main.mvp

import com.base.common.base.mvp.BaseMVPModel
import com.basemvp.util.room.User
import com.basemvp.util.room.UserDatabase

class MVPDemoModel : BaseMVPModel(), MVPDemoModelInterface {
    override suspend fun getAllUsers(): List<User> = UserDatabase.DB_USER.getAllUsers()

    override suspend fun insertUsers(user: User): Long = UserDatabase.DB_USER.insertUsers(user)
}