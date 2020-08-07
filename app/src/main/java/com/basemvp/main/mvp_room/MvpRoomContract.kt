package com.basemvp.main.mvp_room

import com.base.common.base.mvp.contract.BaseMVPModelI
import com.base.common.base.mvp.contract.BaseMVPPresenterI
import com.base.common.base.mvp.contract.BaseMVPViewI
import com.basemvp.util.room.User

interface MvpRoomViewInterface : BaseMVPViewI {
}

interface MvpRoomPresenterInterface : BaseMVPPresenterI {
    fun saveData()
    fun queryData()
}

interface MvpRoomModelInterface : BaseMVPModelI {
    suspend fun insertUsers(user: User): Long
    suspend fun getAllUsers(): List<User>
}

