package com.base.common.view.btn

import android.content.Context
import android.graphics.drawable.StateListDrawable
import android.util.AttributeSet
import com.base.common.R

/**
 * 主要用于 按下、禁用和正常状态都为 Drawable 的情况
 */
class ShapeDrawableButton(context: Context, attrs: AttributeSet?) : CommonButton(context, attrs) {

    constructor(context: Context) : this(context, null)

    init {
        val t = context.obtainStyledAttributes(attrs, R.styleable.ShapeDrawableButton)

        val stalistDrawable = StateListDrawable()

        //按下状态
        val pressed = android.R.attr.state_pressed
        val pressedDrawable = t.getDrawable(R.styleable.ShapeDrawableButton_pressedDrawable)
        stalistDrawable.addState(intArrayOf(pressed), pressedDrawable)

        //禁用状态
        val disable = android.R.attr.state_enabled
        val disableDrawable = t.getDrawable(R.styleable.ShapeDrawableButton_disableDrawable)
        stalistDrawable.addState(intArrayOf(-disable), disableDrawable)

        //普通状态
        val normalDrawable = t.getDrawable(R.styleable.ShapeDrawableButton_normalDrawable)
        stalistDrawable.addState(intArrayOf(), normalDrawable)

        background = stalistDrawable

        t.recycle()
    }
}