package com.basemvp.main.special_rc.scrollto_rc

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import com.base.common.util.AndroidUtil
import com.base.common.view.SimpleItemDecoration

class ScrollToRCDecoration(adapter: ScrollToRCAdapter) : SimpleItemDecoration<String>(adapter) {

    override var decorationHeight: Int = AndroidUtil.dp2px(90f).toInt()

    private val bgColor: Int = Color.parseColor("#898989")
    private val bgColor2: Int = Color.parseColor("#FF9500")

    private val textColor: Int = Color.BLACK

    /**
     * 画笔
     */
    private val mPaint: Paint = Paint()

    init {
        mPaint.style = Paint.Style.FILL
        mPaint.isAntiAlias = true
        mPaint.strokeCap = Paint.Cap.ROUND
        mPaint.textAlign = Paint.Align.LEFT
        mPaint.textSize = AndroidUtil.sp2px(18f)
    }

    override fun offsets(outRect: Rect, dataPosition: Int, bean: String) {
        outRect.set(0, decorationHeight, 0, 0)
    }

    override fun drawDecoration(c: Canvas, left: Float, right: Float, top: Float, bottom: Float, dataPosition: Int, bean: String) {
        drawTitleArea(c, left, right, top, bottom, dataPosition, bgColor)
    }

    override fun needEffects(dataPosition: Int, nextDataPosition: Int, bean: String, nextBean: String): Boolean {
        return true
    }

    override fun drawOverTop(c: Canvas, left: Float, right: Float, top: Float, bottom: Float, dataPosition: Int, bean: String) {
        drawTitleArea(c, left, right, top, bottom, dataPosition, bgColor2)
    }

    override fun drawOverDecoration(c: Canvas, left: Float, right: Float, top: Float, bottom: Float, dataPosition: Int, bean: String) {
    }

    /**
     * 绘制方法
     */
    private fun drawTitleArea(c: Canvas, left: Float, right: Float, top: Float, bottom: Float, position: Int, bgColor: Int) {
        //画title背景矩形
        mPaint.color = bgColor
        c.drawRect(left, top, right, bottom, mPaint)

        mPaint.color = textColor
        //画文本
        c.drawText("no $position", left + 100, top + decorationHeight / 2 - getTextOffset(mPaint), mPaint)
    }
}
