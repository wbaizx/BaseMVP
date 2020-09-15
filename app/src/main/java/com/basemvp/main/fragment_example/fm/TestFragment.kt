package com.basemvp.main.fragment_example.fm


import android.os.Bundle
import android.view.View
import com.base.common.base.BaseFragment
import com.base.common.util.log
import com.basemvp.R
import kotlinx.android.synthetic.main.fragment_test.*

/**
 * 测试Fragment用例
 */
class TestFragment(private val text: String) : BaseFragment() {
    private val TAG = "TestFragment"

    override fun getContentView() = R.layout.fragment_test

    override fun createView() {
        log(TAG, "createView  $text")
        testText.text = text
    }

    override fun onFirstVisible() {
        log(TAG, "onFirstVisible  $text")
    }

    override fun onVisible() {
        log(TAG, "onVisible  $text")
    }

    override fun onHide() {
        log(TAG, "onHide  $text")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        log(TAG, "onCreate  $text")
    }

    override fun onDestroy() {
        log(TAG, "onDestroy  $text")
        super.onDestroy()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        log(TAG, "onDestroyView  $text")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        log(TAG, "onViewCreated  $text")
    }

    override fun onStop() {
        super.onStop()
        log(TAG, "onStop  $text")
    }
}
