package com.basemvp.main.mvp_room

import com.base.common.base.mvp.BaseMVPPresenter
import com.base.common.util.LogUtil
import com.basemvp.util.room.User

class MvpRoomPresenter(view: MvpRoomViewInterface?) :
    BaseMVPPresenter<MvpRoomViewInterface, MvpRoomModelInterface>(view, MvpRoomModel()), MvpRoomPresenterInterface {
    private val TAG = "MvpRoomPresenter"

    override fun saveData() {
        runTaskDialog({ model.insertUsers(User(2, "o", 3)) }, {
            LogUtil.log(TAG, it)
        })
    }

    override fun queryData() {
        runTask({
            model.getAllUsers().forEach { user -> LogUtil.log(TAG, user) }
        })
    }
}