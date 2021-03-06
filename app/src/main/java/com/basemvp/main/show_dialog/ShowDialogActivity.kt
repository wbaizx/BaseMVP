package com.basemvp.main.show_dialog

import android.os.Bundle
import com.base.common.base.BaseActivity
import com.base.common.util.log
import com.basemvp.R
import kotlinx.android.synthetic.main.activity_show_dialog.*

class ShowDialogActivity : BaseActivity() {
    private val TAG = "ShowDialogActivity"

    private val dialogFragment1 = DefaultDialog(this)
    private val dialogFragment2 = BottomDialog(this)

    /**
     * 重建时Activity变量会重置
     * 重建时BottomDialog变量会重置
     * 重建时BottomDialog的tag不会重置
     * 只需要在show的时候记录一次tag的值就能在多次重建后找到当前dialog
     */
    companion object {
        private var tag2: String? = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //屏幕旋转的Fragment字段控制
        if (savedInstanceState != null) {
            val dialog = supportFragmentManager.findFragmentByTag(tag2) as? BottomDialog
            log(TAG, "savedInstanceState  $dialog")
            dialog?.setOnDismissListener {
                log(TAG, "OnDismiss")
            }
        }
    }

    override fun getContentView() = R.layout.activity_show_dialog

    override fun initView() {
        dialog1.setOnClickListener { dialogFragment1.showDialog() }
        dialog2.setOnClickListener {
            dialogFragment2.showDialog()
            tag2 = dialogFragment2.getDialogTag()
            log(TAG, "initView  $tag2")
        }


        dialogFragment2.setOnDismissListener {
            log(TAG, "OnDismiss")
        }
    }

    override fun initData() {
    }
}
