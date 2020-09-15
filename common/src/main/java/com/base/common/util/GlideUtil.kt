package com.base.common.util

import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.base.common.R
import com.base.common.config.GlideRequest
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.transition.Transition
import jp.wasabeef.glide.transformations.BlurTransformation
import jp.wasabeef.glide.transformations.RoundedCornersTransformation

private const val TAG = "GlideUtil"

//const val imgUrl = "http://img1.imgtn.bdimg.com/it/u=1004510913,4114177496&fm=26&gp=0.jpg"
const val imgUrl =
    "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1583902813407&di=e5c444a2a80d5561d59d2866e3d2b8b8&imgtype=0&src=http%3A%2F%2Fa3.att.hudong.com%2F68%2F61%2F300000839764127060614318218_950.jpg"

fun GlideRequest<Drawable>.normalInto(img: ImageView) {
    thumbnail(0.2f)
        .placeholder(R.mipmap.placeholder_icon)
        .error(R.mipmap.test_icon)
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(img)
}

fun GlideRequest<Drawable>.blurInto(img: ImageView) {
    //高斯模糊
    //如果需要对图片部分，比如底部做模糊，可以在图片上再盖一张图，上面的imageView包裹一层父布局，限制高度
    //然后上层imageView与下层imageView同高宽，同时与包裹的父布局底部对齐，达到只模糊底部的骚操作
    apply(RequestOptions.bitmapTransform(BlurTransformation(25, 3)))
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(img)
}

fun GlideRequest<Drawable>.circleInto(img: ImageView) {
    //圆图实现，尽量用下面这种，还有 CircleImageView 和 cardView 也能实现
    apply(RequestOptions.circleCropTransform())
        //圆角
//        .apply(RequestOptions.bitmapTransform(RoundedCornersTransformation(50, 0)))
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(img)
}

fun GlideRequest<Drawable>.roundInto(img: ImageView) {
    //圆角
    apply(RequestOptions.bitmapTransform(RoundedCornersTransformation(50, 0)))
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(img)
}

/**
 * 使用 CircleImageView + into(view) 可能导致第一次只加载显示 placeholder
 * 使用这个类来加载到view上可以解决
 * 或者直接使用 ImageView + glide圆图转换 实现
 */
private class BaseTarget(img: ImageView) : CustomViewTarget<ImageView, Drawable>(img) {
    override fun onLoadFailed(errorDrawable: Drawable?) {
        log(TAG, "onLoadFailed $errorDrawable")
        getView().setImageDrawable(errorDrawable)
    }

    override fun onResourceCleared(placeholder: Drawable?) {
        //这个方法在drawable被回收时调用，如果在除了imageView以外的地方引用了imageView中的bitmap，在这里清除引用以避免崩溃
        log(TAG, "onResourceCleared $placeholder")
    }

    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
        log(TAG, "onResourceReady $resource")
        getView().setImageDrawable(resource)
    }
}