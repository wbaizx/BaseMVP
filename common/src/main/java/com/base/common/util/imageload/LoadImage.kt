package com.base.common.util.imageload

import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import java.io.File

object LoadImage {
    const val NORMAL = 1
    const val BLUR = 2
    const val CIRCLE = 3
    const val ROUND = 4

    //const val imgUrl = "http://img1.imgtn.bdimg.com/it/u=1004510913,4114177496&fm=26&gp=0.jpg"
    const val imgUrl =
        "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1583902813407&di=e5c444a2a80d5561d59d2866e3d2b8b8&imgtype=0&src=http%3A%2F%2Fa3.att.hudong.com%2F68%2F61%2F300000839764127060614318218_950.jpg"

    private val glideEngine: LoadEngine by lazy { GlideEngine() }
    private val coilEngine: LoadEngine by lazy { CoilEngine() }

    private var defaultEngine = coilEngine

    //两个参数重载是为了方便java调用
    fun load(@RawRes @DrawableRes id: Int, img: ImageView) {
        load(id, img, NORMAL)
    }

    //两个参数重载是为了方便java调用
    fun load(file: File, img: ImageView) {
        load(file, img, NORMAL)
    }

    fun load(@RawRes @DrawableRes id: Int, img: ImageView, type: Int = NORMAL) {
        defaultEngine.load(id, img, type)
    }

    fun load(url: String, img: ImageView, type: Int = NORMAL) {
        defaultEngine.load(url, img, type)
    }

    fun load(file: File, img: ImageView, type: Int = NORMAL) {
        defaultEngine.load(file, img, type)
    }
}
