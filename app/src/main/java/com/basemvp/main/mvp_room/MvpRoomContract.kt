package com.basemvp.main.mvp_room

import com.basemvp.util.room.User

interface MvpRoomViewInterface {
}

interface MvpRoomPresenterInterface {
    fun saveData()
    fun queryData()
}

interface MvpRoomModelInterface {
    suspend fun insertUsers(user: User): Long
    suspend fun getAllUsers(): List<User>
}

