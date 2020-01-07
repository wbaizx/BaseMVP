package com.basemvp.main.show_dialog

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.basemvp.R
import com.basemvp.base.BaseActivity
import com.basemvp.config.RouteString
import com.basemvp.util.LogUtil
import kotlinx.android.synthetic.main.activity_show_dialog.*

@Route(path = RouteString.DIALOG, name = "展示FragmentDialog")
class ShowDialogActivity : BaseActivity() {
    private val TAG = "ShowDialogActivity"

    private val dialogFragment1 = DefaultDialog(this)
    private val dialogFragment2 = BottomDialog(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //屏幕旋转的Fragment字段控制
        if (savedInstanceState != null) {
            val dialog = supportFragmentManager.findFragmentByTag("2") as? BottomDialog
            LogUtil.log(TAG, "savedInstanceState  $dialog")
            dialog?.setOnDismissListener {
                LogUtil.log(TAG, "OnDismiss")
            }
        }
    }

    override fun getContentView() = R.layout.activity_show_dialog

    override fun initView() {
        dialog1.setOnClickListener { dialogFragment1.showDialog("1") }
        dialog2.setOnClickListener { dialogFragment2.showDialog("2") }

        dialogFragment2.setOnDismissListener {
            LogUtil.log(TAG, "OnDismiss")
        }
    }

    override fun initData() {
    }
}
