package com.base.common.update

import com.base.common.util.LogUtil
import com.base.common.util.http.BaseHttp

object UpdateHttp {
    val updateRequest: UpdateAPI by lazy {
        LogUtil.log("UpdateHttp", "updateRequest")
        BaseHttp.simpleRetrofit.create(UpdateAPI::class.java)
    }
}