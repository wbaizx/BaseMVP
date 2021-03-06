package com.basemvp.main.fragment_example.fm


import android.os.Bundle
import android.view.View
import com.base.common.base.mvp.BaseMVPFragment
import com.base.common.base.mvp.contract.BaseMVPPresenterI
import com.base.common.util.log
import com.basemvp.R
import kotlinx.android.synthetic.main.fragment_test_v.*

/**
 * 测试Fragment用例
 */
class TestMVPFragment(private val text: String) : BaseMVPFragment<BaseMVPPresenterI>() {
    private val TAG = "TestViewFragment"

    override var presenter: BaseMVPPresenterI? = null

    override fun getContentView() = R.layout.fragment_test_v

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
