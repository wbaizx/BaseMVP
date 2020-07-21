package com.base.common.view.btn

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Region
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.RippleDrawable
import android.os.Build
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.base.common.R
import com.base.common.util.AndroidUtil
import com.base.common.util.LogUtil
import kotlin.math.min

/**
 * 主要用于需要自定义 drawable 的 button，使用这个类可以避免为每个 button 配一个xml背景
 * 背景阴影需要占中view本身宽高， 字体阴影可以使用自带属性 android:shadowColor="" android:shadowRadius=""
 */
class ShapeButton(context: Context, attrs: AttributeSet?) : CommonButton(context, attrs) {
    private val TAG = "ShapeButton"

    constructor(context: Context) : this(context, null)

    /**
     * view 的背景
     */
    private var bgDrawable: Drawable? = null

    /**
     * 背景圆角
     */
    private var cornerArray: FloatArray? = null

    /**
     * 阴影画笔
     */
    private var shadowPaint: Paint? = null

    /**
     * 画阴影需要裁减画布的路径
     */
    private var shadowClipPath: Path? = null

    /**
     * 背景阴影半径
     */
    private var bgShadowRadius: Float = 0f

    /**
     * 背景阴影半径偏移量
     */
    private var bgShadowOffsetY: Float = 0f

    /**
     * 背景阴影偏移量
     */
    private var bgShadowOffsetX: Float = 0f

    init {
        LogUtil.log(TAG, "init")
        //硬件加速，主要用于阴影绘制
        setLayerType(View.LAYER_TYPE_SOFTWARE, null)

        val t = context.obtainStyledAttributes(attrs, R.styleable.ShapeButton)

        //----------普通背景---------------
        val bgShape = GradientDrawable()
        //形状
        val shapeType = t.getInt(R.styleable.ShapeButton_shapeType, 0)
        LogUtil.log(TAG, "shapeType $shapeType")
        when (shapeType) {
            0 -> bgShape.setShape(GradientDrawable.RECTANGLE)
            1 -> bgShape.setShape(GradientDrawable.OVAL)
            2 -> bgShape.setShape(GradientDrawable.LINE)
        }
        //边框
        val strokeColor = t.getColor(R.styleable.ShapeButton_strokeColor, ContextCompat.getColor(context, R.color.color_8B8))
        val strokeWith = t.getDimension(R.styleable.ShapeButton_strokeWith, 0f)
        val dashGap = t.getDimension(R.styleable.ShapeButton_dashGap, 0f)
        val dashWidth = t.getDimension(R.styleable.ShapeButton_dashWidth, 0f)
        LogUtil.log(TAG, "strokeColor $strokeColor  strokeWith $strokeWith")
        if (strokeWith > 0f) {
            bgShape.setStroke(strokeWith.toInt(), strokeColor, dashWidth, dashGap)
        }
        //圆角
        val radius = t.getDimension(R.styleable.ShapeButton_radius, AndroidUtil.dp2px(4f))
        LogUtil.log(TAG, "radius $radius")
        val topLeftRadius = t.getDimension(R.styleable.ShapeButton_topLeftRadius, radius)
        val topRightRadius = t.getDimension(R.styleable.ShapeButton_topRightRadius, radius)
        val bottomRightRadius = t.getDimension(R.styleable.ShapeButton_bottomLeftRadius, radius)
        val bottomLeftRadius = t.getDimension(R.styleable.ShapeButton_bottomRightRadius, radius)
        cornerArray = floatArrayOf(
            topLeftRadius,
            topLeftRadius,
            topRightRadius,
            topRightRadius,
            bottomRightRadius,
            bottomRightRadius,
            bottomLeftRadius,
            bottomLeftRadius
        )
        bgShape.cornerRadii = cornerArray
        //设置渐变颜色，至少需要startColor和endColor才生效，这里最多支持3种颜色
        val startColor = t.getColor(R.styleable.ShapeButton_startColor, -1)
        val centerColor = t.getColor(R.styleable.ShapeButton_centerColor, -1)
        val endColor = t.getColor(R.styleable.ShapeButton_endColor, -1)
        if (startColor != -1 && endColor != -1) {
            //设置线性渐变，除此之外还有：GradientDrawable.SWEEP_GRADIENT（扫描式渐变），GradientDrawable.RADIAL_GRADIENT（圆形渐变）
            bgShape.setGradientType(GradientDrawable.LINEAR_GRADIENT)
            //设置渐变方向
            val colorAngle = t.getInt(R.styleable.ShapeButton_colorAngle, 6)
            LogUtil.log(TAG, "colorAngle $colorAngle")
            when (colorAngle) {
                0 -> bgShape.setOrientation(GradientDrawable.Orientation.TOP_BOTTOM)
                1 -> bgShape.setOrientation(GradientDrawable.Orientation.TR_BL)
                2 -> bgShape.setOrientation(GradientDrawable.Orientation.RIGHT_LEFT)
                3 -> bgShape.setOrientation(GradientDrawable.Orientation.BR_TL)
                4 -> bgShape.setOrientation(GradientDrawable.Orientation.BOTTOM_TOP)
                5 -> bgShape.setOrientation(GradientDrawable.Orientation.BL_TR)
                6 -> bgShape.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT)
                7 -> bgShape.setOrientation(GradientDrawable.Orientation.TL_BR)
            }
            if (centerColor != -1) {
                bgShape.setColors(intArrayOf(startColor, centerColor, endColor))
            } else {
                bgShape.setColors(intArrayOf(startColor, endColor))
            }
        } else {
            //纯背景色
            val solidColor = t.getColor(R.styleable.ShapeButton_solidColor, ContextCompat.getColor(context, R.color.color_DCD))
            LogUtil.log(TAG, "shapeType $solidColor")
            bgShape.setColor(solidColor)
        }
        bgDrawable = bgShape


