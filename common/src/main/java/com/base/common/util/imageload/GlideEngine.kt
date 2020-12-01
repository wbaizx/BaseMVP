package com.base.common.util.imageload

import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.base.common.R
import com.base.common.config.GlideApp
import com.base.common.util.log
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.transition.Transition
import jp.wasabeef.glide.transformations.BlurTransformation
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import java.io.File

class GlideEngine : LoadEngine {

    /**
     * 使用 CircleImageView + into(view) 可能导致第一次只加载显示 placeholder
     * 使用这个类来加载到view上可以解决
     * 或者直接使用圆图裁剪API loadCircle 实现
     */
    private class BaseTarget(img: ImageView) : CustomViewTarget<ImageView, Drawable>(img) {
        override fun onLoadFailed(errorDrawable: Drawable?) {
            log("GlideLoad", "onLoadFailed $errorDrawable")
            getView().setImageDrawable(errorDrawable)
        }

        override fun onResourceCleared(placeholder: Drawable?) {
            //这个方法在drawable被回收时调用，如果在除了imageView以外的地方引用了imageView中的bitmap，在这里清除引用以避免崩溃
            log("GlideLoad", "onResourceCleared $placeholder")
        }

        override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
            log("GlideLoad", "onResourceReady $resource")
            getView().setImageDrawable(resource)
        }
    }

    override fun load(url: String, img: ImageView) {
        GlideApp.with(img.context).load(url)
            .thumbnail(0.2f)
            .transition(DrawableTransitionOptions.withCrossFade())
            .placeholder(R.mipmap.placeholder_icon)
            .error(R.mipmap.test_icon)
            .into(img)
    }

    override fun load(file: File, img: ImageView) {
        GlideApp.with(img.context).load(file)
            .thumbnail(0.2f)
            .transition(DrawableTransitionOptions.withCrossFade())
            .placeholder(R.mipmap.placeholder_icon)
            .error(R.mipmap.test_icon)
            .into(img)
    }

    override fun load(id: Int, img: ImageView) {
        GlideApp.with(img.context).load(id)
            .thumbnail(0.2f)
            .transition(DrawableTransitionOptions.withCrossFade())
            .placeholder(R.mipmap.placeholder_icon)
            .error(R.mipmap.test_icon)
            .into(img)
    }

    //高斯模糊
    //如果需要对图片部分，比如底部做模糊，可以在图片上再盖一张图，上面的imageView包裹一层父布局，限制高度
    //然后上层imageView与下层imageView同高宽，同时与包裹的父布局底部对齐，达到只模糊底部的骚操作
    override fun loadBlur(url: String, img: ImageView) {
        GlideApp.with(img.context).load(url)
            .transition(DrawableTransitionOptions.withCrossFade())
            .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 3)))
            .into(img)
    }

    override fun loadBlur(file: File, img: ImageView) {
        GlideApp.with(img.context).load(file)
            .transition(DrawableTransitionOptions.withCrossFade())
            .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 3)))
            .into(img)
    }

    override fun loadBlur(id: Int, img: ImageView) {
        GlideApp.with(img.context).load(id)
            .transition(DrawableTransitionOptions.withCrossFade())
            .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 3)))
            .into(img)
    }

    //圆图实现，尽量用这种，另外 CircleImageView 和 cardView 也能实现
    override fun loadCircle(url: String, img: ImageView) {
        GlideApp.with(img.context).load(url)
            .transition(DrawableTransitionOptions.withCrossFade())
            .apply(RequestOptions.circleCropTransform())
            .into(img)
    }

    override fun loadCircle(file: File, img: ImageView) {
        GlideApp.with(img.context).load(file)
            .transition(DrawableTransitionOptions.withCrossFade())
            .apply(RequestOptions.circleCropTransform())
            .into(img)
    }

    override fun loadCircle(id: Int, img: ImageView) {
        GlideApp.with(img.context).load(id)
            .transition(DrawableTransitionOptions.withCrossFade())
            .apply(RequestOptions.circleCropTransform())
            .into(img)
    }

    //圆角
    override fun loadRound(url: String, img: ImageView) {
        GlideApp.with(img.context).load(url)
            .transition(DrawableTransitionOptions.withCrossFade())
            .apply(RequestOptions.bitmapTransform(RoundedCornersTransformation(50, 0)))
            .into(img)
    }

    override fun loadRound(file: File, img: ImageView) {
        GlideApp.with(img.context).load(file)
            .transition(DrawableTransitionOptions.withCrossFade())
            .apply(RequestOptions.bitmapTransform(RoundedCornersTransformation(50, 0)))
            .into(img)
    }

    override fun loadRound(id: Int, img: ImageView) {
        GlideApp.with(img.context).load(id)
            .transition(DrawableTransitionOptions.withCrossFade())
            .apply(RequestOptions.bitmapTransform(RoundedCornersTransformation(50, 0)))
            .into(img)
    }
}