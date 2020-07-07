package com.basemvp.main.special_rc.connection_rc

import android.graphics.*
import com.base.common.BaseAPP
import com.base.common.util.AndroidUtil
import com.base.common.view.SimpleItemDecoration
import com.basemvp.R

class ConnectionDecoration(adapter: ConnectionAdapter) : SimpleItemDecoration<String, ConnectionAdapter>(adapter) {
    override var decorationHeight: Int = AndroidUtil.dp2px(8f).toInt()

    private val icon = BitmapFactory.decodeResource(BaseAPP.baseAppContext.resources, R.mipmap.transit_icon)

    //图片位置矩形
    private val rectF = RectF(0f, 0f, 0f, 0f)

    /**
     * 画笔
     */
    private val mPaint: Paint = Paint()

    init {
        mPaint.isAntiAlias = true
    }

    override fun offsets(outRect: Rect, dataPosition: Int, bean: String) {
        if (dataPosition != 0) {
            outRect.set(0, decorationHeight, 0, 0)
        }
    }

    override fun drawDecoration(c: Canvas, left: Float, right: Float, top: Float, bottom: Float, dataPosition: Int, bean: String) {
    }

    override fun needEffects(dataPosition: Int, nextDataPosition: Int, bean: String, nextBean: String): Boolean {
        return false
    }

    override fun drawOverTop(c: Canvas, left: Float, right: Float, top: Float, bottom: Float, dataPosition: Int, bean: String) {
    }

    override fun drawOverDecoration(c: Canvas, left: Float, right: Float, top: Float, bottom: Float, dataPosition: Int, bean: String) {
        if (dataPosition != 0) {
            val dis = (right - left) / 3

            drawBitmap(left + dis, top, c)
            drawBitmap(left + dis * 2, top, c)
        }
    }

    private fun drawBitmap(startX: Float, top: Float, c: Canvas) {
        val X = startX - icon.width / 2
        val Y = top + decorationHeight / 2 - icon.height / 2
        rectF.set(
            X,
            Y,
            X + icon.width,
            Y + icon.height
        )
        c.drawBitmap(icon, null, rectF, mPaint)
    }
}