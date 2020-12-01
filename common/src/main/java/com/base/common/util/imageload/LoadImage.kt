package com.base.common.util.imageload

import android.widget.ImageView
import java.io.File

object LoadImage : LoadEngine {
    //const val imgUrl = "http://img1.imgtn.bdimg.com/it/u=1004510913,4114177496&fm=26&gp=0.jpg"
    const val imgUrl =
        "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1583902813407&di=e5c444a2a80d5561d59d2866e3d2b8b8&imgtype=0&src=http%3A%2F%2Fa3.att.hudong.com%2F68%2F61%2F300000839764127060614318218_950.jpg"

    private val glideEngine: LoadEngine by lazy { GlideEngine() }
    private val coilEngine: LoadEngine by lazy { CoilEngine() }

    private var defaultEngine = glideEngine

    override fun load(url: String, img: ImageView) {
        defaultEngine.load(url, img)
    }

    override fun load(file: File, img: ImageView) {
        defaultEngine.load(file, img)
    }

    override fun load(id: Int, img: ImageView) {
        defaultEngine.load(id, img)
    }

    override fun loadBlur(url: String, img: ImageView) {
        defaultEngine.loadBlur(url, img)
    }

    override fun loadBlur(file: File, img: ImageView) {
        defaultEngine.loadBlur(file, img)
    }

    override fun loadBlur(id: Int, img: ImageView) {
        defaultEngine.loadBlur(id, img)
    }

    override fun loadCircle(url: String, img: ImageView) {
        defaultEngine.loadCircle(url, img)
    }

    override fun loadCircle(file: File, img: ImageView) {
        defaultEngine.loadCircle(file, img)
    }

    override fun loadCircle(id: Int, img: ImageView) {
        defaultEngine.loadCircle(id, img)
    }

    override fun loadRound(url: String, img: ImageView) {
        defaultEngine.loadRound(url, img)
    }

    override fun loadRound(file: File, img: ImageView) {
        defaultEngine.loadRound(file, img)
    }

    override fun loadRound(id: Int, img: ImageView) {
        defaultEngine.loadRound(id, img)
    }
}
