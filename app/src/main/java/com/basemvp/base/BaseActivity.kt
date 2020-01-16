package com.basemvp.base

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.basemvp.APP
import com.basemvp.R
import com.gyf.immersionbar.ImmersionBar

/**
 *  Activity基类
 */
abstract class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        APP.registerActivity(this)

        configure()

        setContentView(getContentView())

        setImmersionBar()

        initView()
        initData()
    }

    /**
     * 有些操作需要在此位置实现的，可以根据需求覆写
     */
    protected open fun configure() {

    }

    protected abstract fun getContentView(): Int

    protected open fun setImmersionBar() {
        ImmersionBar.with(this)
            .statusBarColor(R.color.color_336)
            .fitsSystemWindows(true)
            .init()
    }

    protected abstract fun initView()

    protected abstract fun initData()

    override fun onDestroy() {
        super.onDestroy()
        APP.unregisterActivity(this)
    }

    /**
     * 关闭软键盘
     */
    fun closeSoftInput() {
        val aa = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        aa.hideSoftInputFromWindow(findViewById<View>(android.R.id.content).windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    /**
     * 打开软键盘
     */
    fun openSoftInput(ed: EditText) {
        ed.requestFocus()
        val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(ed, InputMethodManager.SHOW_IMPLICIT)
    }
}