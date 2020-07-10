package com.base.common.view

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import com.base.common.util.LogUtil

class ShapeButton(context: Context?, attrs: AttributeSet?) : androidx.appcompat.widget.AppCompatButton(context, attrs) {
    private val TAG = "ShapeButton"

    constructor(context: Context?) : this(context, null)

    init {
        val bgShape = GradientDrawable()

        background = bgShape
    }

}