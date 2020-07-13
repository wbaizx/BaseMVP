package com.base.common.view.btn

import android.content.Context
import android.util.AttributeSet
import com.base.common.util.LogUtil

/**
 * 所有 button 的基类，项目所有使用的 button 都应该使用它，或者继承它
 * 主要包含了防止重复点击
 */
open class CommonButton(context: Context, attrs: AttributeSet?) : androidx.appcompat.widget.AppCompatButton(context, attrs) {
    private val TAG = "CommonButton"

    private var lastTime = 0L

    constructor(context: Context) : this(context, null)

    init {
        isAllCaps = false
    }

    /**
     * 防止重复点击
     */
    override fun performClick(): Boolean {
        if (System.currentTimeMillis() - lastTime > 1000L) {
            lastTime = System.currentTimeMillis()
            return super.performClick()
        }else{
            LogUtil.log(TAG, "performClick false")
        }
        return false
    }
}