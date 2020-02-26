package com.basemvp.base

import android.view.View
import androidx.fragment.app.FragmentActivity
import com.basemvp.R
import com.basemvp.util.dialog.BaseFragmentDialog

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