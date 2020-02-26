package com.basemvp.main.mvp_room

import com.basemvp.base.mvp.BasePresenterImpl
import com.basemvp.util.LogUtil
import com.basemvp.util.room.User

class MvpRoomPresenter(view: MvpRoomViewInterface?) :
    BasePresenterImpl<MvpRoomViewInterface, MvpRoomModelInterface>(view, MvpRoomModel()), MvpRoomPresenterInterface {
    private val TAG = "MvpRoomPresenter"

    override fun saveData() {
        launchTaskDialog({ model?.insertUsers(User(3, "v", 4)) }, {
            LogUtil.log(TAG, it)
        })
    }

    override fun queryData() {
        launchTask({
            model?.getAllUsers()?.forEach { user -> LogUtil.log(TAG, user) }
        })
    }
}