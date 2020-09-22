package ${escapeKotlinIdentifiers(packageName)}

import com.base.common.base.mvp.BaseMVPPresenter

class ${mvpCommonName}Presenter(view: ${mvpCommonName}ViewInterface?) :
    BaseMVPPresenter<${mvpCommonName}ViewInterface, ${mvpCommonName}ModelInterface>(view, ${mvpCommonName}Model()), ${mvpCommonName}PresenterInterface {
    private val TAG = "${mvpCommonName}Presenter"

}
