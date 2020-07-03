package com.basemvp.main.mvp_room

import com.base.common.base.mvp.contract.BaseModel
import com.base.common.base.mvp.contract.BasePresenter
import com.base.common.base.mvp.contract.BaseView
import com.basemvp.util.room.User

interface MvpRoomViewInterface : BaseView {
}

interface MvpRoomPresenterInterface : BasePresenter {
    fun saveData()
    fun queryData()
}

interface MvpRoomModelInterface : BaseModel {
    suspend fun insertUsers(user: User): Long
    suspend fun getAllUsers(): List<User>
}

