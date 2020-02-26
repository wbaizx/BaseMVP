package com.basemvp.main.mvp_room

import com.basemvp.base.mvp.BaseModelImpl
import com.basemvp.util.room.User
import com.basemvp.util.room.UserDatabase

class MvpRoomModel : BaseModelImpl(), MvpRoomModelInterface {
    override suspend fun getAllUsers(): List<User> = UserDatabase.DB_USER.getAllUsers()

    override suspend fun insertUsers(user: User): Long = UserDatabase.DB_USER.insertUsers(user)
}