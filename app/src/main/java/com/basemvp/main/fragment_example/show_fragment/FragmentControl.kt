package com.basemvp.main.fragment_example.show_fragment

import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import com.basemvp.main.fragment_example.fm.TestFragment
import com.basemvp.main.fragment_example.fm.TestMVPFragment
import com.base.common.util.LogUtil

/**
 * 通过配合 setMaxLifecycle 控制生命周期
 */
class FragmentControl(private val supportFragmentManager: FragmentManager, private val fragmentLayout: Int) {
    private val TAG = "FragmentControl"

    private var fragmentList = arrayListOf(
        TestFragment("1"),
        TestFragment("2"),
        TestFragment("3"),
        TestFragment("4"),
        TestMVPFragment("MVP 5"),
        TestMVPFragment("MVP 6"),
        TestMVPFragment("MVP 7")
    )

    private var currentPosition = -1

    fun show(position: Int) {
        if (currentPosition != position) {
            val beginTransaction = supportFragmentManager.beginTransaction()
            if (currentPosition != -1) {
                if (fragmentList[currentPosition].isAdded) {
                    beginTransaction.hide(fragmentList[currentPosition])
                    beginTransaction.setMaxLifecycle(fragmentList[currentPosition], Lifecycle.State.STARTED)
                    LogUtil.log(TAG, "hide  ${fragmentList[currentPosition]}")
                }
            }
            if (fragmentList[position].isAdded) {
                beginTransaction.show(fragmentList[position])
                LogUtil.log(TAG, "show")
            } else {
                beginTransaction.add(fragmentLayout, fragmentList[position])
                LogUtil.log(TAG, "add")
            }
            beginTransaction.setMaxLifecycle(fragmentList[position], Lifecycle.State.RESUMED)
            beginTransaction.commit()

            currentPosition = position
            LogUtil.log(TAG, "currentPosition $currentPosition")
        }
    }

    fun reset() {
        val beginTransaction = supportFragmentManager.beginTransaction()
        fragmentList.forEach {
            if (it.isAdded) {
                beginTransaction.remove(it)
                LogUtil.log(TAG, "remove")
            }
        }
        beginTransaction.commit()
        currentPosition = -1
    }
}