        //----------需要阴影背景---------------
        bgShadowRadius = t.getDimension(R.styleable.ShapeButton_bgShadowRadius, 0f)
        if (bgShadowRadius != 0f) {
            bgShadowOffsetY = t.getDimension(R.styleable.ShapeButton_bgShadowOffsetY, min(0f, bgShadowRadius))
            bgShadowOffsetX = t.getDimension(R.styleable.ShapeButton_bgShadowOffsetX, min(0f, bgShadowRadius))
            val bgShadowColor = t.getColor(R.styleable.ShapeButton_bgShadowColor, ContextCompat.getColor(context, R.color.color_shadow))

            shadowPaint = Paint()
            shadowPaint?.isAntiAlias = true
            shadowPaint?.setShadowLayer(bgShadowRadius, bgShadowOffsetX, bgShadowOffsetY, bgShadowColor)

            shadowClipPath = Path()

            val layerDrawable = LayerDrawable(arrayOf(bgDrawable))
            //为将要画的背景阴影腾出地方
            layerDrawable.setLayerInset(
                0,
                bgShadowRadius.toInt() - bgShadowOffsetX.toInt(),
                bgShadowRadius.toInt() - bgShadowOffsetY.toInt(),
                bgShadowRadius.toInt() + bgShadowOffsetX.toInt(),
                bgShadowRadius.toInt() + bgShadowOffsetY.toInt()
            )
            bgDrawable = layerDrawable
        }


        //----------水波纹背景---------------
        //水波纹效果颜色
        val rippleColor = t.getColor(R.styleable.ShapeButton_rippleColor, -1)
        if (rippleColor != -1) {
            val colorStateList = ColorStateList.valueOf(rippleColor)

            //参数2：content:普通背景
            //参数3：mask是不会被draw的，但是它会限制波纹的边界，如果为null，默认为content的边界，同上，当content为null就没有边界了
            bgDrawable = RippleDrawable(colorStateList, bgDrawable, null)
        }

        revert()

        t.recycle()
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)

        //绘制背景阴影
        if (shadowClipPath != null && shadowPaint != null && cornerArray != null) {
            canvas?.save()

            shadowClipPath!!.reset()
            shadowClipPath!!.addRoundRect(
                bgShadowRadius - bgShadowOffsetX,
                bgShadowRadius - bgShadowOffsetY,
                measuredWidth - bgShadowRadius - bgShadowOffsetX,
                measuredHeight - bgShadowRadius - bgShadowOffsetY,
                cornerArray!!,
                Path.Direction.CW
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                canvas?.clipOutPath(shadowClipPath!!)
            } else {
                canvas?.clipPath(shadowClipPath!!, Region.Op.DIFFERENCE)
            }

            canvas?.drawPath(shadowClipPath!!, shadowPaint!!)

            canvas?.restore()
        }
    }

    /**
     * 如果代码中动态改变了 background ，需要手动还原
     */
    fun revert() {
        background = bgDrawable
    }
}