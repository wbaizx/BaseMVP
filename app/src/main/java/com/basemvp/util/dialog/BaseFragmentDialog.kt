package com.basemvp.util.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.basemvp.R
import com.basemvp.util.LogUtil

/**
 * 屏幕旋转后 会重建 Fragment ，可能导致字段，变量重置，需要注意！
 */
abstract class BaseFragmentDialog : DialogFragment() {
    private val TAG = "BaseFragmentDialog"

    protected lateinit var mActivity: FragmentActivity

    private var onDismissListener: (() -> Unit)? = null

    /**
     * 是否是显示状态
     */
    var isShow = false

    /**
     * 点击返回键是否关闭
     */
    private var backCancel = true

    override fun onCreate(savedInstanceState: Bundle?) {
        LogUtil.log(TAG, "onCreate")
        super.onCreate(savedInstanceState)

        //屏幕旋转的字段控制
        if (savedInstanceState != null) {
            isShow = savedInstanceState.getBoolean("show", false)
        }
        setStyle(STYLE_NO_FRAME, getStyle())
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        LogUtil.log(TAG, "onCreateDialog")
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        LogUtil.log(TAG, "onCreateView")
        if (dialog != null) {
            LogUtil.log(TAG, "onCreateView dialog")
            dialog!!.setOnKeyListener { dialog, keyCode, event ->
                //点击返回键也不关闭
                (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN
                        && !backCancel)
            }
            setDialogConfigure()
        }
        return inflater.inflate(getLayout(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
        LogUtil.log(TAG, "onViewCreated")
    }

    override fun onStart() {
        LogUtil.log(TAG, "onStart")
        val win = dialog!!.window
        if (win != null) {
            setWindowConfigure(win)
            win.setWindowAnimations(getStyleAnimations())
        }
        super.onStart()
    }

    /**
     * 设置主题
     */
    protected open fun getStyle() = R.style.default_fragmentdialog_style


    /**
     * 配置dialog
     */
    protected abstract fun setDialogConfigure()

    /**
     * 设置布局
     */
    protected abstract fun getLayout(): Int

    /**
     * 配置Window
     */
    protected open fun setWindowConfigure(win: Window) {
        val params = win.attributes
        params.gravity = Gravity.CENTER
        params.width = WindowManager.LayoutParams.WRAP_CONTENT
        params.height = WindowManager.LayoutParams.WRAP_CONTENT
        win.attributes = params
    }

    /**
     * 设置进出动画
     * R.style.animate_dialog
     * <style name="animate_dialog">
     * <item name="android:windowEnterAnimation">@anim/dialog_enter</item>
     * <item name="android:windowExitAnimation">@anim/dialog_out</item>
     * </style>
     */
    protected open fun getStyleAnimations() = 0

    /**
     * 初始化布局
     */
    protected abstract fun initView(view: View)

    /**
     * 调用showDialog展示dialog，注意已经show了再调容易出错，可以用isShow标志控制
     */
    fun showDialog(tag: String) {
        val ft = mActivity.supportFragmentManager.beginTransaction()
        val prev = mActivity.supportFragmentManager.findFragmentByTag(tag)
        if (prev != null) {
            //这句话实际效果不行
            ft.remove(prev)
        }
        show(ft, tag)
        isShow = true
    }

    /**
     * 任何关闭的情况都会回调该方法，包括activity旋转重建也会回调该方法，但我们在activity旋转时不需要回调
     * 所以这个方法用不到
     */
    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
    }

    /**
     * 屏幕旋转的字段控制
     */
    override fun onSaveInstanceState(outState: Bundle) {
        LogUtil.log(TAG, "onSaveInstanceState")
        outState.putBoolean("show", isShow)
        super.onSaveInstanceState(outState)
    }

    /**
     * 点击外部或者返回键是会回调该方法
     */
    override fun onCancel(dialog: DialogInterface) {
        isShow = false
        super.onCancel(dialog)
        onDismissListener?.invoke()
    }

    /**
     * 主动调用dismiss才会回调该方法
     */
    override fun dismiss() {
        if (isShow) {
            isShow = false
            super.dismiss()
            onDismissListener?.invoke()
        }
    }

    /**
     * 设置点击外部是否关闭
     */
    fun setCanceledOnTouchOutside(outsideCancel: Boolean) {
        dialog?.setCanceledOnTouchOutside(outsideCancel)
    }

    /**
     * 设置点击返回键是否关闭
     */
    fun setCanceledOnBack(backCancel: Boolean) {
        this.backCancel = backCancel
    }

    /**
     * 设置关闭监听，只有主动关闭或者点击返回键或者点击外部才会调用
     */
    fun setOnDismissListener(onDismissListener: () -> Unit) {
        this.onDismissListener = onDismissListener
    }

    /**
     * 设置 FragmentActivity 必须设置，也可以通过子类添加构造方法在子类中配置
     */
    fun setContext(mActivity: FragmentActivity) {
        this.mActivity = mActivity
    }
}
