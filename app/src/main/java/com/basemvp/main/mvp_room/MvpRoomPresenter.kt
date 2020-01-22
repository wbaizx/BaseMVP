package com.basemvp.main.mvp_room

import com.basemvp.base.mvp.BasePresenterImpl

class MvpRoomPresenter(view: MvpRoomViewInterface?) :
    BasePresenterImpl<MvpRoomViewInterface, MvpRoomModelInterface>(view, MvpRoomModel()), MvpRoomPresenterInterface {

    override fun saveData() {
        launchUI()
    }
}