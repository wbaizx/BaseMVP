package com.base.common.util.imageload

import android.widget.ImageView
import java.io.File

interface LoadEngine {
    fun load(url: String, img: ImageView, type: Int)

    fun load(file: File, img: ImageView, type: Int)

    fun load(id: Int, img: ImageView, type: Int)
}