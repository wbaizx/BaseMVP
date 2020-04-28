package com.base.common.base

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.launcher.ARouter
import com.base.common.BaseAPP
import com.base.common.R
import com.base.common.util.LogUtil
import com.gyf.immersionbar.ImmersionBar
import pub.devrel.easypermissions.EasyPermissions


/**
 *  Activity基类
 */
abstract class BaseActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks, EasyPermissions.RationaleCallbacks {
    private val TAG = "BaseActivity"
    private val loadDialog by lazy { LoadDialog(this) }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //强制竖屏，这里用代码方式，有问题，首次打开页面可能并非竖屏导致闪屏
        //可以使用 xml 中 android:screenOrientation="portrait"，不过每个页面添加有点麻烦
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        BaseAPP.registerActivity(this)

        ARouter.getInstance().inject(this)

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
        BaseAPP.unregisterActivity(this)
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

    fun showLoadDialog() {
        if (!loadDialog.isShow) {
            LogUtil.log(TAG, "showLoadDialog")
            loadDialog.showDialog()
        }
    }

    fun hideLoadDialog() {
        if (loadDialog.isShow) {
            LogUtil.log(TAG, "hideLoadDialog")
            loadDialog.dismiss()
        }
    }

    /**
     * 权限允许后回调
     */
    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        LogUtil.log(TAG, "onPermissionsGranted")
    }

    /**
     * 权限拒绝后回调
     */
    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        LogUtil.log(TAG, "onPermissionsDenied")
    }

    /**
     * 权限拒绝过一次后的提示框被拒绝
     */
    override fun onRationaleDenied(requestCode: Int) {
        LogUtil.log(TAG, "onRationaleDenied")
    }

    /**
     * 权限拒绝过一次后的提示框被允许
     */
    override fun onRationaleAccepted(requestCode: Int) {
        LogUtil.log(TAG, "onRationaleAccepted")
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }
}