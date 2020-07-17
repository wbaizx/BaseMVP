package com.base.common.view.btn

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.base.common.R
import com.base.common.util.AndroidUtil
import com.base.common.util.LogUtil

/**
 * 主要用于需要自定义 drawable 的 button，使用这个类可以避免为每个 button 配一个xml背景
 */
class ShapeButton(context: Context, attrs: AttributeSet?) : CommonButton(context, attrs) {
    private val TAG = "ShapeButton"

    constructor(context: Context) : this(context, null)

    private var bgrawable: Drawable? = null

    init {
        LogUtil.log(TAG, "init")
        setLayerType(LAYER_TYPE_SOFTWARE, null)

        val t = context.obtainStyledAttributes(attrs, R.styleable.ShapeButton)

        //普通背景
        val bgShape = GradientDrawable()
        initShape(t, bgShape)

        //水波纹效果颜色
        val rippleColor = t.getColor(R.styleable.ShapeButton_rippleColor, -1)
        if (rippleColor != -1) {
            val colorStateList = ColorStateList.valueOf(rippleColor)

            //参数2：content:普通背景
            //参数3：mask是不会被draw的，但是它会限制波纹的边界，如果为null，默认为content的边界，同上，当content为null就没有边界了
            val rippleDrawable = RippleDrawable(colorStateList, bgShape, null)
            bgrawable = rippleDrawable
        } else {
            bgrawable = bgShape
        }
        background = bgrawable
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
        val strokeColor = t.getColor(R.styleable.ShapeButton_strokeColor, ContextCompat.getColor(context, R.color.color_8B8))
        val strokeWith = t.getDimension(R.styleable.ShapeButton_strokeWith, 0f)
        val dashGap = t.getDimension(R.styleable.ShapeButton_dashGap, 0f)
        val dashWidth = t.getDimension(R.styleable.ShapeButton_dashWidth, 0f)
        LogUtil.log(TAG, "strokeColor $strokeColor  strokeWith $strokeWith")
        if (strokeWith > 0f) {
            shape.setStroke(strokeWith.toInt(), strokeColor, dashWidth, dashGap)
        }
        //圆角
        val radius = t.getDimension(R.styleable.ShapeButton_radius, AndroidUtil.dp2px(4f))
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
        //设置渐变颜色，至少需要startColor和endColor才生效，这里最多支持3种颜色
        val startColor = t.getColor(R.styleable.ShapeButton_startColor, -1)
        val centerColor = t.getColor(R.styleable.ShapeButton_centerColor, -1)
        val endColor = t.getColor(R.styleable.ShapeButton_endColor, -1)
        if (startColor != -1 && endColor != -1) {
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
            if (centerColor != -1) {
                shape.setColors(intArrayOf(startColor, centerColor, endColor))
            } else {
                shape.setColors(intArrayOf(startColor, endColor))
            }
        } else {
            //背景色
            val solidColor = t.getColor(R.styleable.ShapeButton_solidColor, ContextCompat.getColor(context, R.color.color_DCD))
            LogUtil.log(TAG, "shapeType $solidColor")
            shape.setColor(solidColor)
        }

//        shape.setAlpha(20)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //View.resolveSize() //模板方法
        //setMeasuredDimension
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    }

    /**
     * 如果代码中动态改变了 background ，需要手动还原
     */
    fun revert() {
        background = bgrawable
    }
}