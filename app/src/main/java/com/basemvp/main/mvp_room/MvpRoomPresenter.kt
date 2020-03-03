package com.basemvp.main.mvp_room

import com.base.common.base.mvp.BasePresenterImpl
import com.base.common.util.LogUtil
import com.basemvp.util.room.User

class MvpRoomPresenter(view: MvpRoomViewInterface?) :
    BasePresenterImpl<MvpRoomViewInterface, MvpRoomModelInterface>(view, MvpRoomModel()), MvpRoomPresenterInterface {
    private val TAG = "MvpRoomPresenter"

    override fun saveData() {
        launchTaskDialog({ model?.insertUsers(User(1, "p", 2)) }, {
            LogUtil.log(TAG, it)
        })
    }

    override fun queryData() {
        launchTask({
            model?.getAllUsers()?.forEach { user -> LogUtil.log(TAG, user) }
        })
    }
}