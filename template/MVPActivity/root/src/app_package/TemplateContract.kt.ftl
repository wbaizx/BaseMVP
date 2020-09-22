package ${escapeKotlinIdentifiers(packageName)}

import com.base.common.base.mvp.contract.BaseMVPModelI
import com.base.common.base.mvp.contract.BaseMVPPresenterI
import com.base.common.base.mvp.contract.BaseMVPViewI

interface ${mvpCommonName}ViewInterface : BaseMVPViewI {
}

interface ${mvpCommonName}PresenterInterface : BaseMVPPresenterI {
}

interface ${mvpCommonName}ModelInterface : BaseMVPModelI {
}