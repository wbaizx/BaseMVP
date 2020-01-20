package com.basemvp.main.special_rc.qq_album

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout

class AlbumPhotoSquareLayout(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }
}