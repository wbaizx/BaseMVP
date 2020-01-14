package com.basemvp.main.scrollto_rc

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.basemvp.util.AndroidUtil

class ScrollToRCDecoration(private val adapter: ScrollToRCAdapter) : RecyclerView.ItemDecoration() {
    /**
     * 标题栏和悬浮栏高度
     */
    val titleHeight: Int = AndroidUtil.dp2px(90f).toInt()

    private val bgColor: Int = Color.parseColor("#898989")
    private val bgColor2: Int = Color.parseColor("#FF9500")

    private val textColor: Int = Color.BLACK

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

        mPaint.textSize = AndroidUtil.sp2px(18f)
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

    /**
     * 绘制方法
     */
    private fun drawTitleArea(c: Canvas, left: Float, right: Float, top: Float, bottom: Float, position: Int, bgColor: Int) {
        //画title背景矩形
        mPaint.color = bgColor
        c.drawRect(left, top, right, bottom, mPaint)

        mPaint.color = textColor
        //画文本
        c.drawText("no $position", left + 100, top + titleHeight / 2 - getTextOffset(), mPaint)
    }
}
