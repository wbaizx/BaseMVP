package com.basemvp.main.show_dialog

import android.view.View
import androidx.fragment.app.FragmentActivity
import com.basemvp.R
import com.basemvp.util.dialog.BaseFragmentDialog
import kotlinx.android.synthetic.main.test_dialog_view.*

class DefaultDialog : BaseFragmentDialog {

    constructor() : super()

    constructor(mActivity: FragmentActivity) : super() {
        this.mActivity = mActivity
    }

    override fun setDialogConfigure() {
        setCanceledOnTouchOutside(true)
        setCanceledOnBack(true)
    }

    override fun getLayout() = R.layout.test_dialog_view

    override fun initView(view: View) {
        text.text = "默认"
        basedialog_cancel.setOnClickListener { dismiss() }
    }
}