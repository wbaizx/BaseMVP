package com.base.common.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

/**
 * Fragment基类，通过setMaxLifecycle控制生命周期
 * 屏幕旋转后 会重建 Fragment ，可能导致字段，变量重置，需要注意！
 */
abstract class BaseFragment : Fragment() {
    private var mView: View? = null
    private var isLoad = false
    private var isCreateView = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configure()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (mView == null) {
            mView = bindView(inflater, container)
        }
        return mView
    }

    protected open fun bindView(inflater: LayoutInflater, container: ViewGroup?): View {
        return inflater.inflate(getContentView(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!isCreateView) {
            isCreateView = true
            createView()
        }
    }

    override fun onResume() {
        super.onResume()
        if (!isLoad) {
            isLoad = true
            onFirstVisible()
        } else {
            onVisible()
        }
    }

    override fun onPause() {
        onHide()
        super.onPause()
    }

    /**
     * 有些操作必须在此位置实现的，可以根据需求覆写
     */
    protected open fun configure() {

    }

    protected abstract fun getContentView(): Int

    /**
     * view可用时调用，可在这里使用控件等
     */
    protected abstract fun createView()

    /**
     * Fragment第一次可见时调用，如果不是必须的初始化操作可以放在这里，避免多个fragment同时加载资源
     */
    protected abstract fun onFirstVisible()

    /**
     * Fragment每次可见时调用（第一次不调用）
     */
    protected abstract fun onVisible()

    /**
     * Fragment每次不可见时调用
     */
    protected abstract fun onHide()

    fun showLoadDialog() {
        (activity as? BaseActivity)?.showLoadDialog()
    }

    fun hideLoadDialog() {
        (activity as? BaseActivity)?.hideLoadDialog()
    }

    /**
     * 某些情况下需要继承此方法做后续操作，比如关闭下拉刷新状态
     * 注意mvvm下使用sharedViewModel共用viewModel，那么统一交给activity注册的基本监听接收，此方法不会回调
     */
    open fun runError(e: Exception) {
        (activity as? BaseActivity)?.runError(e)
    }

    /**
     * 对应资源销毁在onDestroy中，有时也要根据情况在onDestroyView或者onDetach中
     */
    override fun onDestroy() {
        super.onDestroy()
        if (mView?.parent != null) {
            (mView?.parent as ViewGroup).removeView(mView)
        }
        mView = null
        isLoad = false
        isCreateView = false
    }
}