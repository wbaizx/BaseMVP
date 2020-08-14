package com.base.common.view.btn

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.animation.LinearInterpolator
import com.base.common.R
import com.base.common.util.LogUtil

/**
 * 所有 button 的基类，项目所有使用的 button 都应该使用它，或者继承它
 * 主要包含了防止重复点击，默认按下效果
 */
open class CommonButton(context: Context, attrs: AttributeSet?) : androidx.appcompat.widget.AppCompatButton(context, attrs) {
    private val TAG = "CommonButton"

    constructor(context: Context) : this(context, null)

    private val interpolator by lazy { LinearInterpolator() }

    /**
     * 是否允许重复快速点击
     */
    var allowRepeatedClick = false
    private var lastTime = 0L

    /**
     * 点击时间间隔
     */
    var clickInterval = 800L

    /**
     * 是否允默认按下动画效果
     */
    var allowPressEffect = true

    init {
        LogUtil.log(TAG, "init")

        //允许小写
        isAllCaps = false

        val t = context.obtainStyledAttributes(attrs, R.styleable.CommonButton)
        allowRepeatedClick = t.getBoolean(R.styleable.CommonButton_allowRepeatedClick, false)
        allowPressEffect = t.getBoolean(R.styleable.CommonButton_allowPressEffect, true)
        t.recycle()
    }

    /**
     * 防止重复点击
     */
    override fun performClick(): Boolean {
        if (!allowRepeatedClick) {
            if (System.currentTimeMillis() - lastTime > clickInterval) {
                lastTime = System.currentTimeMillis()
            } else {
                LogUtil.log(TAG, "performClick false")
                return false
            }
        }
        return super.performClick()
    }

    /**
     * ViewPropertyAnimator、ObjectAnimator、ValueAnimator三种 Animator是一种递进的关系：从左到右依次变得更加难用，也更加灵活
     * ViewPropertyAnimator 和 ObjectAnimator 的内部实现其实都是 ValueAnimator，它们三个的性能并没有差别。差别只是使用的便捷性以及功能的灵活性
     * 实际使用时候的选择，只要遵循一个原则就行：尽量用简单的
     * 能用 View.animate() 实现就不用 ObjectAnimator ， 能用 ObjectAnimator 就不用 ValueAnimator。
     */
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (isEnabled && allowPressEffect) {
            if (event?.action == MotionEvent.ACTION_DOWN) {
                LogUtil.log(TAG, "onTouchEvent ACTION_DOWN $isEnabled")
                animate().setDuration(60).scaleX(0.95f).scaleY(0.95f).alpha(0.8f).setInterpolator(interpolator).start()
            } else if (event?.action == MotionEvent.ACTION_UP || event?.action == MotionEvent.ACTION_CANCEL) {
                LogUtil.log(TAG, "onTouchEvent ACTION_DOWN $isEnabled")
                animate().setDuration(60).scaleX(1.0f).scaleY(1.0f).alpha(1.0f).setInterpolator(interpolator).start()
            }
        }
        return super.onTouchEvent(event)
    }
}