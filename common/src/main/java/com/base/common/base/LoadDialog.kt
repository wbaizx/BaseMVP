package com.base.common.base

import android.view.View
import androidx.fragment.app.FragmentActivity
import com.base.common.R
import com.base.common.base.dialog.BaseFragmentDialog

class LoadDialog : BaseFragmentDialog {
    constructor() : super()

    constructor(mActivity: FragmentActivity) : super() {
        this.mActivity = mActivity
    }

    override fun setDialogConfigure() {
        setCanceledOnTouchOutside(false)
        setCanceledOnBack(false)
    }

    override fun getLayout() = R.layout.load_dialog_view

    override fun initView(view: View) {
    }
}