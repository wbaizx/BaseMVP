package com.basemvp.main.mvp

import com.base.common.base.mvp.BaseMVPPresenter
import com.base.common.util.log
import com.basemvp.util.room.User

class MVPDemoPresenter(view: MVPDemoViewInterface?) :
    BaseMVPPresenter<MVPDemoViewInterface, MVPDemoModelInterface>(view, MVPDemoModel()), MVPDemoPresenterInterface {
    private val TAG = "MvpRoomPresenter"

    override fun saveData() {
        runTask(bgAction = { model.insertUsers(User(2, "o", 3)) }, uiAction = { log(TAG, it) })
    }

    override fun queryData() {
        runTask(isShowDialog = false, bgAction = {
            model.getAllUsers().forEach { user -> log(TAG, user) }
        })
    }
}