package com.basemvp.main.mvvm

import androidx.lifecycle.MutableLiveData
import com.base.common.base.mvvm.BaseMVVMViewModel
import com.basemvp.util.room.User

class MVVMDemoViewModel(private val repository: MVVMDemoRepository) : BaseMVVMViewModel() {

    val name: MutableLiveData<String> by lazy { MutableLiveData<String>() }

    fun saveData() = runTask(action = {
        repository.insertUsers(User(9, "4", 6))
        name.postValue("存入成功")
    })


    fun queryData() = runTask(isShowDialog = false, action = {
        name.postValue("${repository.getAllUsers().size}")
    })

}