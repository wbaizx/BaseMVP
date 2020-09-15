package com.base.common.view

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

/**
 * 简单分割线可直接使用 DividerItemDecoration
 *
 * SimpleItemDecoration 是 RecyclerView 稍复杂分割线，依赖BaseRecyclerViewAdapterHelper，可自行绘制分割线，连接器，顶部悬停（可带动效）等
 *
 * 根据 view 找 position
 * RecyclerView.LayoutParams.viewLayoutPosition
 *
 * 根据 position 找 view
 * LinearLayoutManager.findViewByPosition()
 * RecyclerView.findViewHolderForLayoutPosition()
 */
abstract class SimpleItemDecoration<B>(protected val adapter: BaseQuickAdapter<B, BaseViewHolder>) : RecyclerView.ItemDecoration() {

    /**
     * 标题栏和悬浮栏高度
     */
    abstract var decorationHeight: Int

    /**
     * 文字对应于y坐标点居中的偏移量，用y中点减去此偏移量
     */
    fun getTextOffset(paint: Paint): Float {
        val fontMetrics = paint.fontMetrics
        return fontMetrics.top / 2 + fontMetrics.bottom / 2
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        val childPosition = (view.layoutParams as RecyclerView.LayoutParams).viewLayoutPosition
        //判断是否是列表数据，需要考虑头尾部
        if (childPosition > (-1 + adapter.headerLayoutCount) && childPosition < adapter.data.size + adapter.headerLayoutCount) {
            val dataPosition = childPosition - adapter.headerLayoutCount
            offsets(outRect, dataPosition, adapter.data[dataPosition])
        }
    }

    /**
     * 根据需求设置偏移量（配置分割线宽度）
     * 不需要的项不做操作
     */
    abstract fun offsets(outRect: Rect, dataPosition: Int, bean: B)

    /**
     * 这个方法在绘制item之前调用，所以这里绘制的东西会被item覆盖，但这里可以覆盖recycleView背景色
     */
    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        val left = parent.paddingLeft.toFloat()
        val right = parent.width - parent.paddingRight.toFloat()
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val childPosition = params.viewLayoutPosition
            val top = child.top.toFloat() - params.topMargin - decorationHeight
            val bottom = top + decorationHeight
            //判断是否是列表数据，需要考虑头尾部
            if (childPosition > (-1 + adapter.headerLayoutCount) && childPosition < adapter.data.size + adapter.headerLayoutCount) {
                val dataPosition = childPosition - adapter.headerLayoutCount
                drawDecoration(c, left, right, top, bottom, dataPosition, adapter.data[dataPosition])
            }
        }
    }

    /**
     * 根据需求绘制分割线
     * 一次绘制会多次调用
     * 不需要的项不做操作
     */
    abstract fun drawDecoration(c: Canvas, left: Float, right: Float, top: Float, bottom: Float, dataPosition: Int, bean: B)

    /**
     * 这个方法绘制顺序在最后，覆盖在最上层
     * 可以在这里做顶部悬浮、item之间有连接装饰等功能
     */
    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)

        val left = parent.paddingLeft.toFloat()
        val right = parent.width - parent.paddingRight.toFloat()

        //-----绘制顶部悬停
        val topPosition = (parent.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
        //判断是否是列表数据，需要考虑头尾部
        if (topPosition > (-1 + adapter.headerLayoutCount) && topPosition < adapter.data.size + adapter.headerLayoutCount) {
            //child = parent.getChildAt(i) 有时候child为空
            val child = parent.findViewHolderForLayoutPosition(topPosition)?.itemView
            if (child != null) {
                val dataPosition = topPosition - adapter.headerLayoutCount

                //定义一个flag，Canvas是否位移过的标志
                var flag = false
                //如果后面还有数据
                if (adapter.data.size > dataPosition + 1) {
                    if (needEffects(dataPosition, dataPosition + 1, adapter.data[dataPosition], adapter.data[dataPosition + 1])) {
                        //当getTop开始变负，它的绝对值，是第一个可见的Item移出屏幕的距离，
                        if (child.height + child.top < decorationHeight) {
                            //当第一个可见的item在屏幕中还剩的高度小于title区域的高度时，开始做悬浮Title的“交换动画”
                            //每次绘制前 保存当前Canvas状态，
                            c.save()
                            flag = true

                            //将canvas上移 （y为负数） ,所以后面canvas 画出来的Rect和Text都上移了，有种切换的“动画”感觉
                            c.translate(0f, (child.height + child.top - decorationHeight).toFloat())
                        }
                    }
                }
                val top = parent.paddingTop.toFloat()
                val bottom = top + decorationHeight
                drawOverTop(c, left, right, top, bottom, dataPosition, adapter.data[dataPosition])

                if (flag) {
                    c.restore()//恢复画布到之前保存的状态
                }
            }
        }

        //-----绘制连接器
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val childPosition = params.viewLayoutPosition
            val top = child.top.toFloat() - params.topMargin - decorationHeight
            val bottom = top + decorationHeight
            //判断是否是列表数据，需要考虑头尾部
            if (childPosition > (-1 + adapter.headerLayoutCount) && childPosition < adapter.data.size + adapter.headerLayoutCount) {
                val dataPosition = childPosition - adapter.headerLayoutCount
                drawOverDecoration(c, left, right, top, bottom, dataPosition, adapter.data[dataPosition])
            }
        }
    }

    /**
     * 是否需要顶出效果的切换动画
     * 一次绘制只调用一次
     */
    abstract fun needEffects(dataPosition: Int, nextDataPosition: Int, bean: B, nextBean: B): Boolean

    /**
     * 根据需求绘制顶部悬停 ,可通过 needEffects 设置动画开关
     * 一次绘制只调用一次
     */
    abstract fun drawOverTop(c: Canvas, left: Float, right: Float, top: Float, bottom: Float, dataPosition: Int, bean: B)

    /**
     * 根据需求绘制最上层分割线，可在这里面超出坐标限制绘制，达到盖在上面的效果
     * 一次绘制会多次调用
     * 不需要的项不做操作
     */
    abstract fun drawOverDecoration(c: Canvas, left: Float, right: Float, top: Float, bottom: Float, dataPosition: Int, bean: B)
}
