package ${escapeKotlinIdentifiers(packageName)}

import androidx.lifecycle.MutableLiveData
import com.base.common.base.mvvm.BaseMVVMViewModel

class ${mvvmCommonName}ViewModel(private val repository: ${mvvmCommonName}Repository) : BaseMVVMViewModel(){

//    val bean: MutableLiveData<String> by lazy { MutableLiveData<String>() }

//    fun test() = runTaskDialog({
//		repository.test()
//		bean.postValue("success")
//    })
}