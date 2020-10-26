package ${escapeKotlinIdentifiers(packageName)}

import com.alibaba.android.arouter.facade.annotation.Route
import com.base.common.base.mvvm.BaseMVVMActivity
import com.base.common.config.RouteString
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlinx.android.synthetic.main.${layoutName}.*

@Route(path = RouteString.MVVM, name = "description")
class ${activityClass} : BaseMVVMActivity<Activity${mvvmCommonName}Binding>(){
    private val TAG = "${activityClass}"
	
    /**
     * viewModel 使用koin注入方式，fragment可以使用sharedViewModel
     */
    override val viewModel by viewModel<${mvvmCommonName}ViewModel>()

    override fun getContentView() = R.layout.${layoutName}
	
	override fun bindModelId(binding: Activity${mvvmCommonName}Binding) {
    }

    override fun initView() {
    }

    override fun initData() {
    }
}
