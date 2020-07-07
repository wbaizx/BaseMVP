package com.basemvp.main.coordinator.coordinator1

import android.graphics.*
import com.base.common.BaseAPP
import com.base.common.util.AndroidUtil
import com.base.common.view.SimpleItemDecoration
import com.basemvp.R

class Coordinator1Decoration(adapter: Coordinator1Adapter) : SimpleItemDecoration<String, Coordinator1Adapter>(adapter) {

    override var decorationHeight: Int = AndroidUtil.dp2px(90f).toInt()

    private val bgColor: Int = Color.parseColor("#898989")
    private val bgColor2: Int = Color.parseColor("#FF9500")

    /**
     * 画笔
     */
    private val mPaint: Paint

    private val textTitleColor = Color.BLACK
    private val textTitleSize = AndroidUtil.sp2px(18f)
    private val textTitleLineColor = Color.parseColor("#BBEDFF")

    private val textDayColor = Color.parseColor("#9B9B9B")
    private val textDaySize = AndroidUtil.sp2px(16f)

    private val textTimeColor = Color.parseColor("#B4B4B4")
    private val textTimeSize = AndroidUtil.sp2px(12f)

    private val textLabelColor = Color.parseColor("#5CC1FF")
    private val icon =
        BitmapFactory.decodeResource(BaseAPP.baseAppContext.resources, R.mipmap.bill_details_main_item_line_icon)

    //粗体文字
    private val BOLD = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

    //普通文字
    private val DEFAULT = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

    //图片位置矩形
    private val rectF = RectF(0f, 0f, 0f, 0f)

    //图标左偏移
    private val iconLeftOff = AndroidUtil.dp2px(34f)

    //图标右偏移
    private val iconRightOff = AndroidUtil.dp2px(16f)

    //画笔宽度
    private val strokeWidth1 = AndroidUtil.dp2px(3f)
    private val strokeWidth2 = AndroidUtil.dp2px(6f)


    init {
        mPaint = Paint()
        mPaint.style = Paint.Style.FILL
        mPaint.isAntiAlias = true
        mPaint.strokeCap = Paint.Cap.ROUND
        mPaint.textAlign = Paint.Align.LEFT
    }

    override fun offsets(outRect: Rect, dataPosition: Int, bean: String) {
        outRect.set(0, decorationHeight, 0, 0)
    }

    override fun drawDecoration(c: Canvas, left: Float, right: Float, top: Float, bottom: Float, dataPosition: Int, bean: String) {
        drawTitleArea(c, left, right, top, bottom, dataPosition, bean, bgColor)
    }

    override fun needEffects(dataPosition: Int, nextDataPosition: Int, bean: String, nextBean: String): Boolean {
        return true
    }

    override fun drawOverTop(c: Canvas, left: Float, right: Float, top: Float, bottom: Float, dataPosition: Int, bean: String) {
        drawTitleArea(c, left, right, top, bottom, dataPosition, bean, bgColor2)
    }

    override fun drawOverDecoration(c: Canvas, left: Float, right: Float, top: Float, bottom: Float, dataPosition: Int, bean: String) {
    }

    /**
     * 绘制方法
     */
    private fun drawTitleArea(c: Canvas, left: Float, right: Float, top: Float, bottom: Float, position: Int, bean: String, bgColor: Int) {
        //画title背景矩形
        mPaint.color = bgColor
        c.drawRect(left, top, right, bottom, mPaint)

        //画内容

        //大title文字高度一半

        mPaint.typeface = BOLD
        mPaint.textSize = textTitleSize
        val height = (mPaint.fontMetrics.descent - mPaint.fontMetrics.ascent) / 2

        //画竖线
        mPaint.strokeWidth = strokeWidth1
        mPaint.color = textLabelColor
        var startX = AndroidUtil.dp2px(17f)
        var bottomY = top + decorationHeight / 2 + height
        c.drawLine(startX, bottomY - strokeWidth1 / 2, startX, bottomY - AndroidUtil.dp2px(13f) + strokeWidth1 / 2, mPaint)

        //画天
        mPaint.color = textDayColor
        mPaint.textSize = textDaySize
        startX = AndroidUtil.dp2px(24f)
        c.drawText("DAY$position", startX, bottomY - AndroidUtil.dp2px(7f) - getTextOffset(mPaint), mPaint)

        //画大title--------------------------
        startX += AndroidUtil.dp2px(5f) + mPaint.measureText("DAY$position")
        //画大title下划线
        mPaint.textSize = textTitleSize

        val titleWidth = mPaint.measureText(bean)
        mPaint.strokeWidth = strokeWidth2
        mPaint.color = textTitleLineColor
        c.drawLine(
            startX + strokeWidth2 / 2,
            bottomY - strokeWidth2 / 2,
            startX + titleWidth - strokeWidth2 / 2,
            bottomY - strokeWidth2 / 2,
            mPaint
        )

        //画大title字
        mPaint.color = textTitleColor
        c.drawText(bean, startX, top + decorationHeight / 2 - getTextOffset(mPaint), mPaint)


        //画日期
        mPaint.typeface = DEFAULT
        startX = AndroidUtil.dp2px(24f)
        bottomY += AndroidUtil.dp2px(9f)
        mPaint.color = textTimeColor
        mPaint.textSize = textTimeSize

        c.drawText("--$position", startX, bottomY + AndroidUtil.dp2px(6f) - getTextOffset(mPaint), mPaint)

        //画图标
        rectF.set(
            right - iconLeftOff,
            top + decorationHeight / 2 + height - icon.height,
            right - iconRightOff,
            top + decorationHeight / 2 + height
        )
        c.drawBitmap(icon, null, rectF, mPaint)
    }
}
