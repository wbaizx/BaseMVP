package com.basemvp.main.fragment_example.fm


import android.os.Bundle
import android.view.View
import com.base.common.base.BaseFragment
import com.base.common.util.LogUtil
import com.basemvp.R
import kotlinx.android.synthetic.main.fragment_test.*

/**
 * 测试Fragment用例
 */
class TestFragment(private val text: String) : BaseFragment() {
    private val TAG = "TestFragment"

    override fun getContentView() = R.layout.fragment_test

    override fun createView() {
        LogUtil.log(TAG, "createView  $text")
        testText.text = text
    }

    override fun onFirstVisible() {
        LogUtil.log(TAG, "onFirstVisible  $text")
    }

    override fun onVisible() {
        LogUtil.log(TAG, "onVisible  $text")
    }

    override fun onHide() {
        LogUtil.log(TAG, "onHide  $text")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LogUtil.log(TAG, "onCreate  $text")
    }

    override fun onDestroy() {
        LogUtil.log(TAG, "onDestroy  $text")
        super.onDestroy()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        LogUtil.log(TAG, "onDestroyView  $text")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        LogUtil.log(TAG, "onViewCreated  $text")
    }

    override fun onStop() {
        super.onStop()
        LogUtil.log(TAG, "onStop  $text")
    }
}
