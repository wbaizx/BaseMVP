package com.basemvp.main.mvp

import com.base.common.base.mvp.BaseMVPPresenter
import com.base.common.util.log
import com.basemvp.util.room.User

class MVPDemoPresenter(view: MVPDemoViewInterface?) :
    BaseMVPPresenter<MVPDemoViewInterface, MVPDemoModelInterface>(view, MVPDemoModel()), MVPDemoPresenterInterface {
    private val TAG = "MvpRoomPresenter"

    override fun saveData() {
        runTaskDialog({ model.insertUsers(User(2, "o", 3)) }, {
            log(TAG, it)
        })
    }

    override fun queryData() {
        runTask({
            model.getAllUsers().forEach { user -> log(TAG, user) }
        })
    }
}