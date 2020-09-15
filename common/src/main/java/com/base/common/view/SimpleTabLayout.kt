package com.base.common.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View.OnClickListener
import android.view.animation.OvershootInterpolator
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.TextView
import com.base.common.util.AndroidUtil
import com.base.common.util.log

class SimpleTabLayout(context: Context, attrs: AttributeSet) : HorizontalScrollView(context, attrs) {
    private val TAG = "SimpleTabLayout"
    private val layout = LinearLayout(context)

    private val textViewList = arrayListOf<TextView>()

    private val unSelectSize = AndroidUtil.sp2px(16f)
    private val unSelectColor = Color.parseColor("#6D7278")

    private val selectSize = AndroidUtil.sp2px(18f)
    private val selectColor = Color.parseColor("#000000")

    private var currentPos = -1
    private var currentTex: TextView? = null

    //下划线高度
    private var lineHeight = AndroidUtil.dp2px(3f)
    //下划线宽度
    private var lineWidth = AndroidUtil.dp2px(35f)
    //item宽度
    private var itemWidth = AndroidUtil.dp2px(69f)
    //屏幕宽度
    private var screenWidth = AndroidUtil.getScreenWidth()

    //HorizontalScrollView总宽度
    private var tabWidth = 0f

    //下划线绘制起点X
    private var lineX = 0f
    //下划线动画结束最终会到的起点X
    private var lineToX = 0f

    private var lineAnimator: ValueAnimator? = null

    private val mPaint = Paint()

    private var listener: ((Int) -> Unit)? = null

    private val itemListener = OnClickListener {
        val tag = it.getTag() as Int
        log(TAG, "itemClick $tag")
        if (tag != currentPos) {
            listener?.invoke(tag)
            movePosition(tag)
        }
    }

    init {
        //水平方向的水平滚动条是否显示
        isHorizontalScrollBarEnabled = false

        mPaint.style = Paint.Style.FILL
        mPaint.strokeCap = Paint.Cap.ROUND
        mPaint.isAntiAlias = true
        mPaint.strokeWidth = lineHeight
        mPaint.color = Color.parseColor("#55BCFF")

        layout.orientation = LinearLayout.HORIZONTAL
        val layoutParams =
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT)
        layout.layoutParams = layoutParams
        addView(layout)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
    }

    fun setData(textList: ArrayList<String>) {
        layout.removeAllViews()
        textViewList.clear()

        if (currentPos == -1) {
            currentPos = 0
        }

        lineX = calculationLineX(currentPos)
        for (index in textList.indices) {
            val tex = TextView(context)
            tex.text = textList[index]
            tex.gravity = Gravity.CENTER
            tex.tag = index
            tex.setOnClickListener(itemListener)

            val layoutParams =
                LinearLayout.LayoutParams(itemWidth.toInt(), LinearLayout.LayoutParams.MATCH_PARENT)
            tex.layoutParams = layoutParams

            if (currentPos == index) {
                setSelectTextView(tex)
                currentTex = tex
            } else {
                setUnSelectTextView(tex)
            }

            layout.addView(tex)
            textViewList.add(tex)
        }
        tabWidth = itemWidth * textList.size
        log(TAG, "width  $lineWidth -- $itemWidth -- $screenWidth --$tabWidth")
        log(TAG, "current  $currentPos -- $currentTex")
    }

    private fun setUnSelectTextView(tex: TextView?) {
        tex?.setTextSize(TypedValue.COMPLEX_UNIT_PX, unSelectSize)
        tex?.setTextColor(unSelectColor)
        tex?.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
    }

    private fun setSelectTextView(tex: TextView?) {
        tex?.setTextSize(TypedValue.COMPLEX_UNIT_PX, selectSize)
        tex?.setTextColor(selectColor)
        tex?.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
    }

    /**
     * 根据选中位置计算下划线起点x坐标
     */
    private fun calculationLineX(pos: Int) = itemWidth * pos + (itemWidth - lineWidth) / 2

    fun setPosition(toPos: Int) {
        if (toPos != currentPos) {
            movePosition(toPos)
        }
    }

    private fun movePosition(toPos: Int) {
        if (!textViewList.isNullOrEmpty()) {
            log(TAG, "movePosition $toPos")

            setUnSelectTextView(currentTex)

            currentPos = toPos
            currentTex = textViewList[currentPos]
            setSelectTextView(currentTex)

            lineToX = calculationLineX(currentPos)
            startLineAnimator()
            horizontalScroll(currentTex!!)
        } else {
            currentPos = toPos
        }
    }

    //对于超出屏幕的item使其居中
    private fun horizontalScroll(texV: TextView) {
        if ((texV.right - this.scrollX - screenWidth) > 0) {
            //右侧超出屏幕，需要向右滚动（需考虑滚动值）

            //view左点距离屏幕边位置
            val leftStartX = texV.left - this.scrollX - screenWidth
            val off = leftStartX + (screenWidth - itemWidth) / 2 + itemWidth
            this.smoothScrollBy(off.toInt(), 0)
        } else if ((texV.left - this.scrollX) < 0) {
            //view右点距离屏幕边位置
            val rightStartX = -(texV.right - this.scrollX)
            val off = rightStartX + (screenWidth - itemWidth) / 2 + itemWidth
            this.smoothScrollBy(-off.toInt(), 0)
        }

    }

    private val updateLisenter = ValueAnimator.AnimatorUpdateListener {
        lineX = it.animatedValue as Float
        invalidate()
    }

    private val animatorLisenter = object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator?) {
            lineX = lineToX
            invalidate()
        }
    }

    private fun startLineAnimator() {
        lineAnimator?.removeAllListeners()
        lineAnimator?.removeAllUpdateListeners()
        lineAnimator?.cancel()

        lineAnimator = ValueAnimator.ofFloat(lineX, lineToX)
        lineAnimator?.duration = 250
        lineAnimator?.interpolator = OvershootInterpolator()
        lineAnimator?.addUpdateListener(updateLisenter)

        lineAnimator?.addListener(animatorLisenter)

        lineAnimator?.start()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (currentPos != -1) {
            val lineY = measuredHeight - (lineHeight / 2)
            canvas.drawLine(lineX, lineY, lineX + lineWidth, lineY, mPaint)
        }
    }

    fun setListener(listener: (Int) -> Unit) {
        this.listener = listener
    }
}