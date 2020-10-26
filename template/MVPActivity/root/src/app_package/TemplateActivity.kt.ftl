package ${escapeKotlinIdentifiers(packageName)}

import com.alibaba.android.arouter.facade.annotation.Route
import com.base.common.base.mvp.BaseMVPActivity
import com.base.common.config.RouteString
import kotlinx.android.synthetic.main.${layoutName}.*

@Route(path = RouteString.MVP, name = "description")
class ${activityClass} : BaseMVPActivity<${mvpCommonName}PresenterInterface>(), ${mvpCommonName}ViewInterface {
    private val TAG = "${activityClass}"
	
    override var presenter: ${mvpCommonName}PresenterInterface? = ${mvpCommonName}Presenter(this)

    override fun getContentView() = R.layout.${layoutName}

    override fun initView() {
    }

    override fun initData() {
    }
}
