package com.basemvp.test.fm


import android.os.Bundle
import android.view.View
import com.basemvp.R
import com.basemvp.base.mvp.BaseViewFragment
import com.basemvp.util.LogUtil
import kotlinx.android.synthetic.main.fragment_test_v.*

/**
 * 测试Fragment用例
 */
class TestViewFragment(private val text: String) : BaseViewFragment<Any>() {
    private val TAG = "TestViewFragment"

    override fun initBasePresenter(): Any? = null

    override fun getContentView() = R.layout.fragment_test_v

    override fun createView() {
        LogUtil.log(TAG, "createView  $text")
        testText.text = text
    }

    override fun onFristVisible() {
        LogUtil.log(TAG, "onFristVisible  $text")
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
