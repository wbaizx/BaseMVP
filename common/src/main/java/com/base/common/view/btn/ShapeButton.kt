package com.base.common.view.btn

import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.util.AttributeSet
import com.base.common.R
import com.base.common.util.LogUtil
import com.base.common.view.btn.CommonButton

/**
 * 主要用于需要自定义 drawable 的 button，使用这个类可以避免为每个 button 配一个xml背景
 * 如果代码中动态改变了 background ，需要手动还原
 *
 * 目前这个 button 没有适配合适的按下状态和禁用状态背景，后面有好的方案在弄下
 */
class ShapeButton(context: Context, attrs: AttributeSet?) : CommonButton(context, attrs) {
    private val TAG = "ShapeButton"

    private val  stalistDrawable = StateListDrawable()

    constructor(context: Context) : this(context, null)

    init {
        val t = context.obtainStyledAttributes(attrs, R.styleable.ShapeButton)

        val bgShape = GradientDrawable()
        initShape(t, bgShape)

        stalistDrawable.addState(intArrayOf(), bgShape)

        background = stalistDrawable

        t.recycle()
    }

    private fun initShape(t: TypedArray, shape: GradientDrawable) {
        //形状
        val shapeType = t.getInt(R.styleable.ShapeButton_shapeType, 0)
        LogUtil.log(TAG, "shapeType $shapeType")
        when (shapeType) {
            0 -> shape.setShape(GradientDrawable.RECTANGLE)
            1 -> shape.setShape(GradientDrawable.OVAL)
            2 -> shape.setShape(GradientDrawable.LINE)
        }
        //边框
        val strokeColor = t.getColor(R.styleable.ShapeButton_strokeColor, 0xb2898989.toInt())
        val strokeWith = t.getDimension(R.styleable.ShapeButton_strokeWith, 0f)
        val dashGap = t.getDimension(R.styleable.ShapeButton_dashGap, 0f)
        val dashWidth = t.getDimension(R.styleable.ShapeButton_dashWidth, 0f)
        LogUtil.log(TAG, "strokeColor $strokeColor  strokeWith $strokeWith")
        shape.setStroke(strokeWith.toInt(), strokeColor, dashWidth, dashGap)
        //圆角
        val radius = t.getDimension(R.styleable.ShapeButton_radius, 0f)
        LogUtil.log(TAG, "radius $radius")
        val topLeftRadius = t.getDimension(R.styleable.ShapeButton_topLeftRadius, radius)
        val topRightRadius = t.getDimension(R.styleable.ShapeButton_topRightRadius, radius)
        val bottomRightRadius = t.getDimension(R.styleable.ShapeButton_bottomLeftRadius, radius)
        val bottomLeftRadius = t.getDimension(R.styleable.ShapeButton_bottomRightRadius, radius)
        shape.cornerRadii = floatArrayOf(
            topLeftRadius,
            topLeftRadius,
            topRightRadius,
            topRightRadius,
            bottomRightRadius,
            bottomRightRadius,
            bottomLeftRadius,
            bottomLeftRadius
        )
        //设置线性渐变，除此之外还有：GradientDrawable.SWEEP_GRADIENT（扫描式渐变），GradientDrawable.RADIAL_GRADIENT（圆形渐变）
        shape.setGradientType(GradientDrawable.LINEAR_GRADIENT)
        //设置渐变方向
        val colorAngle = t.getInt(R.styleable.ShapeButton_colorAngle, 6)
        LogUtil.log(TAG, "colorAngle $colorAngle")
        when (colorAngle) {
            0 -> shape.setOrientation(GradientDrawable.Orientation.TOP_BOTTOM)
            1 -> shape.setOrientation(GradientDrawable.Orientation.TR_BL)
            2 -> shape.setOrientation(GradientDrawable.Orientation.RIGHT_LEFT)
            3 -> shape.setOrientation(GradientDrawable.Orientation.BR_TL)
            4 -> shape.setOrientation(GradientDrawable.Orientation.BOTTOM_TOP)
            5 -> shape.setOrientation(GradientDrawable.Orientation.BL_TR)
            6 -> shape.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT)
            7 -> shape.setOrientation(GradientDrawable.Orientation.TL_BR)
        }
        //设置渐变颜色，至少需要startColor和endColor才生效
        val startColor = t.getColor(R.styleable.ShapeButton_startColor, -1)
        val centerColor = t.getColor(R.styleable.ShapeButton_centerColor, -1)
        val endColor = t.getColor(R.styleable.ShapeButton_endColor, -1)
        if (startColor != -1 && endColor != -1) {
            if (centerColor != -1) {
                shape.setColors(intArrayOf(startColor, centerColor, endColor))
            } else {
                shape.setColors(intArrayOf(startColor, endColor))
            }
        } else {
            //背景色
            val solidColor = t.getColor(R.styleable.ShapeButton_solidColor, 0xffffffff.toInt())
            LogUtil.log(TAG, "shapeType $solidColor")
            shape.setColor(solidColor)
        }

//        shape.setAlpha(20)
    }
}