package com.basemvp.main.show_dialog

import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.fragment.app.FragmentActivity
import com.basemvp.R
import com.base.common.base.dialog.BaseFragmentDialog
import kotlinx.android.synthetic.main.test_dialog_view.*

class BottomDialog : BaseFragmentDialog {

    constructor() : super()

    constructor(mActivity: FragmentActivity) : super() {
        this.mActivity = mActivity
    }

    override fun setDialogConfigure() {
        setCanceledOnTouchOutside(true)
        setCanceledOnBack(true)
    }

    override fun getLayout() = R.layout.test_dialog_view

    override fun setWindowConfigure(win: Window) {
        val params = win.attributes
        params.gravity = Gravity.BOTTOM
        params.width = WindowManager.LayoutParams.WRAP_CONTENT
        params.height = WindowManager.LayoutParams.WRAP_CONTENT
        win.attributes = params
    }

    override fun getStyleAnimations() = R.style.AnimUp

    override fun initView(view: View) {
        text.text = "底部"
        basedialog_cancel.setOnClickListener { dismiss() }
    }
}