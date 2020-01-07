package com.basemvp.main.show_fragment

import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import com.basemvp.test.fm.TestFragment
import com.basemvp.test.fm.TestViewFragment
import com.basemvp.util.LogUtil

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
        TestViewFragment("MVP 5"),
        TestViewFragment("MVP 6"),
        TestViewFragment("MVP 7")
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
                beginTransaction.setMaxLifecycle(fragmentList[position], Lifecycle.State.RESUMED)
                LogUtil.log(TAG, "show")
            } else {
                beginTransaction.add(fragmentLayout, fragmentList[position])
                beginTransaction.setMaxLifecycle(fragmentList[position], Lifecycle.State.RESUMED)
                LogUtil.log(TAG, "add")
            }
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

        fragmentList = arrayListOf(
            TestFragment("1"),
            TestFragment("2"),
            TestFragment("3"),
            TestFragment("4"),
            TestViewFragment("MVP 5"),
            TestViewFragment("MVP 6"),
            TestViewFragment("MVP 7")
        )
    }
}