package com.basemvp.main.special_rc.circle_rc

import android.graphics.Canvas
import android.graphics.Rect
import com.base.common.util.AndroidUtil
import com.base.common.view.SimpleItemDecoration

class CircleDecoration(adapter: CircleAdapter) : SimpleItemDecoration<String, CircleAdapter>(adapter) {

    override var decorationHeight: Int = 0

    override fun offsets(outRect: Rect, dataPosition: Int, bean: String) {
        if (dataPosition != 0) {
            outRect.left = -AndroidUtil.dp2px(22f).toInt()
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
    }
}