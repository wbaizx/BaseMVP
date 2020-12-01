package com.base.common.util.imageload

import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import java.io.File

interface LoadEngine {
    fun load(url: String, img: ImageView)

    fun load(file: File, img: ImageView)

    fun load(@RawRes @DrawableRes id: Int, img: ImageView)

    fun loadBlur(url: String, img: ImageView)

    fun loadBlur(file: File, img: ImageView)

    fun loadBlur(@RawRes @DrawableRes id: Int, img: ImageView)

    fun loadCircle(url: String, img: ImageView)

    fun loadCircle(file: File, img: ImageView)

    fun loadCircle(@RawRes @DrawableRes id: Int, img: ImageView)

    fun loadRound(url: String, img: ImageView)

    fun loadRound(file: File, img: ImageView)

    fun loadRound(@RawRes @DrawableRes id: Int, img: ImageView)
}