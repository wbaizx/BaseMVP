package com.basemvp.main.mvp

import com.base.common.base.mvp.contract.BaseMVPModelI
import com.base.common.base.mvp.contract.BaseMVPPresenterI
import com.base.common.base.mvp.contract.BaseMVPViewI
import com.basemvp.util.room.User

interface MVPDemoViewInterface : BaseMVPViewI {
}

interface MVPDemoPresenterInterface : BaseMVPPresenterI {
    fun saveData()
    fun queryData()
}

interface MVPDemoModelInterface : BaseMVPModelI {
    suspend fun insertUsers(user: User): Long
    suspend fun getAllUsers(): List<User>
}

