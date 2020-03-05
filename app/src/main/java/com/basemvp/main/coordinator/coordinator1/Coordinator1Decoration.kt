package com.basemvp.main.coordinator.coordinator1

import android.graphics.*
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.base.common.BaseAPP
import com.basemvp.R
import com.base.common.util.AndroidUtil

class Coordinator1Decoration(private val adapter: Coordinator1Adapter) : RecyclerView.ItemDecoration() {
    /**
     * 标题栏和悬浮栏高度
     */
    private val titleHeight: Int = AndroidUtil.dp2px(90f).toInt()

    private val bgColor: Int = Color.parseColor("#898989")
    private val bgColor2: Int = Color.parseColor("#FF9500")

    /**
     * 画笔
     */
    private val mPaint: Paint

    init {
        mPaint = Paint()
        mPaint.style = Paint.Style.FILL
        mPaint.isAntiAlias = true
        mPaint.strokeCap = Paint.Cap.ROUND
        mPaint.textAlign = Paint.Align.LEFT
    }

    private fun getTextOffset(): Float {
        val fontMetrics = mPaint.fontMetrics
        return fontMetrics.top / 2 + fontMetrics.bottom / 2
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        val position = (view.layoutParams as RecyclerView.LayoutParams).viewLayoutPosition
        //判断是否是列表数据，需要考虑头尾部
        if (position > (-1 + adapter.getHeaderLayoutCount()) && position < adapter.data.size + adapter.getHeaderLayoutCount()) {
            outRect.set(0, titleHeight, 0, 0)
        }
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        val left = parent.paddingLeft.toFloat()
        val right = parent.width - parent.paddingRight.toFloat()
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val position = params.viewLayoutPosition
            val top = child.top.toFloat() - params.topMargin - titleHeight
            val bottom = top + titleHeight
            //判断是否是列表数据，需要考虑头尾部
            if (position > (-1 + adapter.getHeaderLayoutCount()) && position < adapter.data.size + adapter.getHeaderLayoutCount()) {
                drawTitleArea(c, left, right, top, bottom, position - adapter.getHeaderLayoutCount(), bgColor)
            }
        }
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
        val position = (parent.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
        //判断是否是列表数据，需要考虑头尾部
        if (position > (-1 + adapter.getHeaderLayoutCount()) && position < adapter.data.size + adapter.getHeaderLayoutCount()) {
            //child = parent.getChildAt(i) 有时候child为空
            val child = parent.findViewHolderForLayoutPosition(position)?.itemView
            if (child != null) {
                //定义一个flag，Canvas是否位移过的标志
                var flag = false
                //如果后面还有数据
                if (adapter.data.size > position) {
                    //如果前第一个可见的Item的tag，不等于其后一个item的tag，说明悬浮的View要切换了,如下判断
                    //if (datas[position].title != datas[position + 1].title) {

                    //当getTop开始变负，它的绝对值，是第一个可见的Item移出屏幕的距离，
                    if (child.height + child.top < titleHeight) {
                        //当第一个可见的item在屏幕中还剩的高度小于title区域的高度时，开始做悬浮Title的“交换动画”
                        //每次绘制前 保存当前Canvas状态，
                        c.save()
                        flag = true

                        //将canvas上移 （y为负数） ,所以后面canvas 画出来的Rect和Text都上移了，有种切换的“动画”感觉
                        c.translate(0f, (child.height + child.top - titleHeight).toFloat())
                    }
                }
                val left = parent.paddingLeft.toFloat()
                val right = parent.width - parent.paddingRight.toFloat()
                val top = parent.paddingTop.toFloat()
                val bottom = top + titleHeight
                drawTitleArea(c, left, right, top, bottom, position - adapter.getHeaderLayoutCount(), bgColor2)

                if (flag) {
                    c.restore()//恢复画布到之前保存的状态
                }
            }
        }
    }

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

    /**
     * 绘制方法
     */
    private fun drawTitleArea(c: Canvas, left: Float, right: Float, top: Float, bottom: Float, position: Int, bgColor: Int) {
        //画title背景矩形
        mPaint.color = bgColor
        c.drawRect(left, top, right, bottom, mPaint)

        val bean = adapter.data[position]

        //画内容

        //大title文字高度一半

        mPaint.typeface = BOLD
        mPaint.textSize = textTitleSize
        val height = (mPaint.fontMetrics.descent - mPaint.fontMetrics.ascent) / 2

        //画竖线
        mPaint.strokeWidth = strokeWidth1
        mPaint.color = textLabelColor
        var startX = AndroidUtil.dp2px(17f)
        var bottomY = top + titleHeight / 2 + height
        c.drawLine(startX, bottomY - strokeWidth1 / 2, startX, bottomY - AndroidUtil.dp2px(13f) + strokeWidth1 / 2, mPaint)

        //画天
        mPaint.color = textDayColor
        mPaint.textSize = textDaySize
        startX = AndroidUtil.dp2px(24f)
        c.drawText("DAY$position", startX, bottomY - AndroidUtil.dp2px(7f) - getTextOffset(), mPaint)

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
        c.drawText(bean, startX, top + titleHeight / 2 - getTextOffset(), mPaint)


        //画日期
        mPaint.typeface = DEFAULT
        startX = AndroidUtil.dp2px(24f)
        bottomY += AndroidUtil.dp2px(9f)
        mPaint.color = textTimeColor
        mPaint.textSize = textTimeSize

        c.drawText("--$position", startX, bottomY + AndroidUtil.dp2px(6f) - getTextOffset(), mPaint)

        //画图标
        rectF.set(
            right - iconLeftOff,
            top + titleHeight / 2 + height - icon.height,
            right - iconRightOff,
            top + titleHeight / 2 + height
        )
        c.drawBitmap(icon, null, rectF, mPaint)
    }
}
