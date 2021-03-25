package com.basemvp.main.item_animation.item_animation3

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.base.common.base.BaseActivity
import com.basemvp.R
import kotlinx.android.synthetic.main.activity_item_animation3.*

class ItemAnimation3Activity : BaseActivity() {
    private var isLine = true
    private var duration = 500L

    override fun getContentView() = R.layout.activity_item_animation3

    override fun initView() {
        recyclerViewV.post { recyclerViewV.translationX = -layout.width.toFloat() }

        recyclerViewH.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewH.adapter = ItemAnimation3HAdapter()

        recyclerViewV.layoutManager = GridLayoutManager(this, 2)
        recyclerViewV.adapter = ItemAnimation3VAdapter()

        change.setOnClickListener {
            if (isLine) {
                for (index in 0 until recyclerViewV.childCount) {
                    val view = recyclerViewV.getChildAt(index)
                    view.pivotX = view.width.toFloat()
                    view.pivotY = view.height * 5f
                    view.rotation = (340..350).random().toFloat()
                    view.animate().setDuration(duration + (400..600).random()).rotation(360f).start()
                }

                recyclerViewV.animate()
                    .setDuration(duration)
                    .translationX(0f)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationStart(animation: Animator?) {
                            super.onAnimationStart(animation)
                        }
                    })
                    .start()

                recyclerViewH.animate()
                    .setDuration(duration)
                    .translationX(layout.width.toFloat())
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            super.onAnimationEnd(animation)
                        }
                    })
                    .start()
            } else {
                recyclerViewV.animate()
                    .setDuration(duration)
                    .translationX(-layout.width.toFloat())
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            super.onAnimationEnd(animation)
                        }
                    })
                    .start()

                recyclerViewH.animate()
                    .setDuration(duration)
                    .translationX(0f)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationStart(animation: Animator?) {
                            super.onAnimationStart(animation)
                        }
                    })
                    .start()
            }
            isLine = !isLine
        }
    }

    override fun initData() {
    }

}
